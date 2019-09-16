package org.rif.notifier.models.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Notification {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "to_address")
    private String toAddress;

    private Date timestamp;

    private boolean sended;

    private String data;

    public Notification(){}

    public Notification(String to_address, Date timestamp, boolean sended, String data) {
        this.toAddress = to_address;
        this.timestamp = timestamp;
        this.sended = sended;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo_address() {
        return toAddress;
    }

    public void setTo_address(String to_address) {
        this.toAddress = to_address;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}