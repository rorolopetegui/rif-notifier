package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.subscription.SubscriptionChannel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BlockDataFetcher extends DataFetcher {

    @Async
    @Override
    public CompletableFuture<FetchedData> fetch(SubscriptionChannel subscriptionChannel) {
        long start  = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() +" - Start Block Data fetching");

        long count = 0l;
        for(long x=0;x<Integer.MAX_VALUE ;x++){
            count+=1;
        }
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId()  +" - End Block Data fetching time = "+ (end-start));

        FetchedData result = new FetchedData();
        result.data = String.valueOf(count);
        return  CompletableFuture.completedFuture(result);
    }
}
