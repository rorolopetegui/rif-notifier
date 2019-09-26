package org.rif.notifier.models.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    private String type;

    private String hash;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TopicParams> topicParams ;

    public Topic(){}

    public Topic(String type, String hash){
        this.type = type;
        this.hash = hash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id &&
                type.equals(topic.type) &&
                topicParams.equals(topic.topicParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, topicParams);
    }
}
