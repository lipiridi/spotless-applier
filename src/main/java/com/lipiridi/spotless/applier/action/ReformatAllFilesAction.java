package com.lipiridi.spotless.applier.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.lipiridi.spotless.applier.ReformatProcessor;

public class ReformatAllFilesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        new ReformatProcessor(project).run();
    }
}
