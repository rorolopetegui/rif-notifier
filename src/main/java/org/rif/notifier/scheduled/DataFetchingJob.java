package org.rif.notifier.scheduled;

import org.rif.notifier.datamanagers.RawDataManager;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
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

@Component
public class DataFetchingJob {

    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private RskBlockchainService rskBlockchainService;

    @Autowired
    private RawDataManager rawDataManager;

    @Scheduled(fixedRate = 5000, initialDelay = 2000)
    public void run() throws Exception {
        // TODO Mocked data, must read
        List<EthereumBasedListenable> ethereumBasedListenables = Arrays.asList(new EthereumBasedListenable("0x5159345aab821172e795d56274d0f5fdfdc6abd9", EthereumBasedListenableTypes.CONTRACT_EVENT,  Arrays.asList(
                new TypeReference<Address>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint256>() {}), "Transfer"),
                new EthereumBasedListenable(null, EthereumBasedListenableTypes.NEW_BLOCK, null, null)
                ,new EthereumBasedListenable("0x2", EthereumBasedListenableTypes.NEW_TRANSACTIONS, null, null));

        //TODO from-to blocks
        //Fetching
        long start  = System.currentTimeMillis();
         List<CompletableFuture<?>> fetchingTasks = new ArrayList<>();
         for (EthereumBasedListenable subscriptionChannel : ethereumBasedListenables){
             try{
                 switch (subscriptionChannel.getKind()){
                     case NEW_BLOCK:
                         fetchingTasks.add(rskBlockchainService.getBlocks(subscriptionChannel, null, null));
                         break;
                     case CONTRACT_EVENT:
                         fetchingTasks.add( rskBlockchainService.getContractEvents(subscriptionChannel, null, null));
                         break;
                     case NEW_TRANSACTIONS:
                         fetchingTasks.add(rskBlockchainService.getTransactions(subscriptionChannel, null, null));
                         break;
                 }
             }catch (Exception e){
                 logger.error("Error during DataFetching job: ",e);
             }
        }

         // Save the data when data is fetched
        for(CompletableFuture cp : fetchingTasks){
           Object o = cp.whenComplete((o1, o2) -> {
               long end = System.currentTimeMillis();
               logger.info(Thread.currentThread().getId() + " - End task = "+ (end-start));
               //TODO each method of fetching should return a wrapper that gets the type in order to parse the data, or.. we can serialize the object in a generic way.
               rawDataManager.insert("Event", "Blablabla", false, new BigInteger("22"));
               System.out.println("Completed "+o1);
           });

        }


    }
}
