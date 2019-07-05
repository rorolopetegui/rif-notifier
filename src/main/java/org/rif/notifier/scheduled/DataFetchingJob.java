package org.rif.notifier.scheduled;

import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.rif.notifier.services.blockchain.RskBlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class DataFetchingJob {


    @Autowired
    private RskBlockchainService rskBlockchainService;

    @Scheduled(fixedRate = 5000, initialDelay = 2000)
    public void run() throws Exception {

        // Mocked data
        List<EthereumBasedListenable> ethereumBasedListenables = Arrays.asList(new EthereumBasedListenable("0x5159345aab821172e795d56274d0f5fdfdc6abd9", EthereumBasedListenableTypes.CONTRACT_EVENT,  Arrays.asList(
                new TypeReference<Address>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint256>() {}), "Transfer"),
                new EthereumBasedListenable(null, EthereumBasedListenableTypes.NEW_BLOCK, null, null)
                ,new EthereumBasedListenable("0x2", EthereumBasedListenableTypes.NEW_TRANSACTIONS, null, null));
         List<CompletableFuture<?>> list = new ArrayList<>();
        //TODO este componente deberia tener 3 listas y llamar polimorficamente
        for (EthereumBasedListenable subscriptionChannel : ethereumBasedListenables){
            switch (subscriptionChannel.getKind()){
                case NEW_BLOCK:
                    list.add(rskBlockchainService.getBlocks(subscriptionChannel, null, null));
                    break;
                case CONTRACT_EVENT:
                    try {
                        list.add( rskBlockchainService.getContractEvents(subscriptionChannel, null, null));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case NEW_TRANSACTIONS:
                    try {

                        list.add(rskBlockchainService.getTransactions(subscriptionChannel, null, null));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()]));
        for(CompletableFuture cp : list){
           Object o = cp.get();
        }


    }
}
