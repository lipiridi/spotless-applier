package com.lipiridi.spotless.applier;

import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.util.GradleConstants;

public final class ToolEnvExternalSystemUtil {
    private static final Logger LOG = Logger.getInstance(ToolEnvExternalSystemUtil.class);

    public static void runTask(
            final @NotNull ExternalSystemTaskExecutionSettings taskSettings,
            final @NotNull String executorId,
            final @NotNull Project project,
            final @NotNull ProjectSystemId externalSystemId,
            final @Nullable TaskCallback callback,
            final @Nullable Document document,
            final @Nullable AbstractLayoutCodeProcessor prepareCodeProcessor) {
        ExecutionEnvironment environment =
                ExternalSystemUtil.createExecutionEnvironment(project, externalSystemId, taskSettings, executorId);
        runTask(
                taskSettings,
                executorId,
                project,
                externalSystemId,
                callback,
                document,
                prepareCodeProcessor,
                environment);
    }

    public static void runTask(
            final @NotNull ExternalSystemTaskExecutionSettings taskSettings,
            final @NotNull String executorId,
            final @NotNull Project project,
            final @NotNull ProjectSystemId externalSystemId,
            final @Nullable TaskCallback callback,
            final @Nullable Document document,
            final @Nullable AbstractLayoutCodeProcessor prepareCodeProcessor,
            final ExecutionEnvironment environment) {
        if (environment == null) {
            LOG.warn("Execution environment for " + externalSystemId + " is null");
            return;
        }

        RunnerAndConfigurationSettings runnerAndConfigurationSettings = environment.getRunnerAndConfigurationSettings();
        assert runnerAndConfigurationSettings != null;
        runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(false);

        final TaskUnderProgress taskUnderProgress = new TaskUnderProgress(executorId, project, callback, environment);

        final String title = project.getName() + " " + taskSettings.getTaskNames();

        Task task = getTask(project, document, title, taskUnderProgress, externalSystemId, prepareCodeProcessor);

        task.queue();
    }

    @NotNull private static Task getTask(
            @NotNull Project project,
            Document document,
            String title,
            TaskUnderProgress taskUnderProgress,
            ProjectSystemId externalSystemId,
            AbstractLayoutCodeProcessor prepareCodeProcessor) {
        // Currently not found the way to run gradle task synchronously
        // https://intellij-support.jetbrains.com/hc/en-us/community/posts/12849664786322-Execute-Gradle-task-in-ProgressExecutionMode-MODAL-SYNC
        if (document == null && !externalSystemId.equals(GradleConstants.SYSTEM_ID)) {
            return new Task.Modal(project, title, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    if (prepareCodeProcessor != null) {
                        prepareCodeProcessor.run();
                    }
                    taskUnderProgress.execute(indicator);
                }
            };
        } else {
            // AbstractLayoutCodeProcessor cannot be run in background thread
            if (prepareCodeProcessor != null) {
                prepareCodeProcessor.run();
            }
            return new Task.Backgroundable(project, title) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    if (document != null) {
                        document.setReadOnly(true);
                    }
                    taskUnderProgress.execute(indicator);
                }
            };
        }
    }
}
