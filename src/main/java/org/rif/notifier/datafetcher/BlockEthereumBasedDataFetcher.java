package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedBlock;
import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockEthereumBasedDataFetcher extends EthereumBasedDataFetcher {

    @Async
    @Override
    public CompletableFuture<List<FetchedBlock>> fetch(EthereumBasedListenable ethereumBasedListenable, Web3j web3j) {
       return null;
    }


}
