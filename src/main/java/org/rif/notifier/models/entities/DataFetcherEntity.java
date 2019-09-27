package org.rif.notifier.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;

@Entity
public class DataFetcherEntity {
    @Id
    public int id;

    @Column(name = "last_block")
    public BigInteger lastBlock;

    public DataFetcherEntity() {}

    public DataFetcherEntity(int id, BigInteger lastBlock) {
        this.id = id;
        this.lastBlock = lastBlock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigInteger getLastBlock() {
        return lastBlock;
    }

    public void setLastBlock(BigInteger lastBlock) {
        this.lastBlock = lastBlock;
    }
}
