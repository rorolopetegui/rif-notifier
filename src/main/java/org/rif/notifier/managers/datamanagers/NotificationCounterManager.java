package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.NotificationCounter;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.repositories.NotificationCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationCounterManager {
    @Autowired
    private NotificationCounterRepository notificationCounterRepository;

    public NotificationCounter insert(Subscription subscription, int counter){
        NotificationCounter ntc = new NotificationCounter(subscription, counter);
        return notificationCounterRepository.save(ntc);
    }
}
