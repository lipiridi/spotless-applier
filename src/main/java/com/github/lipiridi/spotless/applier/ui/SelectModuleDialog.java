package com.github.lipiridi.spotless.applier.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class SelectModuleDialog extends DialogWrapper {

    private final CheckBoxList<String> moduleList = new CheckBoxList<>();
    private final Collection<String> moduleNames;
    private final JBCheckBox applyOnRootProject = new JBCheckBox("Apply on root project");
    private final boolean isProjectAlsoModule;

    public SelectModuleDialog(Collection<String> moduleNames, boolean isProjectAlsoModule) {
        super(true); // use current window as parent
        setTitle("Select Modules for Spotless Applying");
        this.moduleNames = moduleNames;
        this.isProjectAlsoModule = isProjectAlsoModule;
        init();
    }

    @Nullable @Override
    protected JComponent createCenterPanel() {
        moduleNames.forEach(moduleName -> moduleList.addItem(moduleName, moduleName, false));

        JPanel dialogPanel = new JPanel(new BorderLayout());

        // Create a panel to hold the applyOnRootProject checkbox
        if (isProjectAlsoModule) {
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(applyOnRootProject);
            dialogPanel.add(topPanel, BorderLayout.NORTH);
        }

        JScrollPane scrollPane = new JBScrollPane(moduleList);
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
            applyOnRootProject.addChangeListener(e -> {
                boolean selected = applyOnRootProject.isSelected();
                moduleList.setEnabled(!selected);
                selectAllButton.setEnabled(!selected);
                unselectAllButton.setEnabled(!selected);
            });
        }

        return dialogPanel;
    }

    public List<String> getSelectedModules() {
        return moduleNames.stream().filter(moduleList::isItemSelected).toList();
    }

    public boolean isApplyOnRootProject() {
        return applyOnRootProject.isSelected();
    }

    private void setSelectedAll(boolean selected) {
        moduleNames.forEach(item -> moduleList.setItemSelected(item, selected));
        moduleList.repaint();
    }
}
