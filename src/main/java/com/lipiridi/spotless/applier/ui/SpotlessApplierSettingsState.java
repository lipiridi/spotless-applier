package com.lipiridi.spotless.applier.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
@State(name = "com.lipiridi.spotless.applier.ui.SpotlessSettingsState", storages = {@Storage("spotless-applier.xml")})
public final class SpotlessApplierSettingsState implements PersistentStateComponent<SpotlessApplierSettingsState> {

    public boolean optimizeImportsBeforeApplying;

    public static SpotlessApplierSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(SpotlessApplierSettingsState.class);
    }

    @Nullable
    @Override
    public SpotlessApplierSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpotlessApplierSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
