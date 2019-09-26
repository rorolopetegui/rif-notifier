package org.rif.notifier.models.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="user_topic")
public class UserTopic {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_topic")
    private Topic topic;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_subscription")
    private Subscription subscription;

    public UserTopic(){}

    public UserTopic(Topic topic, Subscription sub){
        this.topic = topic;
        this.subscription = sub;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
