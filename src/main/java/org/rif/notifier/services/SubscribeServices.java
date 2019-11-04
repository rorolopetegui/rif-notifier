package org.rif.notifier.services;

import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.*;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;
import org.rif.notifier.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.rif.notifier.constants.EventTypeConstants.*;

@Service
public class SubscribeServices  {
    @Autowired
    private DbManagerFacade dbManagerFacade;

    private static final Logger logger = LoggerFactory.getLogger(SubscribeServices.class);

    /**
     * Path to web3Types, will be used to check some parameters of a Topic
     */
    private static final String PATH_TO_TYPES = "org.web3j.abi.datatypes.";

    /**
     * Creates a subscription for a user, and a given type of subscription.
     * It creates a luminoInvoice that will be returned for the user to pay it.
     * When the user pays the invoice, the subscription will be activated
     * Actually we are not validating the subscription type, so it can be any number
     * @param us User that will be associated with the subscription
     * @param type Subscription type
     * @return LuminoInvoice string hash
     */
    public String createSubscription(User us, int type){
        SubscriptionType subType = dbManagerFacade.getSubscriptionTypeByType(type);
        Subscription sub = dbManagerFacade.saveSubscription(new Date(), 1, us.getAddress(), subType, SubscriptionConstants.PENDING_PAYMENT);
        //Pending to generate a lumino-invoice
        String invoice = LuminoInvoice.generateInvoice(us.getAddress());
        return invoice;
    }

    public Subscription getSubscriptionByAddress(String user_address){
        return dbManagerFacade.getSubscriptionByAddress(user_address);
    }

    /**
     * Makes the relation between subscription and topic.
     * First checks if the Topic is already created, so if it is, it creates only the relation, in other case it applies logic to the topic sended
     * At this moment the Topic needs to be correctly validated
     * @param topic Topic type fully validated
     * @param sub Subscription type, to be associated with the Topic sended
     */
    public void subscribeToTopic(Topic topic, Subscription sub){
        //Checks if the Topic already exists
        Topic tp = dbManagerFacade.getTopicByHashCode("" + topic.hashCode());
        if (tp == null) {
            //Generate Topic with no params
            tp = dbManagerFacade.saveTopic(topic.getType(), "" + topic.hashCode(), sub);
            switch (topic.getType()) {
                case CONTRACT_EVENT:
                    //Generates params for the contract event
                    for (TopicParams param : topic.getTopicParams()) {
                        dbManagerFacade.saveTopicParams(
                                tp, param.getType(), param.getValue(), param.getOrder(), param.getValueType(), param.getIndexed(), param.getFilter()
                        );
                    }
                    break;
                case PENDING_TRANSACTIONS:
                case NEW_BLOCK:
                case NEW_TRANSACTIONS:
                    //Non of this topics types need params at this moment
                    break;
            }
        }else{
            //Add topic-subscription relationship
            tp.addSubscription(sub);
            dbManagerFacade.updateTopic(tp);
        }
        //This line was throwing error cause the Json is too large
        //resp.setData(ut);
    }

    /**
     * Validates if the user is sending a valid subscription type
     * @param type Type that need to exists in subscription types
     * @return if type exists
     */
    public boolean isSubscriptionTypeValid(int type){
        SubscriptionType subType = dbManagerFacade.getSubscriptionTypeByType(type);
        return subType != null;
    }

    /**
     * Validates a given Topic, it checks if all required fields are correctly setted.
     * For CONTRACT_EVENT it checks that it has all Params like CONTRACT_EVENT_ADDRESS, CONTRACT_EVENT_NAME and at least one CONTRACT_EVENT_PARAM
     * In case of other types like NEW_TRANSACTIONS will be applied other logic
     * @param topic Topic sended by a user, parsed by, to be checked in the method if it is correctly setted
     * @return True in case that the Topic is correctly validated and has all the required fields, false if something's missed
     */
    public boolean validateTopic(Topic topic){
        if(topic.getType() != null) {
            switch (topic.getType()) {
                case CONTRACT_EVENT:
                    return validateContractEventParams(topic.getTopicParams());
                case PENDING_TRANSACTIONS:
                case NEW_BLOCK:
                case NEW_TRANSACTIONS:
            }
        }
        return false;
    }

    /**
     * Checks all the required parameters for a Contract Event, returns false if some parameter is missed
     * Required:
     * -CONTRACT_ADDRESS (Checks if is just 1 param)
     * -EVENT_NAME (Just 1 param)
     * -EVENT_PARAM (Checks if is at least 1)
     * For the EVENT_PARAMS checks if also has a valid web3-type
     * When finding just 1 error, it breaks the for, and returns false
     * @param params List of Params for a CONTRACT_EVENT
     * @return True if all the required params are correctly setted
     */
    private boolean validateContractEventParams(List<TopicParams> params){
    	int counterContractAddress = 0, counterEventName = 0;
    	
    	for (TopicParams param : params) {
    		// if we receive a param without type or value we know is invalid
    		if ((null == param.getType()) || (null == param.getValue()) || param.getValue().isEmpty()) {
    			return false;
    		}
    		String type = param.getType();
    		if(type.equals(CONTRACT_EVENT_ADDRESS) || type.equals(CONTRACT_EVENT_NAME) || type.equals(CONTRACT_EVENT_PARAM)) {
    			switch (type) {
	                case CONTRACT_EVENT_ADDRESS:
	                	counterContractAddress++;
	                    break;
	                case CONTRACT_EVENT_NAME:
	                	counterEventName++;
	                    break;
	            }
    		} else {
    			// if we reach this point it means the parameter has an invalid type
    			return false;
    		}
    	}
    	
        //Checking that the user sends at least 1 contract_address and 1 event name
        return (1 == counterContractAddress) && (1 == counterEventName);
    }

    /**
     * Checks if the given String is a correct Web3Type
     * @param type String to be checked
     * @return True if the type exists in the library
     */
    private boolean isWeb3Type(String type) {
        boolean ret = false;
        if (Utils.isClass(PATH_TO_TYPES + type))
            ret =  true;
        else if(Utils.isClass(PATH_TO_TYPES + "generated." + type))
            ret = true;

        return ret;
    }
}
