package com.github.lipiridi.spotless.applier;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.concurrency.Semaphore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskUnderProgress {

    private static final Logger LOG = Logger.getInstance(TaskUnderProgress.class);

    private final String executorId;
    private final Project project;
    private final TaskCallback callback;
    private final ExecutionEnvironment environment;

    public TaskUnderProgress(
            String executorId,
            @NotNull Project project,
            @Nullable TaskCallback callback,
            ExecutionEnvironment environment) {
        this.executorId = executorId;
        this.project = project;
        this.callback = callback;
        this.environment = environment;
    }

    public void execute(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        final Semaphore targetDone = new Semaphore();
        final Ref<Boolean> result = new Ref<>(false);
        final Disposable disposable = Disposer.newDisposable();

        project.getMessageBus()
                .connect(disposable)
                .subscribe(ExecutionManager.EXECUTION_TOPIC, getExecutionListener(targetDone, result));

        try {
            ApplicationManager.getApplication()
                    .invokeAndWait(
                            () -> {
                                try {
                                    environment.getRunner().execute(environment);
                                } catch (ExecutionException e) {
                                    targetDone.up();
                                    LOG.error(e);
                                }
                            },
                            ModalityState.defaultModalityState());
        } catch (Exception e) {
            LOG.error(e);
            Disposer.dispose(disposable);
            return;
        }

        targetDone.waitFor();
        Disposer.dispose(disposable);

        if (callback != null) {
            if (result.get()) {
                callback.onSuccess();
            } else {
                callback.onFailure();
            }
        }
        if (!result.get()) {
            ApplicationManager.getApplication()
                    .invokeLater(
                            () -> {
                                ToolWindow window = ToolWindowManager.getInstance(project)
                                        .getToolWindow(environment.getExecutor().getToolWindowId());
                                if (window != null) {
                                    window.activate(null, false, false);
                                }
                            },
                            project.getDisposed());
        }
    }

    @NotNull private ExecutionListener getExecutionListener(Semaphore targetDone, Ref<Boolean> result) {
        return new ExecutionListener() {
            @Override
            public void processStartScheduled(
                    final @NotNull String executorIdLocal, final @NotNull ExecutionEnvironment environmentLocal) {
                if (executorId.equals(executorIdLocal) && environment.equals(environmentLocal)) {
                    targetDone.down();
                }
            }

            @Override
            public void processNotStarted(
                    final @NotNull String executorIdLocal, final @NotNull ExecutionEnvironment environmentLocal) {
                if (executorId.equals(executorIdLocal) && environment.equals(environmentLocal)) {
                    targetDone.up();
                }
            }

            @Override
            public void processTerminated(
                    @NotNull String executorIdLocal,
                    @NotNull ExecutionEnvironment environmentLocal,
                    @NotNull ProcessHandler handler,
                    int exitCode) {
                if (executorId.equals(executorIdLocal) && environment.equals(environmentLocal)) {
                    result.set(exitCode == 0);
                    targetDone.up();
                }
            }
        };
    }
}
