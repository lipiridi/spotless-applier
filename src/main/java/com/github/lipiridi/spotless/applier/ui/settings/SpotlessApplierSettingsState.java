package com.github.lipiridi.spotless.applier.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@Service
@State(
        name = "com.github.lipiridi.spotless.applier.ui.settings.SpotlessSettingsState",
        storages = {@Storage("spotless-applier.xml")})
public final class SpotlessApplierSettingsState implements PersistentStateComponent<SpotlessApplierSettingsState> {

    public boolean prohibitGradleCache;
    public boolean optimizeImportsBeforeApplying;
    public boolean prohibitImportsWithAsterisk;

    public static SpotlessApplierSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SpotlessApplierSettingsState.class);
    }

    @Override
    public SpotlessApplierSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpotlessApplierSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
