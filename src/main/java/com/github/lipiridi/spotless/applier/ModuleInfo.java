package com.github.lipiridi.spotless.applier;

import com.github.lipiridi.spotless.applier.enums.BuildTool;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.nio.file.Path;

public record ModuleInfo(Module module, String path, BuildTool buildTool, boolean rootModule) {

    @SuppressWarnings("DataFlowIssue")
    public static ModuleInfo create(Project project, String projectBasePath, Module module) {
        BuildTool buildTool = BuildTool.resolveBuildTool(module);
        if (buildTool == null) {
            return null;
        }

        String modulePath = buildTool.getModulePath(module);
        if (modulePath == null) {
            return null;
        }

        boolean isRootModule = modulePath.equals(projectBasePath);

        Module rootModule =
                switch (buildTool) {
                    case MAVEN -> module;
                    case GRADLE -> {
                        // Gradle defines main and test folders also as modules,
                        // but we want to find only root module
                        VirtualFile moduleVirtualFile = VfsUtil.findFile(Path.of(modulePath), true);
                        yield ModuleUtil.findModuleForFile(moduleVirtualFile, project);
                    }
                };

        return new ModuleInfo(rootModule, modulePath, buildTool, isRootModule);
    }
}
