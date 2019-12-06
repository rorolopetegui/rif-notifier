package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.Notification;
import org.rif.notifier.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class NotifEntityManager {
    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${notifier.notifications.maxquerylimit}")
    private int MAX_LIMIT_QUERY;

    public Notification insert(String to_address, String timestamp, boolean sended, String data, int idTopic){
        Notification ntf = new Notification(to_address, timestamp, sended, data, idTopic);
        Notification result = notificationRepository.save(ntf);
        return result;

    }

    public List<Notification> getNotificationsByUserAddress(String user_address){
        Pageable DEFAULT_PAGEABLE = PageRequest.of(0, MAX_LIMIT_QUERY);
        return new ArrayList<>(notificationRepository.findAllByToAddress(user_address, DEFAULT_PAGEABLE));
    }
    public List<Notification> getNotificationsByUserAddressAndIdTopic(String user_address, Set<Integer> idTopics){
        Pageable DEFAULT_PAGEABLE = PageRequest.of(0, MAX_LIMIT_QUERY);
        return new ArrayList<>(notificationRepository.findByToAddressAndIdTopicIn(user_address, idTopics, DEFAULT_PAGEABLE));
    }
    public List<Notification> getNotificationsByUserAddressAndGraterThanId(String user_address, Integer id){
        Pageable DEFAULT_PAGEABLE = PageRequest.of(0, MAX_LIMIT_QUERY);
        return new ArrayList<>(notificationRepository.findByToAddressAndIdGraterThanId(user_address, id, DEFAULT_PAGEABLE));
    }
    public List<Notification> getNotificationsByUserAddressAndGraterThanIdAndIdTopic(String user_address, Integer id, Set<Integer> idTopics){
        Pageable DEFAULT_PAGEABLE = PageRequest.of(0, MAX_LIMIT_QUERY);
        return new ArrayList<>(notificationRepository.findAllByToAddressAndIdGreaterThanAndIdTopicIn(user_address, id, idTopics, DEFAULT_PAGEABLE));
    }
    public List<Notification> getNotificationsByUserAddressLastRows(String user_address, Integer lastRows){
        Pageable pageable = PageRequest.of(0, MAX_LIMIT_QUERY < lastRows ? MAX_LIMIT_QUERY : lastRows);
        return new ArrayList<>(notificationRepository.findByToAddressAndGetLastRows(user_address, pageable));
    }
    public List<Notification> getNotificationsByUserAddressLastRowsAndIdTopic(String user_address, Integer lastRows, Set<Integer> idTopics){
        Pageable pageable = PageRequest.of(0, MAX_LIMIT_QUERY < lastRows ? MAX_LIMIT_QUERY : lastRows);
        return new ArrayList<>(notificationRepository.findByToAddressAndGetLastRowsAndIdTopic(user_address, idTopics, pageable));
    }
}
