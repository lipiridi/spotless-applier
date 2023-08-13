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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        SelectModuleDialog selectModuleDialog = new SelectModuleDialog(availableModules.keySet());

        if (selectModuleDialog.showAndGet()) {
            List<String> selectedModules = selectModuleDialog.getSelectedModules();
            selectedModules.forEach(module -> new ReformatProcessor(project, availableModules.get(module)).run());
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private Map<String, ModuleInfo> getAvailableModules(Project project) {
        Module[] modules = ProjectUtil.getModules(project);

        Map<String, ModuleInfo> availableModules = new HashMap<>();

        for (Module module : modules) {
            BuildTool buildTool = BuildTool.resolveBuildTool(module);
            if (buildTool == null) {
                continue;
            }

            String modulePath = buildTool.getModulePath(module);
            if (modulePath == null) {
                continue;
            }

            Module rootModule =
                    switch (buildTool) {
                        case MAVEN -> module;
                        case GRADLE -> {
                            // Gradle defines main and test folders also as modules, but we want to find only root
                            // module
                            VirtualFile moduleVirtualFile = VfsUtil.findFile(Path.of(modulePath), true);
                            yield ModuleUtil.findModuleForFile(moduleVirtualFile, project);
                        }
                    };

            availableModules.put(rootModule.getName(), new ModuleInfo(rootModule, modulePath, buildTool));
        }

        return availableModules;
    }
}
