package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedBlock;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BlockEthereumBasedDataFetcher extends EthereumBasedDataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(BlockEthereumBasedDataFetcher.class);

    @Async
    @Override
    public CompletableFuture<List<FetchedBlock>> fetch(EthereumBasedListenable ethereumBasedListenable, BigInteger from, BigInteger to, Web3j web3j) {
        long start = System.currentTimeMillis();
        logger.info(Thread.currentThread().getId() + " - Starting block events for subscription: " + ethereumBasedListenable);
        //TODO not implemented yet!

        long end = System.currentTimeMillis();
        logger.info(Thread.currentThread().getId() + " - End Contract Data fetching time = " + (end - start));
        return CompletableFuture.completedFuture(new ArrayList<>());
    }


}
