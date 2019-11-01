package org.rif.notifier.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "subscription_type")
public class SubscriptionType {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    @Column(name = "subscription_type")
    private int subscriptionType;

    @Column(name = "notification_counter")
    private int notificationCounter;

    public SubscriptionType(){}

    public SubscriptionType(int subscriptionType, int notificationCounter){
        this.subscriptionType = subscriptionType;
        this.notificationCounter = notificationCounter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int type) {
        this.subscriptionType = type;
    }

    public int getNotificationCounter() {
        return notificationCounter;
    }

    public void setNotificationCounter(int notificationCounter) {
        this.notificationCounter = notificationCounter;
    }
}
