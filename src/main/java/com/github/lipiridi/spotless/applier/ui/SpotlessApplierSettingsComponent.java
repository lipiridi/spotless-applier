package com.github.lipiridi.spotless.applier.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class SpotlessApplierSettingsComponent {

    private final JPanel myMainPanel;
    private final JCheckBox isOptimizeImportsBeforeApplyingCheckBox =
            new JBCheckBox("Optimize imports before applying");
    private final JCheckBox isProhibitImportsWithAsteriskCheckBox =
            new JBCheckBox("Prohibit imports with asterisk '*'");

    public SpotlessApplierSettingsComponent() {
        // Add a listener to the first checkbox to toggle the visibility of the second checkbox
        isOptimizeImportsBeforeApplyingCheckBox.addActionListener(e -> {
            boolean selected = isOptimizeImportsBeforeApplyingCheckBox.isSelected();
            isProhibitImportsWithAsteriskCheckBox.setEnabled(selected);
            if (!selected) {
                isProhibitImportsWithAsteriskCheckBox.setSelected(false);
            }
        });

        JPanel secondRow = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addComponent(isProhibitImportsWithAsteriskCheckBox)
                .getPanel();

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(isOptimizeImportsBeforeApplyingCheckBox)
                .addComponent(secondRow)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public boolean isOptimizeImportsBeforeApplying() {
        return isOptimizeImportsBeforeApplyingCheckBox.isSelected();
    }

    public void setOptimizeImportsBeforeApplying(boolean value) {
        isOptimizeImportsBeforeApplyingCheckBox.setSelected(value);
        // Set the default state
        isProhibitImportsWithAsteriskCheckBox.setEnabled(isOptimizeImportsBeforeApplyingCheckBox.isSelected());
    }

    public boolean isProhibitAsteriskImports() {
        return isProhibitImportsWithAsteriskCheckBox.isSelected();
    }

    public void setProhibitAsteriskImports(boolean value) {
        isProhibitImportsWithAsteriskCheckBox.setSelected(value);
    }
}
