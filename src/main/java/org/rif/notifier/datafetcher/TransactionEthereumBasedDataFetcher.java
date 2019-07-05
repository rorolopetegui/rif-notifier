package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.datafetching.FetchedTransaction;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionEthereumBasedDataFetcher extends EthereumBasedDataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(TransactionEthereumBasedDataFetcher.class);

    @Async
    @Override
    public CompletableFuture<List<FetchedTransaction>> fetch(EthereumBasedListenable ethereumBasedListenable, Web3j web3j) {
        logger.info("Transactions start ===================");
        long count = 0l;
        for(long x=0;x<Integer.MAX_VALUE ;x++){
            count+=1;
        }
        return CompletableFuture.supplyAsync(() -> {
            List<FetchedTransaction> transactions = new ArrayList<>();
           Iterable<Transaction> obs = web3j.replayPastTransactionsFlowable(DefaultBlockParameter.valueOf(new BigInteger("75200")), DefaultBlockParameter.valueOf(new BigInteger("75250"))).blockingLatest();
           for(Transaction t : obs){
               FetchedTransaction ft = new FetchedTransaction();
               ft.transaction = t;
               transactions.add(ft);
           }
            logger.info("Transactions "+transactions.size());
           return transactions;

        }).exceptionally(throwable -> {
            logger.error(throwable.toString());
            return null;
        });

    }



}
