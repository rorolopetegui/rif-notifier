package org.rif.notifier.models.entities;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    @Column(name = "id_topic")
    private int idTopic;

    @Column(name = "active_until")
    private Date activeUntil;

    private int active;

    @Column(name = "user_address")
    private String userAddress;

    private int type;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserTopic> userTopic ;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<NotificationPreferences> notificationPreferences ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdTopic() {
        return idTopic;
    }

    public void setIdTopic(int idTopic) {
        this.idTopic = idTopic;
    }

    public Date getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Date activeUntil) {
        this.activeUntil = activeUntil;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<UserTopic> getUserTopic() {
        return userTopic;
    }

    public void setUserTopic(List<UserTopic> userTopic) {
        this.userTopic = userTopic;
    }

    public List<NotificationPreferences> getNotificationPreferences() {
        return notificationPreferences;
    }

    public void setNotificationPreferences(List<NotificationPreferences> notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
}
