package org.rif.notifier.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.managers.datamanagers.DbManagerFacade;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.models.entities.*;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(tags = {"Onboarding Resource"})
@RestController
public class SubscribeController {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private DbManagerFacade dbManagerFacade;

    @ApiOperation(value = "Generate a subscription with an Apikey",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> subscribe(
            @RequestParam(name = "type", required=false) String type,
            @RequestParam(name = "apikey") String apiKey) {
        DTOResponse resp = new DTOResponse();
        String ret = "";
        User us = dbManagerFacade.getUserByApiKey(apiKey);
        if(us != null){
            int myType = Integer.valueOf(type);
            Subscription sub = dbManagerFacade.saveSubscription(new Date(), 0, us.getAddress(), myType, SubscriptionConstants.PENDING_PAYMENT);

            //Pending to generate a lumino-invoice
            ret = LuminoInvoice.generateInvoice(us.getAddress());
        }else{
            resp.setMessage(ResponseConstants.APIKEY_NOT_FOUND);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }

    @ApiOperation(value = "Subscribes to a topic",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribeToTopic", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> subscribeToTopic(
            @RequestParam(name = "apikey") String apiKey,
            @RequestBody Topic topic) {
        DTOResponse resp = new DTOResponse();
        Topic tp = null;
        User us = dbManagerFacade.getUserByApiKey(apiKey);
        if(us != null){
            //Check if the user did subscribe
            Subscription sub = dbManagerFacade.getSubscriptionByAddress(us.getAddress());
            if(sub != null) {
                tp = dbManagerFacade.getTopicByHashCode("" + topic.hashCode());
                if (tp == null) {
                    //Generate Topic and params
                    tp = dbManagerFacade.saveTopic(topic.getType(), "" + topic.hashCode());
                    for(TopicParams param : topic.getTopicParams()){
                        dbManagerFacade.saveTopicParams(
                                tp, param.getType(), param.getValue(), param.getOrder(), param.getValueType(), param.getIndexed()
                        );
                    }
                }
                //Subscribe user to this topic
                UserTopic ut = dbManagerFacade.saveUserTopic(tp, sub);
                //This line is throwing error cause the UT is too heavy
                //resp.setData(ut);
            }else{
                //Return an error because the user still did not create the subscription
                resp.setMessage(ResponseConstants.SUBSCRIPTION_NOT_FOUND);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        }else{
            resp.setMessage(ResponseConstants.APIKEY_NOT_FOUND);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
