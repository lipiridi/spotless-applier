// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the
// Apache 2.0 license.

package com.github.lipiridi.spotless.applier.onSave;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
@State(
        name = "SpotlessOnSaveOptions",
        storages = {@Storage("spotless-applier.xml")})
public final class SpotlessOnSaveOptions extends SpotlessOnSaveOptionsBase<SpotlessOnSaveOptions.State>
        implements PersistentStateComponent<SpotlessOnSaveOptions.State>, Cloneable {

    public static @NotNull SpotlessOnSaveOptions getInstance(@NotNull Project project) {
        return project.getService(SpotlessOnSaveOptions.class);
    }

    static final class State extends SpotlessOnSaveOptionsBase.StateBase implements Cloneable {
        State() {
            super(DefaultsProvider::getFileTypesFormattedOnSaveByDefault);
        }

        @Override
        public State clone() {
            return (State) super.clone();
        }
    }

    private final @NotNull Project myProject;

    public SpotlessOnSaveOptions(@NotNull Project project) {
        super(new State());
        myProject = project;
    }

    @Override
    protected void convertOldProperties() {
        String oldFormatOnSaveProperty = "spotless.format.on.save";
        boolean formatAllOld = PropertiesComponent.getInstance(myProject).getBoolean(oldFormatOnSaveProperty);
        if (formatAllOld) {
            setRunOnSaveEnabled(true);
            setRunForAllFileTypes();
        }
        PropertiesComponent.getInstance(myProject).unsetValue(oldFormatOnSaveProperty);
    }

    @Override
    public SpotlessOnSaveOptions clone() {
        return (SpotlessOnSaveOptions) super.clone();
    }
}
