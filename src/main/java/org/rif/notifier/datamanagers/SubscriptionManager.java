package org.rif.notifier.datamanagers;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionManager {

    @Autowired
    private SubscriptionRepository subscriptionRepositorty;

    public List<Subscription> getActiveSubscriptions(){
        List<Subscription> lst = new ArrayList<>();
        subscriptionRepositorty.findByActive(1).forEach(lst::add);
        return lst;
    }

    public List<Subscription> getActiveSubscriptionsByTopicId(int idTopic){
        List<Subscription> lst = new ArrayList<>();
        subscriptionRepositorty.findByIdTopicAndSubscriptionActive(idTopic).forEach(lst::add);
        return lst;
    }

}
