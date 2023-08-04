package com.lipiridi.spotless.applier.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpotlessConfigurable implements Configurable {

    private final SpotlessConfigPanel spotlessConfigPanel;

    public SpotlessConfigurable() {
        this.spotlessConfigPanel = new SpotlessConfigPanel();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Spotless Applier";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return spotlessConfigPanel;
    }

    @Override
    public boolean isModified() {
        boolean originalValue = getSpotlessConfig().isOptimizeImportsBeforeApplying();
        boolean modifiedValue = spotlessConfigPanel.isOptimizeImportsBeforeApplying();
        return originalValue != modifiedValue;
    }

    @Override
    public void apply() {
        boolean optimizeImports = spotlessConfigPanel.isOptimizeImportsBeforeApplying();
        getSpotlessConfig().setOptimizeImportsBeforeApplying(optimizeImports);
    }

    private SpotlessConfig getSpotlessConfig() {
        return ServiceManager.getService(SpotlessConfig.class);
    }
}
