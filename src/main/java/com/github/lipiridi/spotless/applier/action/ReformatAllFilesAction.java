package com.github.lipiridi.spotless.applier.action;

import com.github.lipiridi.spotless.applier.ModuleInfo;
import com.github.lipiridi.spotless.applier.ReformatProcessor;
import com.github.lipiridi.spotless.applier.enums.BuildTool;
import com.github.lipiridi.spotless.applier.ui.SelectModuleDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.nio.file.Path;
import java.util.*;

public class ReformatAllFilesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        var availableModules = getAvailableModules(project);

        if (availableModules.isEmpty()) {
            return;
        }

        if (availableModules.size() == 1) {
            ModuleInfo moduleInfo =
                    availableModules.values().stream().findFirst().get();
            new ReformatProcessor(project, moduleInfo).run();
            return;
        }

        letUserChooseModules(availableModules, project);
    }

    private void letUserChooseModules(Map<String, ModuleInfo> availableModules, Project project) {
        // The project could be just a folder that contains gradle/maven modules or also a module with own build tool
        // settings
        ModuleInfo projectModuleInfo = null;
        List<String> modulesForDialog = new ArrayList<>();
        for (Map.Entry<String, ModuleInfo> entry : availableModules.entrySet()) {
            var moduleInfo = entry.getValue();
            if (moduleInfo.rootModule()) {
                projectModuleInfo = moduleInfo;
            } else {
                String key = entry.getKey();
                modulesForDialog.add(key);
            }
        }

        SelectModuleDialog selectModuleDialog = new SelectModuleDialog(modulesForDialog, projectModuleInfo != null);

        if (selectModuleDialog.showAndGet()) {
            if (projectModuleInfo != null && selectModuleDialog.isApplyOnRootProject()) {
                new ReformatProcessor(project, projectModuleInfo).run();
            } else {
                selectModuleDialog
                        .getSelectedModules()
                        .forEach(module -> new ReformatProcessor(project, availableModules.get(module)).run());
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private Map<String, ModuleInfo> getAvailableModules(Project project) {
        Module[] modules = ProjectUtil.getModules(project);
        String projectBasePath = project.getBasePath();

        Map<String, ModuleInfo> availableModules = new LinkedHashMap<>();

        for (Module module : modules) {
            BuildTool buildTool = BuildTool.resolveBuildTool(module);
            if (buildTool == null) {
                continue;
            }

            String modulePath = buildTool.getModulePath(module);
            if (modulePath == null) {
                continue;
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

            availableModules.put(rootModule.getName(), new ModuleInfo(rootModule, modulePath, buildTool, isRootModule));
        }

        return availableModules;
    }
}
