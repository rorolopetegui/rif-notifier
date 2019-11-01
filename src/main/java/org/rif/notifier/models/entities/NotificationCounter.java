package org.rif.notifier.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "notification_counter")
public class NotificationCounter {
    @Id @Column(name="subscription_id") int id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private int counter;

    public NotificationCounter(){}

    public NotificationCounter(Subscription sub, int counter){
        this.subscription = sub;
        this.counter = counter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
