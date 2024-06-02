package com.github.lipiridi.spotless.applier.actions;

import com.github.lipiridi.spotless.applier.config.SpotlessConfiguration;
import com.github.lipiridi.spotless.applier.config.SpotlessOnSaveOptions;
import com.intellij.ide.actionsOnSave.impl.ActionsOnSaveFileDocumentManagerListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Action to be called whenever a document is saved */
public class SpotlessActionOnSave extends ActionsOnSaveFileDocumentManagerListener.ActionOnSave {
  public static boolean isActiveFile(
      @NotNull PsiFile psiFile, @NotNull Project project) {
    var virtualFile = psiFile.getVirtualFile();
    var fileEditorManager = FileEditorManager.getInstance(project);

    return Optional.ofNullable(fileEditorManager.getSelectedTextEditor())
        .map(Editor::getVirtualFile)
        .filter(virtualFile::equals)
        .isPresent();
  }

  private boolean isFileSelectedForFormatting(@NotNull PsiFile psiFile, @NotNull
  SpotlessOnSaveOptions options) {
    return options.isFileTypeSelected(psiFile.getFileType());
  }

  private static @Nullable PsiFile getPsiFile(@NotNull Project project, Document editor) {
    return PsiDocumentManager.getInstance(project).getPsiFile(editor);
  }

  @Override
  public boolean isEnabledForProject(@NotNull Project project) {
    return SpotlessConfiguration.getInstance(project).isRunOnSaveEnabled();
  }

  @Override
  public void processDocuments(@NotNull Project project, @NotNull Document @NotNull [] documents) {
    var options = SpotlessOnSaveOptions.getInstance(project);

    if (!options.isRunOnSaveEnabled()) {
      return;
    }

    Arrays.stream(documents)
        .map(document -> getPsiFile(project, document))
        .filter(Objects::nonNull)
        .filter(file -> isActiveFile(file, project))
        .filter(file -> isFileSelectedForFormatting(file, options))
        .forEach(file -> new ReformatCodeProcessor(file).run());
  }
}
