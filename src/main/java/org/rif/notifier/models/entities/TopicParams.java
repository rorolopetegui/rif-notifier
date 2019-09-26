package org.rif.notifier.models.entities;

import org.web3j.abi.datatypes.Bool;

import javax.persistence.*;

@Entity
@Table(name = "topic_params")
public class TopicParams {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="id_topic")
    private Topic topic;

    private String type;

    private String value;

    private int order;

    @Column(name = "value_type")
    private String valueType;

    private boolean indexed;

    public TopicParams(){}

    public TopicParams(Topic topic, String type, String value, int order, String valueType, boolean indexed){
        this.topic = topic;
        this.type = type;
        this.value = value;
        this.order = order;
        this.valueType = valueType;
        this.indexed = indexed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Topic getIdTopic() {
        return topic;
    }

    public void setIdTopic(Topic topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public boolean getIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public int getHashCode(){
        int hash = 7;
        hash = 31 * hash + type.hashCode();
        hash = 31 * hash + value.hashCode();
        hash = 31 * hash + order;
        hash = 31 * hash + valueType.hashCode();
        hash = 31 * hash + (indexed ? 1 : 0);
        return hash;
    }
}
