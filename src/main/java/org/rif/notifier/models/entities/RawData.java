package org.rif.notifier.models.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "raw_data")
public class RawData {

    @Id @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String type;

    private String data;

    private boolean processed;

    private BigInteger block;

    @Column(name = "id_topic")
    private int idTopic;

    public RawData(){}

    public RawData(String type, String data, boolean processed, BigInteger block, int idTopic) {
        this.type = type;
        this.data = data;
        this.processed = processed;
        this.block = block;
        this.idTopic = idTopic;
    }
    public RawData(String id, String type, String data, boolean processed, BigInteger block, int idTopic) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.processed = processed;
        this.block = block;
        this.idTopic = idTopic;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public BigInteger getBlock() {
        return block;
    }

    public void setBlock(BigInteger block) {
        this.block = block;
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
}
