package com.lipiridi.spotless.applier.ui;

import javax.swing.*;

public class SpotlessConfigPanel extends JPanel {

    private final JCheckBox isOptimizeImportsBeforeApplyingCheckBox;

    public SpotlessConfigPanel() {
        // Initialize the checkbox
        isOptimizeImportsBeforeApplyingCheckBox = new JCheckBox("Optimize imports before applying");
        // Add any additional components or layout as needed
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(isOptimizeImportsBeforeApplyingCheckBox);
    }

    public boolean isOptimizeImportsBeforeApplying() {
        // Return the state of the checkbox (true if selected, false otherwise)
        return isOptimizeImportsBeforeApplyingCheckBox.isSelected();
    }
}
