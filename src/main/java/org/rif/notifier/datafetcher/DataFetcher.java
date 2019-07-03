package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.subscription.SubscriptionChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Component
public abstract class DataFetcher  {

    @Value("${rsk.blockchain.endpoint}")
    private String rskBlockchainEndpoint;

    protected Web3j web3j;

    @PostConstruct
    public void init(){
        web3j = Web3j.build(new HttpService(rskBlockchainEndpoint));
    }

    @Async
    public abstract CompletableFuture<FetchedData> fetch (SubscriptionChannel subscriptionChannel);
}
