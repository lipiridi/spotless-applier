package com.lipiridi.spotless.applier;

import com.intellij.execution.Executor;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

public class ReformatProcessor {

    private final static NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance()
            .getNotificationGroup("Spotless Applier");
    private final Project project;
    private final String projectBasePath;
    private final ReformatTaskCallback reformatTaskCallback;
    private final Document document;

    public ReformatProcessor(@NotNull Project project, Document document) {
        this.project = project;
        this.projectBasePath = project.getBasePath();
        this.document = document;
        this.reformatTaskCallback = new ReformatTaskCallback(project, NOTIFICATION_GROUP, document);
    }

    public void run() {
        if (projectBasePath == null) {
            return;
        }

        VirtualFile baseVirtualFile = VfsUtil.findFile(Path.of(projectBasePath), true);

        if (baseVirtualFile == null) {
            return;
        }

        BuildTool buildTool = resolveBuildTool(baseVirtualFile);

        if (buildTool == null) {
            NOTIFICATION_GROUP.createNotification("Unable to resolve build tool", NotificationType.ERROR).notify(project);
            return;
        }

        switch (Objects.requireNonNull(buildTool)) {
            case GRADLE -> executeGradleTask();
            case MAVEN -> executeMavenTask();
        }
    }

    private BuildTool resolveBuildTool(VirtualFile file) {
        if (!file.isDirectory()) {
            return null;
        }
        for (VirtualFile child : file.getChildren()) {
            switch (child.getName()) {
                case "settings.gradle", "settings.gradle.kts", "build.gradle", "build.gradle.kts" -> {
                    return BuildTool.GRADLE;
                }
                case "pom.xml" -> {
                    return BuildTool.MAVEN;
                }
            }
        }
        return null;
    }

    private void executeGradleTask() {
        ExternalSystemTaskExecutionSettings externalSettings = new ExternalSystemTaskExecutionSettings();
        externalSettings.setExternalProjectPath(projectBasePath);
        externalSettings.setTaskNames(Collections.singletonList("spotlessApply"));
        externalSettings.setExternalSystemIdString(GradleConstants.SYSTEM_ID.getId());

        ToolEnvExternalSystemUtil.runTask(
                externalSettings,
                DefaultRunExecutor.EXECUTOR_ID,
                project,
                GradleConstants.SYSTEM_ID,
                reformatTaskCallback,
                document
        );
    }

    private void executeMavenTask() {
        final String command = "spotless:apply";

        ExternalSystemTaskExecutionSettings externalSettings = new ExternalSystemTaskExecutionSettings();
        externalSettings.setTaskNames(Collections.singletonList(command));

        ExecutionEnvironment environment = getMavenExecutionEnvironment(command);

        ToolEnvExternalSystemUtil.runTask(
                externalSettings,
                DefaultRunExecutor.EXECUTOR_ID,
                project,
                MavenUtil.SYSTEM_ID,
                reformatTaskCallback,
                document,
                environment
        );
    }

    @NotNull
    private ExecutionEnvironment getMavenExecutionEnvironment(String command) {
        MavenRunner mavenRunner = MavenRunner.getInstance(project);

        MavenRunnerSettings settings = mavenRunner.getState().clone();

        MavenRunnerParameters params = new MavenRunnerParameters();
        params.setWorkingDirPath(projectBasePath);
        params.setGoals(Collections.singletonList(command));

        RunnerAndConfigurationSettings configSettings = MavenRunConfigurationType.createRunnerAndConfigurationSettings(null,
                settings,
                params,
                project,
                MavenRunConfigurationType.generateName(project, params),
                false);

        configSettings.setActivateToolWindowBeforeRun(false);

        ProgramRunner<?> runner = DefaultJavaProgramRunner.getInstance();
        Executor executor = DefaultRunExecutor.getRunExecutorInstance();
        return new ExecutionEnvironment(executor, runner, configSettings, project);
    }

}

