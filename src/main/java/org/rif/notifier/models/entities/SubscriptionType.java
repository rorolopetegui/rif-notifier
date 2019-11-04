package org.rif.notifier.models.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "subscription_type")
public class SubscriptionType {
    @Id
    int id;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Subscription> subscription;

    private int notifications;

    public SubscriptionType(){}

    public SubscriptionType(int notifications){
        this.notifications = notifications;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnotifications() {
        return notifications;
    }

    public void setnotifications(int notifications) {
        this.notifications = notifications;
    }

    public List<Subscription> getSubscription() {
        return subscription;
    }

    public void setSubscription(List<Subscription> subscription) {
        this.subscription = subscription;
    }
}
