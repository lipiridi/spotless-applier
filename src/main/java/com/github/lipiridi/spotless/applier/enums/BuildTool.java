package com.github.lipiridi.spotless.applier.enums;

import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.plugins.gradle.util.GradleConstants;

public enum BuildTool {
    MAVEN,
    GRADLE;

    public static BuildTool resolveBuildTool(Module module) {
        return ExternalSystemApiUtil.isExternalSystemAwareModule(GradleConstants.SYSTEM_ID, module)
                ? BuildTool.GRADLE
                : MavenUtil.isMavenModule(module) ? BuildTool.MAVEN : null;
    }

    @Nullable public String getModulePath(Module module) {
        return switch (this) {
            case GRADLE -> ExternalSystemApiUtil.getExternalProjectPath(module);
            case MAVEN -> Optional.ofNullable(ProjectUtil.guessModuleDir(module))
                    .map(VirtualFile::getPath)
                    .orElse(null);
        };
    }
}
