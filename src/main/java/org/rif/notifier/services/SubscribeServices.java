package org.rif.notifier.services;

import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SubscribeServices  {
    @Autowired
    private DbManagerFacade dbManagerFacade;

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
            //Perform checks
            

            //Finish checks
            tp = dbManagerFacade.saveTopic(topic.getType(), "" + topic.hashCode(), sub);
            for(TopicParams param : topic.getTopicParams()){
                dbManagerFacade.saveTopicParams(
                        tp, param.getType(), param.getValue(), param.getOrder(), param.getValueType(), param.getIndexed()
                );
            }
        }else{
            //Add topic-subscription relationship
            tp.addSubscription(sub);
            dbManagerFacade.updateTopic(tp);
        }
        //This line is throwing error cause the Json is too large
        //resp.setData(ut);
    }
}
