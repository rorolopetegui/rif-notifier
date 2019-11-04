package org.rif.notifier.scheduled;

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

import static org.rif.notifier.constants.TopicParamTypes.*;
import static org.rif.notifier.constants.TopicTypes.*;


@Component
public class DataFetchingJob {

    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private RskBlockchainService rskBlockchainService;

    @Autowired
    private DbManagerFacade dbManagerFacade;

    /**
     * Creates listeneables, then try to get the blockchain events related.
     * When all the events are fetched try to process the rawdata getted calling methods
     */
    @Scheduled(fixedRateString = "${notifier.run.fixedRateFetchingJob}", initialDelayString = "${notifier.run.fixedDelayFetchingJob}")
    public void run() throws Exception {
        // Get latest block for this run
        BigInteger to = rskBlockchainService.getLastBlock();
        BigInteger from = dbManagerFacade.getLastBlock();
        from = from.add(new BigInteger("1"));

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
        dbManagerFacade.saveLastBlock(to);


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
                processFetchedEvents(fetchedEvents);
            });
        });
    }

    /**
     * Iterates through Subscription given a Contract Address.
     * When iterating, it filters by the contract_address and takes all the topics related to that.
     * Then it checks if the user has filters for that Topic, and in that case, it filters the data, if all correct, saves a new RawData in the DB
     * When no filters apply, it directly saves the rawdata
     * @param fetchedEvents Fetched events from the library
     */
    private void processFetchedEvents(List<FetchedEvent> fetchedEvents){
        ObjectMapper mapper = new ObjectMapper();
        List<RawData> rawEvts = new ArrayList<>();
        fetchedEvents.forEach(fetchedEvent -> {
            try {
                String rawEvent = mapper.writeValueAsString(fetchedEvent);
                EventRawData rwDt = mapper.readValue(rawEvent, EventRawData.class);
                List<Subscription> subs = dbManagerFacade.findByContractAddressAndSubscriptionActive(rwDt.getContractAddress());
                for (Subscription sub : subs) {
                    //Here i have all topics with event name same as rawdata and same contract address
                    sub.getTopics().stream().filter(item ->
                            item.getType().equals(CONTRACT_EVENT)
                                    && item.getTopicParams().stream().anyMatch(param ->
                                    param.getType().equals(EVENT_NAME)
                                            && param.getValue().equals(rwDt.getEventName())
                            )
                                    && item.getTopicParams().stream().anyMatch(param ->
                                    param.getType().equals(CONTRACT_ADDRESS)
                                            && param.getValue().equals(rwDt.getContractAddress())
                            )
                    ).forEach(tp -> {
                        rwDt.setTopicId(tp.getId());
                        //One user can have lots of filters for the same event, so we need to check if this subscription has some filters to apply
                        List<TopicParams> filterParams = new ArrayList<>();
                        //Try getting the parameters to be filtered
                        tp.getTopicParams().stream().filter(param ->
                                param.getType().equals(EVENT_PARAM)
                                        && param.getFilter() != null
                                        && !param.getFilter().isEmpty()
                        ).forEach(filterParams::add);
                        RawData newItem = new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), rwDt.toString(), false, fetchedEvent.getBlockNumber(), tp.getId());
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
                                ){
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if(!rawEvts.isEmpty()) {
            dbManagerFacade.saveRawDataBatch(rawEvts);
        }
    }

    /**
     * Gets all topics for active subscriptions an creates a List of listeneables for the library web3
     * It performs checks if the topic was not already added to the list
     * @return Returns a List<EthereumBasedListeneable> to listen to the blockchain events
     * @throws ClassNotFoundException When trying to parse the web3-type creating the listeneable for contract events
     */
    private List<EthereumBasedListenable> getListeneables() throws ClassNotFoundException {
        List<EthereumBasedListenable> ethereumBasedListenables = new ArrayList<>();
        List<Subscription> activeSubs = dbManagerFacade.getAllActiveSubscriptionsWithBalance();
        boolean alreadyAdded;

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
