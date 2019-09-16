package org.rif.notifier.notificationmanagers.services;

import org.rif.notifier.models.entities.Notification;

import java.util.List;

public interface NotificationService {

    void notifySubscriber(String adddress, List<Notification> notifications);
}
