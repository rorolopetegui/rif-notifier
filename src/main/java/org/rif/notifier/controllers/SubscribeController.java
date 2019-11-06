package org.rif.notifier.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.models.entities.*;
import org.rif.notifier.services.SubscribeServices;
import org.rif.notifier.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = {"Onboarding Resource"})
@RestController
public class SubscribeController {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @Autowired
    private SubscribeServices subscribeServices;

    @Autowired
    private UserServices userServices;

    @ApiOperation(value = "Generate a subscription with an Apikey",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> subscribe(
            @RequestParam(name = "type") int type,
            @RequestHeader(value="apiKey") String apiKey) {
        DTOResponse resp = new DTOResponse();
        User us = userServices.getUserByApiKey(apiKey);
        if(us != null){
            SubscriptionType subType = subscribeServices.getSubscriptionTypeByType(type);
            if(subType != null) {
                if (subscribeServices.getActiveSubscriptionByAddress(us.getAddress()) == null) {
                    resp.setData(subscribeServices.createSubscription(us, subType));
                }else{
                    resp.setMessage(ResponseConstants.SUBSCRIPTION_ALREADY_ADDED);
                    resp.setStatus(HttpStatus.CONFLICT);
                }
            }else{
                resp.setMessage(ResponseConstants.SUBSCRIPTION_INCORRECT_TYPE);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        }else{
            resp.setMessage(ResponseConstants.INCORRECT_APIKEY);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }

    @ApiOperation(value = "Subscribes to a topic",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/subscribeToTopic", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> subscribeToTopic(
            @RequestHeader(value="apiKey") String apiKey,
            @RequestBody String userTopic) {
        ObjectMapper mapper = new ObjectMapper();
        Topic topic = null;
        DTOResponse resp = new DTOResponse();
        try {
            topic = mapper.readValue(userTopic, Topic.class);
            User us = userServices.getUserByApiKey(apiKey);
            if(us != null){
                //Check if the user did subscribe
                Subscription sub = subscribeServices.getSubscriptionByAddress(us.getAddress());
                if(sub != null) {
                    if(subscribeServices.validateTopic(topic)){
                        subscribeServices.subscribeToTopic(topic, sub);
                    }else{
                        //Return an error because the user sends a wrong structure of topic
                        resp.setMessage(ResponseConstants.TOPIC_VALIDATION_FAILED);
                        resp.setStatus(HttpStatus.CONFLICT);
                    }
                }else{
                    //Return an error because the user still did not create the subscription
                    resp.setMessage(ResponseConstants.NO_ACTIVE_SUBSCRIPTION);
                    resp.setStatus(HttpStatus.CONFLICT);
                }
            }else{
                resp.setMessage(ResponseConstants.INCORRECT_APIKEY);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            resp.setMessage(ResponseConstants.TOPIC_VALIDATION_FAILED);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }

    @ApiOperation(value = "Activate a subscription",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/activateSubscription", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> activateSubscription(
            @RequestHeader(value="apiKey") String apiKey) {
        DTOResponse resp = new DTOResponse();
        User us = userServices.getUserByApiKey(apiKey);
        if(us != null) {
            Subscription sub = subscribeServices.getSubscriptionByAddress(us.getAddress());
            if (sub != null) {
                if(!sub.getActive()) {
                    resp.setData(subscribeServices.activateSubscription(sub));
                }else{
                    resp.setMessage(ResponseConstants.SUBSCRIPTION_ALREADY_ACTIVE);
                    resp.setStatus(HttpStatus.CONFLICT);
                }
            } else {
                resp.setMessage(ResponseConstants.SUBSCRIPTION_NOT_FOUND);
                resp.setStatus(HttpStatus.CONFLICT);
            }

        }else{
            resp.setMessage(ResponseConstants.INCORRECT_APIKEY);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }
    @ApiOperation(value = "Adds balance to a subscription",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/addBalance", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> addBalance(
            @RequestParam(name = "type") int type,
            @RequestHeader(value="apiKey") String apiKey) {
        DTOResponse resp = new DTOResponse();
        User us = userServices.getUserByApiKey(apiKey);
        if(us != null){
            SubscriptionType subType = subscribeServices.getSubscriptionTypeByType(type);
            if(subType != null) {
                Subscription subscription = subscribeServices.getSubscriptionByAddress(us.getAddress());
                if (subscription != null) {
                    resp.setData(subscribeServices.addBalanceToSubscription(subscription, subType));
                }else{
                    resp.setMessage(ResponseConstants.SUBSCRIPTION_NOT_FOUND);
                    resp.setStatus(HttpStatus.CONFLICT);
                }
            }else{
                resp.setMessage(ResponseConstants.SUBSCRIPTION_INCORRECT_TYPE);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        }else{
            resp.setMessage(ResponseConstants.INCORRECT_APIKEY);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }
}
