package com.github.lipiridi.spotless.applier.ui;

import com.github.lipiridi.spotless.applier.ui.settings.ModuleSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class SelectModuleDialog extends DialogWrapper {

    private final ModuleSettingsState moduleSettingsState;
    private final CheckBoxList<String> moduleCheckBoxList = new CheckBoxList<>();
    private final Collection<String> moduleNames;
    private final JBCheckBox applyOnRootProjectCheckbox = new JBCheckBox("Root project");
    private final boolean isProjectAlsoModule;

    public SelectModuleDialog(Project project, Collection<String> moduleNames, boolean isProjectAlsoModule) {
        super(true); // use current window as parent
        setTitle("Select Modules for Spotless Applying");
        this.moduleSettingsState = project.getService(ModuleSettingsState.class);
        this.moduleNames = moduleNames;
        this.moduleCheckBoxList.setStringItems(
                moduleNames.stream().collect(Collectors.toMap(Function.identity(), e -> false)));
        this.isProjectAlsoModule = isProjectAlsoModule;

        restoreModuleSettings();
        init();
    }

    @Nullable @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        // Create a panel to hold the applyOnRootProject checkbox
        if (isProjectAlsoModule) {
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(applyOnRootProjectCheckbox);
            dialogPanel.add(topPanel, BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JBScrollPane(moduleCheckBoxList);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllButton = new JButton("Select All");
        JButton unselectAllButton = new JButton("Unselect All");

        selectAllButton.addActionListener(e -> setSelectedAll(true));
        unselectAllButton.addActionListener(e -> setSelectedAll(false));

        buttonPanel.add(selectAllButton);
        buttonPanel.add(unselectAllButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (isProjectAlsoModule) {
            changeModuleListAvailability(selectAllButton, unselectAllButton);
            applyOnRootProjectCheckbox.addChangeListener(
                    e -> changeModuleListAvailability(selectAllButton, unselectAllButton));
        }

        return dialogPanel;
    }

    private void changeModuleListAvailability(JButton selectAllButton, JButton unselectAllButton) {
        boolean selected = applyOnRootProjectCheckbox.isSelected();
        moduleCheckBoxList.setEnabled(!selected);
        selectAllButton.setEnabled(!selected);
        unselectAllButton.setEnabled(!selected);
    }

    public void saveModuleSettings() {
        moduleSettingsState.applyOnRootProject = applyOnRootProjectCheckbox.isSelected();
        moduleSettingsState.selectedModules = getSelectedModules();
    }

    public List<String> getSelectedModules() {
        return moduleNames.stream().filter(moduleCheckBoxList::isItemSelected).toList();
    }

    public boolean getApplyOnRootProjectCheckbox() {
        return applyOnRootProjectCheckbox.isSelected();
    }

    private void setSelectedAll(boolean selected) {
        moduleNames.forEach(item -> moduleCheckBoxList.setItemSelected(item, selected));
        moduleCheckBoxList.repaint();
    }

    private void restoreModuleSettings() {
        applyOnRootProjectCheckbox.setSelected(moduleSettingsState.applyOnRootProject);
        moduleSettingsState.selectedModules.forEach(item -> moduleCheckBoxList.setItemSelected(item, true));
    }
}
