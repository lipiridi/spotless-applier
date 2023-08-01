package com.lipiridi.spotless.applier;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.Optional;

public class ReformatCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        Document document = Optional
                .ofNullable(event.getDataContext().getData(CommonDataKeys.EDITOR))
                .map(Editor::getDocument)
                .orElse(null);

        new ReformatProcessor(project, document).run();
    }
}

