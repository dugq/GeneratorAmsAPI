package com.dugq.component.common;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

/**
 * @author dugq
 * @date 2021/7/7 2:11 下午
 */
public class NotifyComponent {
    private static NotificationGroup notificationGroup;

    static {
        notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }


    public static void error(String message, Project project){
        Notification error = notificationGroup.createNotification(message, NotificationType.ERROR);
        Notifications.Bus.notify(error, project);
    }
    public static void success(String message, Project project){
        Notification error = notificationGroup.createNotification(message, NotificationType.INFORMATION);
        Notifications.Bus.notify(error, project);
    }
}
