package org.rif.notifier.services;

import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;
import org.rif.notifier.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.rif.notifier.constants.EventTypeConstants.*;

@Service
public class SubscribeServices  {
    @Autowired
    private DbManagerFacade dbManagerFacade;

    private static final Logger logger = LoggerFactory.getLogger(SubscribeServices.class);

    private static final String PATH_TO_TYPES = "org.web3j.abi.datatypes.";

    public String createSubscription(User us, int subType){
        Subscription sub = dbManagerFacade.saveSubscription(new Date(), 0, us.getAddress(), subType, SubscriptionConstants.PENDING_PAYMENT);
        //Pending to generate a lumino-invoice
        String invoice = LuminoInvoice.generateInvoice(us.getAddress());
        return invoice;
    }

    public Subscription getSubscriptionByAddress(String user_address){
        return dbManagerFacade.getSubscriptionByAddress(user_address);
    }

    public void subscribeToTopic(Topic topic, Subscription sub){
        Topic tp = dbManagerFacade.getTopicByHashCode("" + topic.hashCode());
        if (tp == null) {
            //Generate Topic and params
            tp = dbManagerFacade.saveTopic(topic.getType(), "" + topic.hashCode(), sub);
            for(TopicParams param : topic.getTopicParams()){
                dbManagerFacade.saveTopicParams(
                        tp, param.getType(), param.getValue(), param.getOrder(), param.getValueType(), param.getIndexed(), param.getFilter()
                );
            }
        }else{
            //Add topic-subscription relationship
            tp.addSubscription(sub);
            dbManagerFacade.updateTopic(tp);
        }
        //This line was throwing error cause the Json is too large
        //resp.setData(ut);
    }

    public boolean validateTopic(Topic topic){
        boolean ret = false;
        if(topic.getType() != null) {
            switch (topic.getType()) {
                case CONTRACT_EVENT:
                    ret = validateContractEventParams(topic.getTopicParams());
                    break;
                case PENDING_TRANSACTIONS:
                case NEW_BLOCK:
                case NEW_TRANSACTIONS:
                    ret = true;
                    break;
            }
        }
        return ret;
    }
    //As this is a Contract, we need to check that the user sends a CONTRACT_ADDRESS, EVENT_NAME and at least 1 EVENT_PARAM
    //Also we need to check that the value type of each param be a valid type of web3java
    private boolean validateContractEventParams(List<TopicParams> params){
        boolean ret = true;
        int counterAddress = 0, counterName = 0, counterParam = 0;
        for(TopicParams param : params){
            if(param.getType() != null) {
                switch (param.getType()) {
                    case CONTRACT_EVENT_ADDRESS:
                        counterAddress++;
                        if(param.getValue() == null || param.getValue().isEmpty())
                            ret = false;
                        break;
                    case CONTRACT_EVENT_NAME:
                        counterName++;
                        if(param.getValue() == null || param.getValue().isEmpty())
                            ret = false;
                        break;
                    case CONTRACT_EVENT_PARAM:
                        counterParam++;
                        if(param.getValue() == null || param.getValue().isEmpty() || !isWeb3Type(param.getValueType()))
                            ret = false;
                        break;
                    default:
                        ret = false;
                    break;
                }
            }else{
                //Type required for each param
                ret = false;
            }
            //No need to keep iterating
            if(!ret)
                break;
        }
        //Checking that the user sends at least 1 contract_address, 1 event name, and 1 parameter (can be more)
        if(counterAddress != 1 && counterName != 1 && counterParam == 0)
            ret = false;
        return ret;
    }
    private boolean isWeb3Type(String type) {
        boolean ret = false;
        if (Utils.isClass(PATH_TO_TYPES + type))
            ret =  true;
        else if(Utils.isClass(PATH_TO_TYPES + "generated." + type))
            ret = true;

        return ret;

    }
}
