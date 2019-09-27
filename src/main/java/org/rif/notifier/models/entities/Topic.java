package org.rif.notifier.models.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    private String type;

    private String hash;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_topic",
            joinColumns = @JoinColumn(name = "id_topic", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_subscription", referencedColumnName = "id"))
    private Set<Subscription> subscriptions;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TopicParams> topicParams;

    public Topic(){}

    public Topic(String type, String hash, Subscription sub){
        this.type = type;
        this.hash = hash;
        this.subscriptions = Stream.of(sub).collect(Collectors.toSet());
        this.subscriptions.forEach(x -> x.getTopics().add(this));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TopicParams> getTopicParams() {
        return topicParams;
    }

    public void setTopicParams(List<TopicParams> topicParams) {
        this.topicParams = topicParams;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void addSubscription(Subscription sub){
        this.subscriptions.add(sub);
        sub.getTopics().add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, topicParams);
    }
}
