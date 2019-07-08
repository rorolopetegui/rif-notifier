package org.rif.notifier.scheduled;

import org.rif.notifier.datamanagers.DbManagerFacade;
import org.rif.notifier.models.datafetching.FetchedBlock;
import org.rif.notifier.models.datafetching.FetchedEvent;
import org.rif.notifier.models.datafetching.FetchedTransaction;
import org.rif.notifier.models.entities.RawData;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.services.blockchain.generic.rootstock.RskBlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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

    //TODO should execute according to the notifier configuration
    @Scheduled(fixedRate = 30000, initialDelay = 2000)
    public void run() throws Exception {

        // TODO Mocked data, must be provided by the subscription manager
        List<EthereumBasedListenable> ethereumBasedListenables = Arrays.asList(new EthereumBasedListenable("0x5159345aab821172e795d56274d0f5fdfdc6abd9", EthereumBasedListenableTypes.CONTRACT_EVENT, Arrays.asList(
                new TypeReference<Address>(true) {
                },
                new TypeReference<Address>(true) {
                },
                new TypeReference<Uint256>() {
                }), "Transfer"),
                new EthereumBasedListenable(null, EthereumBasedListenableTypes.NEW_BLOCK, null, null)
                , new EthereumBasedListenable("0x2", EthereumBasedListenableTypes.NEW_TRANSACTIONS, null, null));

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

                List<RawData> rawTrs = fetchedTransactions.stream().map(fetchedTransaction -> new RawData(EthereumBasedListenableTypes.NEW_TRANSACTIONS.toString(), fetchedTransaction.getTransaction().toString(), false, fetchedTransaction.getTransaction().getBlockNumber())).
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

                List<RawData> rawEvts = fetchedEvents.stream().map(fetchedEvent -> new RawData(EthereumBasedListenableTypes.CONTRACT_EVENT.toString(), fetchedEvent.toString(), false, fetchedEvent.getBlockNumber())).
                        collect(Collectors.toList());
                if(!rawEvts.isEmpty()){
                    dbManagerFacade.saveRawDataBatch(rawEvts);
                }

            });
        });

    }



}
