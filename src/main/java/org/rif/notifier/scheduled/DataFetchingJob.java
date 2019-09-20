package org.rif.notifier.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.datafetcher.MockDatafetcher;
import org.rif.notifier.datamanagers.DbManagerFacade;
import org.rif.notifier.models.datafetching.FetchedBlock;
import org.rif.notifier.models.datafetching.FetchedEvent;
import org.rif.notifier.models.datafetching.FetchedTransaction;
import org.rif.notifier.models.entities.*;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.services.blockchain.generic.rootstock.RskBlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Component
public class DataFetchingJob {

    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private RskBlockchainService rskBlockchainService;

    @Autowired
    private DbManagerFacade dbManagerFacade;

    @Scheduled(fixedRateString = "${notifier.run.fixedRateFetchingJob}", initialDelayString = "${notifier.run.fixedDelayFetchingJob}")
    public void run() throws Exception {
        // TODO Mocked data, must be provided by the subscription manager
        List<EthereumBasedListenable> ethereumBasedListenables = new ArrayList<>();
        List<Subscription> activeSubs = dbManagerFacade.getActiveAndWithCounterSubscriptions();
        Boolean alreadyAdded;
        for(Subscription sub : activeSubs){
            List<UserTopic> userTopics = sub.getUserTopic();
            for(UserTopic uTopic : userTopics){
                alreadyAdded = false;
                EthereumBasedListenable newListeneable = MockDatafetcher.getEthereumBasedListenableFromTopic(uTopic);
                //Performing some checks to not insert when its already in the list
                if(newListeneable.getKind().equals(EthereumBasedListenableTypes.CONTRACT_EVENT)){
                    alreadyAdded = ethereumBasedListenables.stream().anyMatch(item ->
                            item.getKind().equals(EthereumBasedListenableTypes.CONTRACT_EVENT)
                            && item.getAddress().equals(newListeneable.getAddress())
                            && item.getEventName().equals(newListeneable.getEventName())
                    );
                }else if(newListeneable.getKind().equals(EthereumBasedListenableTypes.NEW_TRANSACTIONS)){
                    alreadyAdded = ethereumBasedListenables.stream().anyMatch(item ->
                            item.getKind().equals(EthereumBasedListenableTypes.NEW_TRANSACTIONS)
                    );
                }else{
                    alreadyAdded = ethereumBasedListenables.stream().anyMatch(item ->
                            item.getKind().equals(EthereumBasedListenableTypes.NEW_BLOCK)
                    );
                }
                if(!alreadyAdded)
                    ethereumBasedListenables.add(newListeneable);
            }
        }
        // Get latest block for this run
        BigInteger to = rskBlockchainService.getLastBlock();
        BigInteger from = to.subtract(new BigInteger("5"));// must read latest from db for now it queries latest 5 blocks

        //Fetching
        logger.info(Thread.currentThread().getId() + String.format(" - Starting fetching from %s to %s", from, to));

        long start = System.currentTimeMillis();
        List<CompletableFuture<List<FetchedBlock>>> blockTasks = new ArrayList<>();
        List<CompletableFuture<List<FetchedTransaction>>> transactionTasks = new ArrayList<>();
        List<CompletableFuture<List<FetchedEvent>>> eventTasks = new ArrayList<>();
        for (EthereumBasedListenable subscriptionChannel : ethereumBasedListenables) {
            subscriptionChannel.getEventFields().stream().forEach(item -> {
            });
            try {
                switch (subscriptionChannel.getKind()) {
                    case NEW_BLOCK:
                        blockTasks.add(rskBlockchainService.getBlocks(subscriptionChannel, from, to));
                        break;
                    case CONTRACT_EVENT:
                        eventTasks.add(rskBlockchainService.getContractEvents(subscriptionChannel, from, to));
                        break;
                    case NEW_TRANSACTIONS:
                        transactionTasks.add(rskBlockchainService.getTransactions(subscriptionChannel, from, to));
                        break;
                }
            } catch (Exception e) {
                logger.error("Error during DataFetching job: ", e);
            }
        }


        transactionTasks.forEach(listCompletableFuture -> {
            listCompletableFuture.whenComplete((fetchedTransactions, throwable) -> {
                long end = System.currentTimeMillis();
                logger.info(Thread.currentThread().getId() + " - End fetching transactions task = " + (end - start));
                logger.info(Thread.currentThread().getId() + " - Completed fetching transactions: " + fetchedTransactions);
                List<RawData> rawTrs = fetchedTransactions.stream().map(fetchedTransaction -> new RawData(EthereumBasedListenableTypes.NEW_TRANSACTIONS.toString(), fetchedTransaction.getTransaction().toString(), false, fetchedTransaction.getTransaction().getBlockNumber(), fetchedTransaction.getTopicId())).
                        collect(Collectors.toList());
                if(!rawTrs.isEmpty()){
                    dbManagerFacade.saveRawDataBatch(rawTrs);
                }

            });
        });

        eventTasks.forEach(listCompletableFuture -> {
            listCompletableFuture.whenComplete((fetchedEvents, throwable) -> {
                long end = System.currentTimeMillis();
                logger.info(Thread.currentThread().getId() + " - End fetching events task = " + (end - start));
                logger.info(Thread.currentThread().getId() + " - Completed fetching events: " + fetchedEvents);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String rawEvent = mapper.writeValueAsString(fetchedEvents.get(0));
                    List<RawData> rawEvts = fetchedEvents.stream().map(fetchedEvent -> new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), rawEvent, false, fetchedEvent.getBlockNumber(), fetchedEvent.getTopicId())).
                            collect(Collectors.toList());
                    if(!rawEvts.isEmpty()){
                        dbManagerFacade.saveRawDataBatch(rawEvts);
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error converting contract event data to string: ", throwable);
                }
            });
        });
    }
}
