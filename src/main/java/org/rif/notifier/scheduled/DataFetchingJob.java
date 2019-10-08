package org.rif.notifier.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.datafetcher.MockDatafetcher;
import org.rif.notifier.managers.DbManagerFacade;
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
import org.web3j.abi.datatypes.Utf8String;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.rif.notifier.constants.EventTypeConstants.*;


@Component
public class DataFetchingJob {

    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private RskBlockchainService rskBlockchainService;

    @Autowired
    private DbManagerFacade dbManagerFacade;

    @Scheduled(fixedRateString = "${notifier.run.fixedRateFetchingJob}", initialDelayString = "${notifier.run.fixedDelayFetchingJob}")
    public void run() throws Exception {
        // Get latest block for this run
        BigInteger to = rskBlockchainService.getLastBlock();
        BigInteger from = dbManagerFacade.getLastBlock();
        dbManagerFacade.saveLastBlock(to);

        //Fetching
        logger.info(Thread.currentThread().getId() + String.format(" - Starting fetching from %s to %s", from, to));

        long start = System.currentTimeMillis();
        List<CompletableFuture<List<FetchedBlock>>> blockTasks = new ArrayList<>();
        List<CompletableFuture<List<FetchedTransaction>>> transactionTasks = new ArrayList<>();
        List<CompletableFuture<List<FetchedEvent>>> eventTasks = new ArrayList<>();
        List<EthereumBasedListenable> ethereumBasedListenables = getListeneables();
        for (EthereumBasedListenable subscriptionChannel : ethereumBasedListenables) {
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
                    List<RawData> rawEvts = new ArrayList<>();
                    try {
                        EventRawData rwDt = mapper.readValue(rawEvent, EventRawData.class);
                        List<Subscription> subs = dbManagerFacade.findByContractAddressAndSubscriptionActive(rwDt.getContractAddress());
                        for(Subscription sub : subs) {
                            //Here i have all topics with event name same as rawdata and same contract address
                            sub.getTopics().stream().filter(item ->
                                    item.getType().equals(CONTRACT_EVENT)
                                            && item.getTopicParams().stream().anyMatch(param ->
                                            param.getType().equals(CONTRACT_EVENT_NAME)
                                                    && param.getValue().equals(rwDt.getEventName())
                                    )
                                            && item.getTopicParams().stream().anyMatch(param ->
                                            param.getType().equals(CONTRACT_EVENT_ADDRESS)
                                                    && param.getValue().equals(rwDt.getContractAddress())
                                    )
                            ).forEach(tp -> {
                                rwDt.setTopicId(tp.getId());
                                //One user can have lots of filters for the same event, so we need to check if this subscription has some filters to apply
                                List<TopicParams> filterParams = new ArrayList<>();
                                //Try getting the parameters to be filtered
                                tp.getTopicParams().stream().filter(param ->
                                        param.getType().equals(CONTRACT_EVENT_PARAM)
                                                && param.getFilter() != null
                                                && !param.getFilter().isEmpty()
                                ).forEach(filterParams::add);
                                fetchedEvents.stream().map(fetchedEvent -> new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), rwDt.toString(), false, fetchedEvent.getBlockNumber(), tp.getId()))
                                .forEach(newItem -> {
                                    AtomicBoolean tryAdd = new AtomicBoolean(true);
                                    if (filterParams.size() > 0) {
                                        //Got some filters to apply
                                        filterParams.forEach(param -> {
                                            //Param with filters
                                            EventRawDataParam rawParam = rwDt.getValues().get(param.getOrder());
                                            //This need to be checked in a function that checks the types as TYPE_NAME, etc
                                            //Hardcoded because when retrieving from the listener the type is equals to string and not Utf8String
                                            if (!((rawParam.getTypeAsString().toLowerCase().equals(param.getValueType().toLowerCase())
                                                    || (param.getValueType().equals("Utf8String") && rawParam.getTypeAsString().equals(Utf8String.TYPE_NAME)))
                                                    && rawParam.getValue().equals(param.getFilter()))
                                            ) {
                                                //Param is not the same type as the type getted by the listener or not got the same info
                                                tryAdd.set(false);
                                            }
                                        });
                                    }

                                    //Checking if the data was filtered
                                    if (tryAdd.get()) {
                                        if (rawEvts.size() > 0) {
                                            //Rawdata was not added and need to be added
                                            if (rawEvts.stream().noneMatch(raw -> raw.getBlock().equals(newItem.getBlock()) && raw.getIdTopic() == tp.getId()))
                                                rawEvts.add(newItem);
                                        } else {
                                            rawEvts.add(newItem);
                                        }
                                    }
                                });
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(!rawEvts.isEmpty()){
                        dbManagerFacade.saveRawDataBatch(rawEvts);
                    }
                } catch (JsonProcessingException e) {
                    logger.error("Error converting contract event data to string: ", throwable);
                }
            });
        });
    }

    private List<EthereumBasedListenable> getListeneables() throws ClassNotFoundException {
        List<EthereumBasedListenable> ethereumBasedListenables = new ArrayList<>();
        List<Subscription> activeSubs = dbManagerFacade.getAllActiveSubscriptions();
        Boolean alreadyAdded;
        for(Subscription sub : activeSubs){
            Set<Topic> subTopics = sub.getTopics();
            for(Topic tp : subTopics){
                alreadyAdded = false;
                EthereumBasedListenable newListeneable = MockDatafetcher.getEthereumBasedListenableFromTopic(tp);
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
        return ethereumBasedListenables;
    }
}
