package org.rif.notifier.services;

import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.Subscription;
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
}
