package org.rif.notifier.datamanagers;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
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

    public Subscription getSubscriptionByAddress(String user_address){
        return subscriptionRepositorty.findByUserAddress(user_address);
    }

    public Subscription insert(Date activeUntil, int active, String userAddress, int type, String state) {
        Subscription sub = new Subscription(activeUntil, active, userAddress, type, state);
        Subscription result = subscriptionRepositorty.save(sub);
        return result;
    }
}
