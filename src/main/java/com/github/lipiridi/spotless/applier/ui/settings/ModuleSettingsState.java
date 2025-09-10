package com.github.lipiridi.spotless.applier.ui.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(name = "com.github.lipiridi.spotless.applier.ui.settings.ModuleSettingsState")
public final class ModuleSettingsState implements PersistentStateComponent<ModuleSettingsState> {

    public List<String> selectedModules = new ArrayList<>();
    public boolean applyOnRootProject;

    @Override
    public ModuleSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ModuleSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
