package org.rif.notifier.models.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

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

    public int getHashCode(){
        int hash = 7;
        hash = 31 * hash + type.hashCode();
        if(topicParams.size() > 0){
            for(TopicParams param : topicParams) {
                hash = 31 * hash + param.getHashCode();
            }
        }
        return hash;
    }
}
