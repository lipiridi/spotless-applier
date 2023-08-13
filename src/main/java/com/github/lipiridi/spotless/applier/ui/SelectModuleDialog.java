package com.github.lipiridi.spotless.applier.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class SelectModuleDialog extends DialogWrapper {

    private final CheckBoxList<String> moduleList = new CheckBoxList<>();
    private final Collection<String> moduleNames;

    public SelectModuleDialog(Collection<String> moduleNames) {
        super(true); // use current window as parent
        setTitle("Select Modules for Spotless Applying");
        this.moduleNames = moduleNames;
        init();
    }

    @Nullable @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        moduleNames.forEach(moduleName -> moduleList.addItem(moduleName, moduleName, true));

        JScrollPane scrollPane = new JBScrollPane(moduleList);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton selectAllButton = new JButton("Select All");
        JButton unselectAllButton = new JButton("Unselect All");

        selectAllButton.addActionListener(e -> setSelectedAll(true));
        unselectAllButton.addActionListener(e -> setSelectedAll(false));

        buttonPanel.add(selectAllButton);
        buttonPanel.add(unselectAllButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        return dialogPanel;
    }

    public List<String> getSelectedModules() {
        return moduleNames.stream().filter(moduleList::isItemSelected).toList();
    }

    private void setSelectedAll(boolean selected) {
        moduleNames.forEach(item -> moduleList.setItemSelected(item, selected));
        moduleList.repaint();
    }
}
