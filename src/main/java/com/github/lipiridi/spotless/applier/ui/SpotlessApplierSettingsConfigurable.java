package com.github.lipiridi.spotless.applier.ui;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SpotlessApplierSettingsConfigurable implements Configurable {

    private SpotlessApplierSettingsComponent spotlessApplierSettingsComponent;

    public SpotlessApplierSettingsConfigurable() {
        this.spotlessApplierSettingsComponent = new SpotlessApplierSettingsComponent();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Spotless Applier";
    }

    @Override
    public @Nullable JComponent createComponent() {
        spotlessApplierSettingsComponent = new SpotlessApplierSettingsComponent();
        return spotlessApplierSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        SpotlessApplierSettingsState settings = SpotlessApplierSettingsState.getInstance();
        return spotlessApplierSettingsComponent.isOptimizeImportsBeforeApplying()
                != settings.optimizeImportsBeforeApplying;
    }

    @Override
    public void apply() {
        SpotlessApplierSettingsState settings = SpotlessApplierSettingsState.getInstance();
        settings.optimizeImportsBeforeApplying = spotlessApplierSettingsComponent.isOptimizeImportsBeforeApplying();
    }

    @Override
    public void reset() {
        SpotlessApplierSettingsState settings = SpotlessApplierSettingsState.getInstance();
        spotlessApplierSettingsComponent.setOptimizeImportsBeforeApplying(settings.optimizeImportsBeforeApplying);
    }
}
