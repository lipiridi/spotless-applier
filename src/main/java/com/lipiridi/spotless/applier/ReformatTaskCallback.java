package com.lipiridi.spotless.applier;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.project.Project;

public class ReformatTaskCallback implements TaskCallback {

    private final Project project;
    private final NotificationGroup notificationGroup;
    private final Document document;

    public ReformatTaskCallback(Project project, NotificationGroup notificationGroup, Document document) {
        this.project = project;
        this.notificationGroup = notificationGroup;
        this.document = document;
    }

    @Override
    public void onSuccess() {
        unlockDocument();
        notificationGroup.createNotification("Spotless was applied", NotificationType.INFORMATION).notify(project);
    }

    @Override
    public void onFailure() {
        unlockDocument();
        notificationGroup.createNotification("Spotless was not applied", NotificationType.ERROR).notify(project);
    }

    private void unlockDocument() {
        if (document != null) {
            document.setReadOnly(false);
        }
    }
}
