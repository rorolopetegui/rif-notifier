package org.rif.notifier.datafetcher;

import org.rif.notifier.datafetcher.events.EventData;
import org.rif.notifier.models.datafetching.FetchedData;
import org.rif.notifier.models.subscription.SubscriptionChannel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ContractEventDataFetcher extends DataFetcher {


    @Async
    @Override
    public CompletableFuture<FetchedData> fetch(SubscriptionChannel subscriptionChannel) {
        System.out.println(super.web3j);
        long start  = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() +" - Start Contract Data fetching");

        long count = 0l;
        for(long x=0;x<Integer.MAX_VALUE ;x++){
            count+=1;
        }
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() + " - End Contract Data fetching time = "+ (end-start));

        FetchedData result = new FetchedData();
        result.data = String.valueOf(count);
        try {
            List<EventData> eventData = getLogs(null, null, subscriptionChannel.getAddress(), subscriptionChannel.getEventName(), subscriptionChannel.getEventFields());
            System.out.println("Events " + eventData.size());
        }catch (Exception e ){
            e.printStackTrace();
        }

       return  CompletableFuture.completedFuture(result);
    }

    private List<EventData> getLogs(BigInteger from, BigInteger to, String contractAddress, String eventName, List<TypeReference<?>> eventFields)
            throws Exception {
        // Create event object to add it as a Filter
        Event event = createEvent(eventName, eventFields);

        // Encode event signature
        String encodedEventSignature = EventEncoder.encode(event);

        // Apply filter, retrieve the logs
        EthLog filterLogs = applyFilterForEvent(encodedEventSignature, contractAddress, from, to);

        List<EventData> events = new ArrayList<>();

        // Decode event from logs
        for (EthLog.LogResult logResult : filterLogs.getLogs()) {

            Log log = (Log) logResult.get();

            List<String> topics = log.getTopics();

            // The first topic should be the event signature. The next ones the indexed fields
            if (!topics.get(0).equals(encodedEventSignature)) {
                continue;
            }

            // Get indexed values if present
            List<Type> values = new ArrayList<>();
            if (!event.getIndexedParameters().isEmpty()) {
                for (int i = 0; i < event.getIndexedParameters().size(); i++) {
                    Type value =
                            FunctionReturnDecoder.decodeIndexedValue(
                                    topics.get(i + 1), event.getIndexedParameters().get(i));
                    values.add(value);
                }
            }

            // Get non indexed values (Decode data)
            values.addAll(FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters()));

            events.add(new EventData(eventName, values, log.getBlockNumber(), contractAddress));
        }
        return events;
    }

    private Event createEvent(String eventName, List<TypeReference<?>> eventFields) {
        return new Event(eventName, eventFields);
    }

    private EthLog applyFilterForEvent(
            String encodedEventSignature, String contractAddress, BigInteger from, BigInteger to)
            throws Exception {

        EthFilter ethFilter =
                new EthFilter(
                        from == null ? DefaultBlockParameterName.EARLIEST : DefaultBlockParameter.valueOf(from),
                        to == null ? DefaultBlockParameterName.LATEST : DefaultBlockParameter.valueOf(to),
                        contractAddress);

        ethFilter.addSingleTopic(encodedEventSignature);

        return this.web3j.ethGetLogs(ethFilter).send();
    }
}
