package com.github.lipiridi.spotless.applier.trigger;

import com.github.lipiridi.spotless.applier.ModuleInfo;
import com.github.lipiridi.spotless.applier.ReformatProcessor;
import com.github.lipiridi.spotless.applier.enums.BuildTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PairConsumer;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class SpotlessCheckinHandler extends CheckinHandler {
    private static final Logger LOGGER = Logger.getInstance(SpotlessCheckinHandler.class);
    private final SpotlessApplierCheckinHandlerState spotlessSettings;
    private final Project project;

    public SpotlessCheckinHandler(Project project) {
        this.project = project;
        this.spotlessSettings = SpotlessApplierCheckinHandlerState.getInstance(project);
    }

    @Override
    @Nullable public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        return new BooleanCommitOption(
                        project,
                        "Reformat code with Spotless",
                        true,
                        () -> spotlessSettings.preCommitSpotlessFormating,
                        (val) -> spotlessSettings.preCommitSpotlessFormating = val)
                .withCheckinHandler(this);
    }

    @Override
    public ReturnResult beforeCheckin(
            @Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (!spotlessSettings.preCommitSpotlessFormating) {
            return ReturnResult.COMMIT;
        }

        Set<ModuleInfo> affectedModules = findAffectedModules();

        try {
            affectedModules.forEach(module -> new ReformatProcessor(project, module).run());
            return ReturnResult.COMMIT;
        } catch (Exception e) {
            handleError(e);
            return ReturnResult.CANCEL;
        }
    }

    private void handleError(Exception e) {
        var msg = "Error while reformatting code with Spotless";
        if (e.getMessage() != null) {
            msg = msg + ": " + e.getMessage();
        }
        LOGGER.info(msg, e);
        Messages.showErrorDialog(project, msg, "Error Reformatting Code with Spotless");
    }

    public Set<ModuleInfo> findAffectedModules() {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        List<VirtualFile> affectedFiles = changeListManager.getAllChanges().stream()
                .filter(change -> change.getType() != Change.Type.DELETED)
                .map(Change::getVirtualFile)
                .filter(Objects::nonNull)
                .toList();

        Set<ModuleInfo> moduleInfos = new HashSet<>();
        Set<Module> modules = new HashSet<>();
        for (VirtualFile affectedFile : affectedFiles) {
            Module moduleForFile = ModuleUtil.findModuleForFile(affectedFile, project);
            if (modules.add(moduleForFile)) {
                ModuleInfo moduleInfo = ModuleInfo.create(project, moduleForFile);
                if (moduleInfo == null) {
                    continue;
                }

                // Commiting is a synchronous flow. Currently, gradle has issues with that
                // https://youtrack.jetbrains.com/issue/IDEA-327879
                if (moduleInfo.buildTool() == BuildTool.GRADLE) {
                    continue;
                }

                if (moduleInfo.rootModule()) {
                    return Set.of(moduleInfo);
                }

                moduleInfos.add(moduleInfo);
            }
        }

        return moduleInfos;
    }
}
