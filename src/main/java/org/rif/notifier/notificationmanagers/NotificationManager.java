package org.rif.notifier.notificationmanagers;

import org.rif.notifier.datamanagers.DbManagerFacade;
import org.rif.notifier.models.entities.Notification;
import org.rif.notifier.models.entities.NotificationPreferences;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.notificationmanagers.services.NotificationService;
import org.rif.notifier.notificationmanagers.services.impl.MailNotification;
import org.rif.notifier.notificationmanagers.services.impl.PushAndroid;
import org.rif.notifier.notificationmanagers.services.impl.PushIOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.rif.notifier.constants.NotificationConstants.*;

@Service
public class NotificationManager {

    @Autowired
    private DbManagerFacade dbManagerFacade;

    public List<Notification> getNotificationsForAddress(String address){
        List<Notification> lst = new ArrayList<>();
        Subscription sub = dbManagerFacade.getSubscriptionByAddress(address);
        if(sub.getActive() == 1)
            lst = dbManagerFacade.getNotificationByUserAddress(address);
        return lst;
    }

    public void notificateUsers(){
        List<Subscription> activeSubs = dbManagerFacade.getAllActiveSubscriptions();
        for(Subscription sub : activeSubs){
            List<Notification> notifications = dbManagerFacade.getNotificationByUserAddress(sub.getUserAddress());
            for(NotificationPreferences preference : sub.getNotificationPreferences()){
                NotificationService notificationService;
                switch (preference.getNotificationService()){
                    case PUSH_IOS:
                        notificationService = new PushIOS();
                        break;
                    case PUSH_ANDROID:
                        notificationService = new PushAndroid();
                        break;
                    default:
                        notificationService = new MailNotification();
                        break;
                }
                notificationService.notifySubscriber(sub.getUserAddress(), notifications);
            }
        }
    }
}
