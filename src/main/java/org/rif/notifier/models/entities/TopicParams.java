package org.rif.notifier.models.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.rif.notifier.constants.TopicParamTypes;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "topic_params")
public class TopicParams {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false, fetch=FetchType.EAGER)
    @JoinColumn(name="id_topic")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "param_type")
    private TopicParamTypes type;

    private String value;

    @Column(name = "param_order")
    private int order;

    @Column(name = "value_type")
    private String valueType;

    @Column(name = "is_indexed")
    private boolean indexed;

    private String filter;

    public TopicParams(){}

    public TopicParams(Topic topic, TopicParamTypes type, String value, int order, String valueType, boolean indexed, String filter){
        this.topic = topic;
        this.type = type;
        this.value = value;
        this.order = order;
        this.valueType = valueType;
        this.indexed = indexed;
        this.filter = filter;
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

    public TopicParamTypes getType() {
        return type;
    }

    public void setType(TopicParamTypes type) {
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

    public boolean isIndexed() {
        return indexed;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /*
    @Override
    public int hashCode() {
        return Objects.hash(type, value, order, valueType, indexed, filter);
    }
    */

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type.toString())
                .append(value)
                .append(order)
                .append(valueType)
                .append(indexed)
                .append(filter)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"topic\":" + topic +
                ", \"type\":\"" + type + "\"" +
                ", \"value\":\"" + value + "\"" +
                ", \"order\":" + order +
                ", \"valueType\":\"" + valueType + "\"" +
                ", \"indexed\":" + indexed +
                ", \"filter\":\"" + filter + "\"" +
                '}';
    }

    public String toStringInfo() {
        return "{" +
                "\"type\":\"" + type + "\"" +
                ", \"value\":\"" + value + "\"" +
                ", \"order\":" + order +
                ", \"valueType\":\"" + valueType + "\"" +
                ", \"indexed\":" + indexed +
                ", \"filter\":\"" + filter + "\"" +
                '}';
    }
}
