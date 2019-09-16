package org.rif.notifier.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "notification_preference")
public class NotificationPreferences {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    @Column(name="user_address")
    private String userAddress;

    @Column(name = "notification_service")
    private String notificationService;

    //FK to subscription
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="subscription")
    private Subscription subscription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(String notificationService) {
        this.notificationService = notificationService;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
