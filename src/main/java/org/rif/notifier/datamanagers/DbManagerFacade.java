package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.RawData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbManagerFacade {
    @Autowired
    private RawDataManager rawDataManager;

    public RawData saveRawData(String type, String data, boolean processed, BigInteger block){
       return rawDataManager.insert(type,data,processed, block);
    }

    @Transactional
    public List<RawData> saveRawDataBatch(List<RawData> rawData){
        return rawData.stream().map(rawData1 -> rawDataManager.insert(rawData1.getType(), rawData1.getData(), rawData1.isProcessed(), rawData1.getBlock())).collect(Collectors.toList());
    }
}
