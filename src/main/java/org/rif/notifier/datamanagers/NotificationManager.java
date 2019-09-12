package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.Notification;
import org.rif.notifier.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationManager {
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification insert(String to_address, Date timestamp, boolean sended, String data){
        Notification ntf = new Notification(to_address, timestamp, sended, data);
        Notification result = notificationRepository.save(ntf);
        return result;

    }
}
