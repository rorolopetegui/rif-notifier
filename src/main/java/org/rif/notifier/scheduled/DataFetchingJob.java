package org.rif.notifier.scheduled;

import org.rif.notifier.datafetcher.BlockDataFetcher;
import org.rif.notifier.datafetcher.ContractEventDataFetcher;
import org.rif.notifier.datafetcher.DataFetcher;
import org.rif.notifier.datafetcher.TransactionDataFetcher;
import org.rif.notifier.models.subscription.AvailableSubscriptionChannels;
import org.rif.notifier.models.subscription.SubscriptionChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.Arrays;
import java.util.List;

@Component
public class DataFetchingJob {

    @Autowired
    private ContractEventDataFetcher contractEventDataFetcher;
    @Autowired
    private BlockDataFetcher blockDataFetcher;
    @Autowired
    private TransactionDataFetcher transactionDataFetcher;

    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void run() throws Exception {

        // Mocked data
        List<SubscriptionChannel> subscriptionChannels = Arrays.asList(new SubscriptionChannel("0x5159345aab821172e795d56274d0f5fdfdc6abd9", AvailableSubscriptionChannels.CONTRACT_EVENT,  Arrays.asList(
                new TypeReference<Address>(true) {},
                new TypeReference<Address>(true) {},
                new TypeReference<Uint256>() {}), "Transfer"),
                new SubscriptionChannel(null, AvailableSubscriptionChannels.NEW_BLOCK, null, null)
                ,new SubscriptionChannel("0x2", AvailableSubscriptionChannels.NEW_TRANSACTIONS, null, null));

        //TODO este componente deberia tener 3 listas y llamar polimorficamente
        subscriptionChannels.forEach(subscriptionChannel -> {
            switch (subscriptionChannel.getKind()){
                case NEW_BLOCK:
                    blockDataFetcher.fetch(subscriptionChannel);
                    break;
                case CONTRACT_EVENT:
                    contractEventDataFetcher.fetch(subscriptionChannel);
                    break;
                case NEW_TRANSACTIONS:
                    transactionDataFetcher.fetch(subscriptionChannel);
                    break;
            }
        });
    }
}
