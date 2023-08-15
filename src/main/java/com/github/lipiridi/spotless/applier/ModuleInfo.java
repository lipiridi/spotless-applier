package com.github.lipiridi.spotless.applier;

import com.github.lipiridi.spotless.applier.enums.BuildTool;
import com.intellij.openapi.module.Module;

public record ModuleInfo(Module module, String path, BuildTool buildTool, boolean rootModule) {}
