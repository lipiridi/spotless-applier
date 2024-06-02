package com.github.lipiridi.spotless.applier.config;

import com.intellij.ide.actionsOnSave.ActionOnSaveContext;
import com.intellij.ide.actionsOnSave.ActionOnSaveInfo;
import com.intellij.ide.actionsOnSave.ActionOnSaveInfoProvider;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class SpotlessOnSaveInfoProvider extends ActionOnSaveInfoProvider {
  @Override
  protected @NotNull Collection<? extends ActionOnSaveInfo> getActionOnSaveInfos(
      @NotNull ActionOnSaveContext context) {
    return Collections.singleton(new SpotlessOnSaveActionInfo(context));
  }
}
