package com.github.lipiridi.spotless.applier.trigger;

import com.github.lipiridi.spotless.applier.ModuleInfo;
import com.github.lipiridi.spotless.applier.ReformatProcessor;
import com.github.lipiridi.spotless.applier.ui.settings.SpotlessApplierSettingsState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CommitCheck;
import com.intellij.openapi.vcs.checkin.CommitInfo;
import com.intellij.openapi.vcs.checkin.CommitProblem;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PairConsumer;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.swing.*;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class SpotlessCheckinHandler extends CheckinHandler implements CommitCheck {
    private static final Logger LOGGER = Logger.getInstance(SpotlessCheckinHandler.class);
    private final SpotlessApplierSettingsState spotlessSettings = SpotlessApplierSettingsState.getInstance();
    private final Project project;

    public SpotlessCheckinHandler(Project project) {
        this.project = project;
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

        try {
            ModuleInfo rootModule = findRootModule();
            if (rootModule == null) {
                Messages.showWarningDialog(
                        project, "No root project was found", "Error Reformatting Code with Spotless");
            } else {
                new ReformatProcessor(project, rootModule).run();
            }
            return ReturnResult.COMMIT;
        } catch (Exception e) {
            handleError(e);
            return ReturnResult.CANCEL;
        }
    }

    private ModuleInfo findRootModule() {
        Module[] modules = ProjectUtil.getModules(project);

        return Arrays.stream(modules)
                .map(module -> ModuleInfo.create(project, module))
                .filter(Objects::nonNull)
                .filter(ModuleInfo::rootModule)
                .findFirst()
                .orElse(null);
    }

    private void handleError(Exception e) {
        var msg = "Error while reformatting code with Spotless";
        if (e.getMessage() != null) {
            msg = msg + ": " + e.getMessage();
        }
        LOGGER.info(msg, e);
        Messages.showErrorDialog(project, msg, "Error Reformatting Code with Spotless");
    }

    @NotNull @Override
    public ExecutionOrder getExecutionOrder() {
        return CommitCheck.ExecutionOrder.MODIFICATION;
    }

    @Override
    public boolean isEnabled() {
        return spotlessSettings.preCommitSpotlessFormating;
    }

    @Nullable @Override
    public Object runCheck(@NotNull CommitInfo commitInfo, @NotNull Continuation<? super CommitProblem> continuation) {
        var affectedFiles =
                ChangesUtil.iterateFiles(commitInfo.getCommittedChanges()).toList();

        Set<ModuleInfo> moduleInfos = new HashSet<>();
        Set<Module> modules = new HashSet<>();
        for (VirtualFile affectedFile : affectedFiles) {
            Module moduleForFile = ModuleUtil.findModuleForFile(affectedFile, project);
            if (!modules.add(moduleForFile)) {
                continue;
            }

            ModuleInfo moduleInfo = ModuleInfo.create(project, moduleForFile);
            if (moduleInfo == null) {
                continue;
            }

            if (moduleInfo.rootModule()) {
                new ReformatProcessor(project, moduleInfo).run();
                return null;
            }

            moduleInfos.add(moduleInfo);
        }

        moduleInfos.forEach(module -> new ReformatProcessor(project, module).run());
        return null;
    }
}
