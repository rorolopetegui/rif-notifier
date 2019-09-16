package org.rif.notifier.notificationmanagers;

import org.rif.notifier.models.entities.Notification;

import java.util.List;

public interface NotificateProcess {

    void notifySubscriber(String adddress, List<Notification> notifications);
}
