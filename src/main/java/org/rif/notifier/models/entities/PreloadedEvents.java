package org.rif.notifier.models.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "preloaded_events")
public class PreloadedEvents implements Serializable {
    private static final long serialVersionUID = -8307323096334056925L;
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    @Column
    private String description;

    @Column
    private String event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
