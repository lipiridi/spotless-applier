package com.lipiridi.spotless.applier;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ReformatFileAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        Optional.ofNullable(event.getDataContext().getData(CommonDataKeys.EDITOR))
                .map(Editor::getDocument)
                .ifPresent(document -> {
                    PsiFile psiFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
                    new ReformatProcessor(project, document, psiFile).run();
                });
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Check if there is an active editor.
        boolean isActiveEditorAvailable = Optional.ofNullable(
                        event.getDataContext().getData(CommonDataKeys.EDITOR))
                .isPresent();

        // Enable the action only when there is an active editor.
        event.getPresentation().setEnabled(isActiveEditorAvailable);
        event.getPresentation().setVisible(isActiveEditorAvailable);
    }
}
