package org.rif.notifier.datafetcher;

import org.rif.notifier.models.datafetching.FetchedEvent;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
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
public class ContractEventEthereumBasedDataFetcher extends EthereumBasedDataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ContractEventEthereumBasedDataFetcher.class);


    @Async
    @Override
    public CompletableFuture<List<FetchedEvent>> fetch(EthereumBasedListenable ethereumBasedListenable, BigInteger from, BigInteger to, Web3j web3j) {
       return  CompletableFuture.supplyAsync(() -> {
           long start  = System.currentTimeMillis();
           logger.info(Thread.currentThread().getId() + " - Starting reading events for subscription: "+ ethereumBasedListenable);
           List<FetchedEvent> fetchedEventData = new ArrayList<>();
           try {
               fetchedEventData = getLogs(web3j,from, to, ethereumBasedListenable.getAddress(), ethereumBasedListenable.getEventName(), ethereumBasedListenable.getEventFields());
           } catch (Exception e) {
               logger.error(Thread.currentThread().getId() + " - Error fetching contract data for subscription: "+ ethereumBasedListenable, e);
               return new ArrayList<FetchedEvent>();
           }
           long end = System.currentTimeMillis();
           logger.info(Thread.currentThread().getId() + " - End Contract Data fetching time = "+ (end-start));
           return fetchedEventData;

       }).exceptionally(throwable -> {
           logger.error("Error fetching contract data for subscription: "+ ethereumBasedListenable, throwable);
           return new ArrayList<>();
       });
    }

    private List<FetchedEvent> getLogs(Web3j web3j, BigInteger from, BigInteger to, String contractAddress, String eventName, List<TypeReference<?>> eventFields)
            throws Exception {
        // Create event object to add its signature as a Filter
        Event event =  new Event(eventName, eventFields);

        // Encode event signature
        String encodedEventSignature = EventEncoder.encode(event);

        // Apply filter, retrieve the logs
        EthLog filterLogs = applyFilterForEvent(web3j, encodedEventSignature, contractAddress, from, to);

        List<FetchedEvent> events = new ArrayList<>();

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

            events.add(new FetchedEvent(eventName, values, log.getBlockNumber(), contractAddress));
        }
        return events;
    }


    private EthLog applyFilterForEvent(
           Web3j web3j, String encodedEventSignature, String contractAddress, BigInteger from, BigInteger to)
            throws Exception {

        EthFilter ethFilter =
                new EthFilter(
                        from == null ? DefaultBlockParameterName.EARLIEST : DefaultBlockParameter.valueOf(from),
                        to == null ? DefaultBlockParameterName.LATEST : DefaultBlockParameter.valueOf(to),
                        contractAddress);

        ethFilter.addSingleTopic(encodedEventSignature);

        return web3j.ethGetLogs(ethFilter).send();
    }
}
