package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.ChainAddressEvent;
import org.rif.notifier.repositories.ChainAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChainAddressManager {
    @Autowired
    private ChainAddressRepository chainAddressRepository;

    public ChainAddressEvent insert(String nodehash, String eventName, String chain, String address) {
        ChainAddressEvent evnt = new ChainAddressEvent(nodehash, eventName, chain, address);
        ChainAddressEvent result = chainAddressRepository.save(evnt);
        return result;
    }
}
