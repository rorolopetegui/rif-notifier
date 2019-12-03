package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.Notification;
import org.rif.notifier.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotifEntityManager {
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification insert(String to_address, String timestamp, boolean sended, String data, int idTopic){
        Notification ntf = new Notification(to_address, timestamp, sended, data, idTopic);
        Notification result = notificationRepository.save(ntf);
        return result;

    }

    public List<Notification> getNotificationsByUserAddress(String user_address){
        List<Notification> lst = new ArrayList<>();
        notificationRepository.findByToAddress(user_address).forEach(lst::add);
        return lst;
    }
    public List<Notification> getNotificationsByUserAddressAndGraterThanId(String user_address, Integer id){
        return new ArrayList<>(notificationRepository.findByToAddressAndIdGraterThanId(user_address, id));
    }

    public List<Notification> getNotificationsByUserAddressLastRows(String user_address, Integer lastRows){
        return new ArrayList<>(notificationRepository.findByToAddressAndGetLastRows(user_address, lastRows));
    }
}
