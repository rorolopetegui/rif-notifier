package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.DataFetcherEntity;
import org.rif.notifier.repositories.DataFetcherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class DataFetcherManager {
    @Autowired
    private DataFetcherRepository dataFetcherRepository;

    public DataFetcherEntity insert(BigInteger lastBlock){
        DataFetcherEntity dt = new DataFetcherEntity(0, lastBlock);
        return dataFetcherRepository.save(dt);
    }

    public BigInteger get(){
        return dataFetcherRepository.findById(0).getLastBlock();
    }
}
