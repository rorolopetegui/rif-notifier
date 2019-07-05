package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import java.util.concurrent.CompletableFuture;

@Component
public abstract class EthereumBasedDataFetcher {

    @Async
    public abstract <T extends FetchedData> CompletableFuture<T> fetch (EthereumBasedListenable ethereumBasedListenable, Web3j web3j);
}
