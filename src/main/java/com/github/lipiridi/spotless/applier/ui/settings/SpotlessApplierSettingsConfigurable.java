package com.github.lipiridi.spotless.applier.ui.settings;

import com.github.lipiridi.spotless.applier.ui.SpotlessApplierSettingsComponent;
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

        return spotlessApplierSettingsComponent.isAllowGradleCache() != settings.allowGradleCache
                ^ spotlessApplierSettingsComponent.isOptimizeImportsBeforeApplying()
                        != settings.optimizeImportsBeforeApplying
                ^ spotlessApplierSettingsComponent.isProhibitAsteriskImports() != settings.prohibitImportsWithAsterisk;
    }

    @Override
    public void apply() {
        SpotlessApplierSettingsState settings = SpotlessApplierSettingsState.getInstance();
        settings.allowGradleCache = spotlessApplierSettingsComponent.isAllowGradleCache();
        settings.optimizeImportsBeforeApplying = spotlessApplierSettingsComponent.isOptimizeImportsBeforeApplying();
        settings.prohibitImportsWithAsterisk = spotlessApplierSettingsComponent.isProhibitAsteriskImports();
    }

    @Override
    public void reset() {
        SpotlessApplierSettingsState settings = SpotlessApplierSettingsState.getInstance();
        spotlessApplierSettingsComponent.setAllowGradleCache(settings.allowGradleCache);
        spotlessApplierSettingsComponent.setOptimizeImportsBeforeApplying(settings.optimizeImportsBeforeApplying);
        spotlessApplierSettingsComponent.setProhibitAsteriskImports(settings.prohibitImportsWithAsterisk);
    }
}
