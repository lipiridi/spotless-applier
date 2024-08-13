package com.github.lipiridi.spotless.applier;

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
            final @NotNull String moduleName,
            final @NotNull ProjectSystemId externalSystemId,
            final @Nullable TaskCallback callback,
            final @Nullable Document document) {
        ExecutionEnvironment environment =
                ExternalSystemUtil.createExecutionEnvironment(project, externalSystemId, taskSettings, executorId);
        runTask(taskSettings, executorId, project, moduleName, externalSystemId, callback, document, environment);
    }

    public static void runTask(
            final @NotNull ExternalSystemTaskExecutionSettings taskSettings,
            final @NotNull String executorId,
            final @NotNull Project project,
            final @NotNull String moduleName,
            final @NotNull ProjectSystemId externalSystemId,
            final @Nullable TaskCallback callback,
            final @Nullable Document document,
            final ExecutionEnvironment environment) {
        if (environment == null) {
            LOG.warn("Execution environment for " + externalSystemId + " is null");
            return;
        }

        RunnerAndConfigurationSettings runnerAndConfigurationSettings = environment.getRunnerAndConfigurationSettings();
        assert runnerAndConfigurationSettings != null;
        runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(false);

        final TaskUnderProgress taskUnderProgress = new TaskUnderProgress(executorId, project, callback, environment);

        final String title = moduleName + " " + taskSettings.getTaskNames();

        Task task = getTask(project, document, title, taskUnderProgress, externalSystemId);

        task.queue();
    }

    @NotNull private static Task getTask(
            @NotNull Project project,
            Document document,
            String title,
            TaskUnderProgress taskUnderProgress,
            ProjectSystemId externalSystemId) {
        // Currently not found the way to run gradle task synchronously
        // https://intellij-support.jetbrains.com/hc/en-us/community/posts/12849664786322-Execute-Gradle-task-in-ProgressExecutionMode-MODAL-SYNC
        // https://youtrack.jetbrains.com/issue/IDEA-327879
        if (document == null && !externalSystemId.equals(GradleConstants.SYSTEM_ID)) {
            return new Task.Modal(project, title, false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    taskUnderProgress.execute(indicator);
                }
            };
        } else {
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
