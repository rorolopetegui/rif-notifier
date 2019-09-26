package org.rif.notifier.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.datamanagers.DbManagerFacade;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.models.entities.UserTopic;
import org.rif.notifier.services.blockchain.lumino.LuminoInvoice;
import org.rif.notifier.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api(tags = {"Notification Resource"})
@RestController
public class OnBoardingController {
    private static final String BASE_CONTROLLER_PATH = "/registerToNotifications";

    private static final Logger logger = LoggerFactory.getLogger(OnBoardingController.class);

    @Autowired
    private DbManagerFacade dbManagerFacade;

    @ApiOperation(value = "Register to notifications giving an Address",
            response = String.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/registerToNotifications", method = RequestMethod.GET, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<String> register(@RequestParam(name = "address") String address) {
        String apiKey = "";
        if(address != null && !address.isEmpty()){
            if(dbManagerFacade.getUserByAddress(address) == null) {
                apiKey = Utils.generateNewToken();
                dbManagerFacade.saveUser(address, apiKey);
            }else{
                //User already have an apikey
                //return new ResponseEntity<>(apiKey, HttpStatus.);
            }
        }
        return new ResponseEntity<>(apiKey, HttpStatus.OK);
    }

    @ApiOperation(value = "Generate a subscription with an Apikey",
            response = String.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<String> subscribe(
            @RequestParam(name = "type", required=false) String type,
            @RequestParam(name = "apikey") String apiKey) {
        String ret = "";
        User us = dbManagerFacade.getUserByApiKey(apiKey);
        if(us != null){
            int myType = Integer.valueOf(type);
            Subscription sub = dbManagerFacade.saveSubscription(new Date(), 0, us.getAddress(), myType, SubscriptionConstants.PENDING_PAYMENT);

            //Pending to generate a lumino-invoice
            ret = LuminoInvoice.generateInvoice(us.getAddress());
        }

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "Generate a subscription with an Apikey",
            response = Topic.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribeToTopic", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<Topic> subscribeToTopic(
            @RequestParam(name = "apikey") String apiKey,
            @RequestBody Topic topic) {
        Topic tp = null;
        User us = dbManagerFacade.getUserByApiKey(apiKey);
        logger.info(Thread.currentThread().getId() + "======= Checking if the user api key corresponds to a user");
        if(us != null && false){
            //Check if the user did subscribe
            Subscription sub = dbManagerFacade.getSubscriptionByAddress(us.getAddress());
            if(sub != null) {
                logger.info(Thread.currentThread().getId() + "======= Checking if the topic hash already exists");
                tp = dbManagerFacade.getTopicByHashCode("" + topic.getHashCode());
                if (tp == null) {
                    logger.info(Thread.currentThread().getId() + "======= Generating topic, cause it doesnt exists");
                    //Generate Topic and params
                    tp = dbManagerFacade.saveTopic(topic);
                }
                logger.info(Thread.currentThread().getId() + "======= Subscribing to topic");
                //Subscribe user to this topic
                UserTopic ut = dbManagerFacade.saveUserTopic(tp, sub);

            }else{
                //Return an error because the user still did not create the subscription
                logger.info(Thread.currentThread().getId() + "======= user still did not create the subscription");
            }
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }
}
