package org.rif.notifier.datafetcher;

import org.rif.notifier.models.web3Extensions.RSKTypeReference;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.rif.notifier.models.entities.UserTopic;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.rif.notifier.constants.EventTypeConstants.*;

public class MockDatafetcher {

    private static final Logger logger = LoggerFactory.getLogger(MockDatafetcher.class);

    private static final String PATH_TO_TYPES = "org.web3j.abi.datatypes.";

    private static EthereumBasedListenable CreateContractEventListeneable(UserTopic uTopic) throws ClassNotFoundException {
        List<TypeReference<?>> params = new ArrayList<>();
        Topic tp = uTopic.getTopic();
        //Streaming to get the address, eventName and parameters to create the listeneable
        String address = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals("CONTRACT_ADDRESS")).findFirst().get().getValue();
        String eventName = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals("EVENT_NAME")).findFirst().get().getValue();
        List<TopicParams> topicParams = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals("EVENT_PARAM"))
                .collect(Collectors.toList());
        for(TopicParams param : topicParams){
            String value = param.getValueType();
            boolean indexed = param.getIndexed();
            Class myClass;
            //Get the reflection of the datatype
            if(Utils.isClass(PATH_TO_TYPES + value)){
                myClass = Class.forName(PATH_TO_TYPES + value);
            }else{
                myClass = Class.forName(PATH_TO_TYPES + "generated." + value);
            }

            TypeReference paramReference = RSKTypeReference.createWithIndexed(myClass, indexed);

            params.add(paramReference);
        }
        return new EthereumBasedListenable(address, EthereumBasedListenableTypes.CONTRACT_EVENT, params, eventName, tp.getId());
    }

    public static EthereumBasedListenable getEthereumBasedListenableFromTopic(UserTopic uTopic) throws ClassNotFoundException {
        EthereumBasedListenable rtn = null;
        switch (uTopic.getTopic().getType()){
            case CONTRACT_EVENT:
                rtn = CreateContractEventListeneable(uTopic);
                break;
            case NEW_BLOCK:
                rtn = new EthereumBasedListenable(null, EthereumBasedListenableTypes.NEW_BLOCK, null, null, uTopic.getTopic().getId());
                break;
            case NEW_TRANSACTIONS:
                rtn = new EthereumBasedListenable("0x2", EthereumBasedListenableTypes.NEW_TRANSACTIONS, null, null, uTopic.getTopic().getId());
                break;
        }
        return rtn;
    }
}
