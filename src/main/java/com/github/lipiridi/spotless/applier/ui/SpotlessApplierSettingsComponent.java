package com.github.lipiridi.spotless.applier.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class SpotlessApplierSettingsComponent {

    private final JPanel myMainPanel;
    private final JCheckBox isOptimizeImportsBeforeApplyingCheckBox =
            new JBCheckBox("Optimize imports before applying");

    public SpotlessApplierSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(isOptimizeImportsBeforeApplyingCheckBox)
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
    }
}
