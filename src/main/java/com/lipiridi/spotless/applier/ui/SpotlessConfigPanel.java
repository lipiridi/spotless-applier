package com.lipiridi.spotless.applier.ui;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public class SpotlessConfigPanel extends JPanel {

    private final JCheckBox isOptimizeImportsBeforeApplyingCheckBox;

    public SpotlessConfigPanel() {
        // Initialize the checkbox
        isOptimizeImportsBeforeApplyingCheckBox = new JCheckBox("Optimize imports before applying");
        // Add any additional components or layout as needed
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(isOptimizeImportsBeforeApplyingCheckBox);

        // Load the checkbox state from the settings
        loadSettings();
    }

    private void loadSettings() {
        SpotlessConfig spotlessConfig = ServiceManager.getService(SpotlessConfig.class);
        boolean optimizeImports = spotlessConfig.isOptimizeImportsBeforeApplying();
        isOptimizeImportsBeforeApplyingCheckBox.setSelected(optimizeImports);
    }

    public boolean isOptimizeImportsBeforeApplying() {
        // Return the state of the checkbox (true if selected, false otherwise)
        return isOptimizeImportsBeforeApplyingCheckBox.isSelected();
    }
}
