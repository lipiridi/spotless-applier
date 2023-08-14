package com.github.lipiridi.spotless.applier;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.actions.OptimizeImportsProcessor;
import com.intellij.core.CoreBundle;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.JavaCodeStyleSettings;
import com.intellij.psi.codeStyle.PackageEntryTable;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

// Custom implementation, because we can't optimize imports for specific file with temporary settings
public class SynchronousOptimizeImportsProcessor extends OptimizeImportsProcessor {

    private final boolean prohibitImportsWithAsterisk;
    private PsiFile myFile;

    public SynchronousOptimizeImportsProcessor(
            @NotNull Project project, boolean prohibitImportsWithAsterisk, @NotNull Module module) {
        super(project, module);
        this.prohibitImportsWithAsterisk = prohibitImportsWithAsterisk;
    }

    public SynchronousOptimizeImportsProcessor(
            @NotNull Project project, boolean prohibitImportsWithAsterisk, @NotNull PsiFile file) {
        super(project, file);
        myFile = file;
        this.prohibitImportsWithAsterisk = prohibitImportsWithAsterisk;
    }

    @Override
    public void run() {
        if (prohibitImportsWithAsterisk) {
            CodeStyleSettings currentSettings = CodeStyle.getSettings(myProject);
            CodeStyle.doWithTemporarySettings(myProject, currentSettings, codeStyleSettings -> {
                JavaCodeStyleSettings javaSettings = codeStyleSettings.getCustomSettings(JavaCodeStyleSettings.class);
                javaSettings.CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND = Integer.MAX_VALUE;
                javaSettings.NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND = Integer.MAX_VALUE;
                javaSettings.PACKAGES_TO_USE_IMPORT_ON_DEMAND = new PackageEntryTable();

                runProcess();
            });
        } else {
            runProcess();
        }
    }

    public void runProcess() {
        if (myFile != null) {
            runProcessFile(myFile);
            return;
        }

        runProcessFiles();
    }

    private void runProcessFile(@NotNull final PsiFile file) {
        PsiUtilCore.ensureValid(file);

        Document document = PsiDocumentManager.getInstance(myProject).getDocument(file);

        if (document == null) {
            return;
        }

        if (!FileDocumentManager.getInstance().requestWriting(document, myProject)) {
            Messages.showMessageDialog(
                    myProject,
                    CoreBundle.message("cannot.modify.a.read.only.file", file.getName()),
                    CodeInsightBundle.message("error.dialog.readonly.file.title"),
                    Messages.getErrorIcon());
            return;
        }

        runWithoutProgress();
    }

    private void runProcessFiles() {
        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(
                        () -> {
                            ProgressIndicator indicator =
                                    ProgressManager.getInstance().getProgressIndicator();
                            return processFilesUnderProgress(indicator);
                        },
                        getCommandName(),
                        true,
                        myProject);
    }
}
