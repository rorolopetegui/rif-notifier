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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
        //BigInteger from = BigInteger.ZERO;
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
                    logger.info(Thread.currentThread().getId() + "============rawEvent: " + rawEvent);
                    //Deberia conseguir los subs que estan suscritos a este topic, chequear si tienen filtros sobre esto y guardar la data.
                    try {
                        EventRawData rwDt = mapper.readValue(rawEvent, EventRawData.class);
                        List<Subscription> subs = dbManagerFacade.findByContractAddressAndSubscriptionActive(rwDt.getContractAddress());
                        for(Subscription sub : subs){
                            logger.info(Thread.currentThread().getId() + "============Sub: " + sub.getUserAddress());

                            //Need to get the topics for each subscriptions, then stream it if it has filters
                            //Here i have all topics with event name same as rawdata and same contract address
                            List<Topic> topics = sub.getTopics().stream().filter(item ->
                                    item.getType().equals(CONTRACT_EVENT)
                                    && item.getTopicParams().stream().anyMatch(param ->
                                        param.getType().equals(CONTRACT_EVENT_NAME)
                                        && param.getValue().equals(rwDt.getEventName())
                                    )
                                    && item.getTopicParams().stream().anyMatch(param ->
                                        param.getType().equals(CONTRACT_EVENT_ADDRESS)
                                        && param.getValue().equals(rwDt.getContractAddress())
                                    )
                            ).collect(Collectors.toList());
                            logger.info(Thread.currentThread().getId() + "============Topics Created: " + topics.size());
                            //One user can have lots of filters for the same event, so we need to check if this subscription has some filters to apply
                            boolean gotFilters = topics.stream().anyMatch(item -> item.getTopicParams().stream().anyMatch(param ->
                                    param.getType().equals(CONTRACT_EVENT_PARAM)
                                    && param.getFilter() != null
                                    && !param.getFilter().isEmpty()
                            ));
                            logger.info(Thread.currentThread().getId() + "============GotFilters: " + gotFilters);
                            if(gotFilters){
                                //Try filters on rawdata
                                topics.stream().forEach(item -> item.getTopicParams().stream().filter(param ->
                                        param.getType().equals(CONTRACT_EVENT_PARAM)
                                                && param.getFilter() != null
                                                && !param.getFilter().isEmpty()
                                ).forEach(param -> {
                                    //Param with filters
                                    logger.info(Thread.currentThread().getId() + "============Param with filters");
                                    logger.info(Thread.currentThread().getId() + "============Param: " + param.getValue());
                                    logger.info(Thread.currentThread().getId() + "============Param Filter: " + param.getFilter());
                                    fetchedEvents.stream().map(fetchedEvent -> new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), rawEvent, false, fetchedEvent.getBlockNumber(), item.getId()))
                                            .forEach(newItem -> {
                                                EventRawDataParam rawParam = rwDt.getValues().get(param.getOrder());
                                                if(rawParam.getTypeAsString().toLowerCase().equals(param.getValueType().toLowerCase())){
                                                    //Param is the same type as the type getted by the listener
                                                    if(rawParam.getValue().equals(param.getFilter())){
                                                        //RawData has the same info as the filter
                                                        if(rawEvts.size() > 0){
                                                            //Rawdata was not added and need to be added
                                                            if(rawEvts.stream().noneMatch(raw -> raw.getBlock().equals(newItem.getBlock())))
                                                                rawEvts.add(newItem);
                                                        }else{
                                                            rawEvts.add(newItem);
                                                        }
                                                    }
                                                }
                                            });
                                }));
                            }else{
                                logger.info(Thread.currentThread().getId() + "============NO FILTERS");
                                topics.stream().filter(item -> item.getTopicParams().stream().anyMatch(param ->
                                        param.getType().equals(CONTRACT_EVENT_PARAM)
                                                && (param.getFilter() == null
                                                || param.getFilter().isEmpty())
                                )).forEach(tp -> {
                                    //Need to check if the rawdata was not added before
                                    fetchedEvents.stream().map(fetchedEvent -> new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), rawEvent, false, fetchedEvent.getBlockNumber(), tp.getId()))
                                            .forEach(item -> {
                                                if(rawEvts.size() > 0){
                                                    //Rawdata was not added and need to be added
                                                    if(rawEvts.stream().noneMatch(raw ->
                                                            raw.getBlock().equals(item.getBlock())
                                                            && raw.getIdTopic() == item.getIdTopic()
                                                    ))
                                                        rawEvts.add(item);
                                                }else{
                                                    rawEvts.add(item);
                                                }
                                            });
                                });

                            }
                            //Got filters for this CONTRACT_ADDRESS and EVENT_NAME
                            logger.info(Thread.currentThread().getId() + "============gotFilters: " + gotFilters);
                            //logger.info(Thread.currentThread().getId() + "============TopicParams.size: " + params.size());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.info(Thread.currentThread().getId() + "============Exception: " + e.toString());
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
