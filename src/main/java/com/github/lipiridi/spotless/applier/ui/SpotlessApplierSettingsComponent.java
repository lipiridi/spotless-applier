package com.github.lipiridi.spotless.applier.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;

public class SpotlessApplierSettingsComponent {

    private final JPanel myMainPanel;
    private final JCheckBox prohibitGradleCacheCheckBox = new JBCheckBox("Prohibit Gradle cache for 'apply' task");
    private final JCheckBox optimizeImportsBeforeApplyingCheckBox = new JBCheckBox("Optimize imports before applying");
    private final JCheckBox prohibitImportsWithAsteriskCheckBox = new JBCheckBox("Prohibit imports with asterisk '*'");

    public SpotlessApplierSettingsComponent() {
        // Add a listener to the first checkbox to toggle the visibility of the second checkbox
        optimizeImportsBeforeApplyingCheckBox.addActionListener(e -> {
            boolean selected = optimizeImportsBeforeApplyingCheckBox.isSelected();
            prohibitImportsWithAsteriskCheckBox.setEnabled(selected);
            if (!selected) {
                prohibitImportsWithAsteriskCheckBox.setSelected(false);
            }
        });

        JPanel secondRow = FormBuilder.createFormBuilder()
                .setFormLeftIndent(20)
                .addComponent(prohibitImportsWithAsteriskCheckBox)
                .getPanel();

        prohibitGradleCacheCheckBox.setToolTipText(
                "Starting from version 6.0.0, Spotless supports Gradle's configuration cache. If you want to use it, please enable this checkbox.");

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(prohibitGradleCacheCheckBox)
                .addComponent(optimizeImportsBeforeApplyingCheckBox)
                .addComponent(secondRow)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public boolean isProhibitGradleCache() {
        return prohibitGradleCacheCheckBox.isSelected();
    }

    public void setProhibitGradleCache(boolean value) {
        prohibitGradleCacheCheckBox.setSelected(value);
    }

    public boolean isOptimizeImportsBeforeApplying() {
        return optimizeImportsBeforeApplyingCheckBox.isSelected();
    }

    public void setOptimizeImportsBeforeApplying(boolean value) {
        optimizeImportsBeforeApplyingCheckBox.setSelected(value);
        // Set the default state
        prohibitImportsWithAsteriskCheckBox.setEnabled(optimizeImportsBeforeApplyingCheckBox.isSelected());
    }

    public boolean isProhibitAsteriskImports() {
        return prohibitImportsWithAsteriskCheckBox.isSelected();
    }

    public void setProhibitAsteriskImports(boolean value) {
        prohibitImportsWithAsteriskCheckBox.setSelected(value);
    }
}
