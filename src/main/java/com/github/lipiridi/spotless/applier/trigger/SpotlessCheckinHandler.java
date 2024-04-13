package com.github.lipiridi.spotless.applier.trigger;

import com.github.lipiridi.spotless.applier.ModuleInfo;
import com.github.lipiridi.spotless.applier.ReformatProcessor;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.ui.NonFocusableCheckBox;
import com.intellij.util.PairConsumer;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class SpotlessCheckinHandler extends CheckinHandler {
    private static final Logger LOGGER = Logger.getInstance(SpotlessCheckinHandler.class);
    private static final String ACTIVATED_OPTION_NAME = "SPOTLESS_PRECOMMIT_FORMATTING";

    private final Project project;
    private JCheckBox checkBox;

    public SpotlessCheckinHandler(Project project) {
        this.project = project;
    }

    @Override
    @Nullable public RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
        this.checkBox = new NonFocusableCheckBox("Reformat code with Spotless");
        return new SpotlessRefreshableOnComponent(checkBox);
    }

    @Override
    public ReturnResult beforeCheckin(
            @Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
        if (checkBox != null && !checkBox.isSelected()) {
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
        Messages.showErrorDialog(project, msg, "Error reformatting code with Spotless");
    }

    private class SpotlessRefreshableOnComponent implements RefreshableOnComponent, UnnamedConfigurable {
        private final JCheckBox checkBox;

        private SpotlessRefreshableOnComponent(JCheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public JComponent getComponent() {
            var panel = new JPanel(new BorderLayout());
            panel.add(checkBox);
            return panel;
        }

        @Override
        public void saveState() {
            PropertiesComponent.getInstance(project)
                    .setValue(ACTIVATED_OPTION_NAME, Boolean.toString(checkBox.isSelected()));
        }

        @Override
        public void restoreState() {
            checkBox.setSelected(getSavedStateOrDefault());
        }

        private boolean getSavedStateOrDefault() {
            var props = PropertiesComponent.getInstance(project);
            return props.getBoolean(ACTIVATED_OPTION_NAME);
        }

        @Override
        public @Nullable JComponent createComponent() {
            return getComponent();
        }

        @Override
        public boolean isModified() {
            return checkBox.isSelected() != getSavedStateOrDefault();
        }

        @Override
        public void apply() {
            saveState();
        }

        @Override
        public void reset() {
            restoreState();
        }
    }
}
