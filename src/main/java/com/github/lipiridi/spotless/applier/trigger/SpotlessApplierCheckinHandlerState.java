package com.github.lipiridi.spotless.applier.trigger;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(name = "SpotlessApplierCheckinHandler", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class SpotlessApplierCheckinHandlerState
        implements PersistentStateComponent<SpotlessApplierCheckinHandlerState> {

    public boolean preCommitSpotlessFormating;

    public static SpotlessApplierCheckinHandlerState getInstance(Project project) {
        return project.getService(SpotlessApplierCheckinHandlerState.class);
    }

    @Override
    public SpotlessApplierCheckinHandlerState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SpotlessApplierCheckinHandlerState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
