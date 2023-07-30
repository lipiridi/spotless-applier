package com.lipiridi.spotless.applier;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.project.Project;

public class ReformatTaskCallback implements TaskCallback {

    private final Project project;
    private final NotificationGroup notificationGroup;

    public ReformatTaskCallback(Project project, NotificationGroup notificationGroup) {
        this.project = project;
        this.notificationGroup = notificationGroup;
    }

    @Override
    public void onSuccess() {
        notificationGroup.createNotification("Spotless was applied", NotificationType.INFORMATION).notify(project);
    }

    @Override
    public void onFailure() {
        notificationGroup.createNotification("Spotless was not applied", NotificationType.ERROR).notify(project);
    }
}
