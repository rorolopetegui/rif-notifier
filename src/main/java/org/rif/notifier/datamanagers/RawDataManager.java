package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.RawData;
import org.rif.notifier.repositories.RawDataRepositorty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class RawDataManager {

    @Autowired
    private RawDataRepositorty rawDataRepositorty;

    public RawData insert(String type, String data, boolean processed, BigInteger block){
        RawData rd = new RawData(type, data, processed, block);
        RawData result = rawDataRepositorty.save(rd);
        return result;

    }
}
