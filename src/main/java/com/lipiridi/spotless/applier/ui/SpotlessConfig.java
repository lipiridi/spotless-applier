package com.lipiridi.spotless.applier.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.NotNull;

public class SpotlessConfig implements PersistentStateComponent<SpotlessConfig.State> {

    public static class State {
        // Define your custom settings as instance variables
        public boolean optimizeImportsBeforeApplying;
        // Add more settings as needed
    }

    private State myState = new State();

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    // Add getters and setters for each setting
    public boolean isOptimizeImportsBeforeApplying() {
        return myState.optimizeImportsBeforeApplying;
    }

    public void setOptimizeImportsBeforeApplying(boolean optimizeImportsBeforeApplying) {
        myState.optimizeImportsBeforeApplying = optimizeImportsBeforeApplying;
    }
}
