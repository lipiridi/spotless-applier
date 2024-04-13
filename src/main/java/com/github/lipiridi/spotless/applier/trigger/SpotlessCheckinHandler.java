package com.github.lipiridi.spotless.applier.trigger;

import com.github.lipiridi.spotless.applier.ModuleInfo;
import com.github.lipiridi.spotless.applier.ReformatProcessor;
import com.github.lipiridi.spotless.applier.ui.settings.SpotlessApplierSettingsState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.PairConsumer;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class SpotlessCheckinHandler extends CheckinHandler {
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
            new ReformatProcessor(project, findRootModule()).run();
            return ReturnResult.COMMIT;
        } catch (Exception e) {
            handleError(e);
            return ReturnResult.CANCEL;
        }
    }

    private ModuleInfo findRootModule() {
        Module[] modules = ProjectUtil.getModules(project);
        String projectBasePath = project.getBasePath();

        return Arrays.stream(modules)
                .map(module -> ModuleInfo.create(project, projectBasePath, module))
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
}
