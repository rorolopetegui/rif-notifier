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
            @RequestParam(name = "type", required=false) String type,
            @RequestHeader(value="apiKey") String apiKey) {
        DTOResponse resp = new DTOResponse();
        User us = userServices.getUserByApiKey(apiKey);
        if(us != null){
            if(subscribeServices.getSubscriptionByAddress(us.getAddress()) == null) {
                resp.setData(subscribeServices.createSubscription(us, Integer.parseInt(type)));
            }else{
                resp.setMessage(ResponseConstants.SUBSCRIPTION_ALREADY_ADDED);
                resp.setStatus(HttpStatus.CONFLICT);
            }
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
            @RequestHeader(value="apiKey") String apiKey,
            @RequestBody String sTopic) {
        ObjectMapper mapper = new ObjectMapper();
        Topic topic = null;
        DTOResponse resp = new DTOResponse();
        try {
            topic = mapper.readValue(sTopic, Topic.class);
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
                    resp.setMessage(ResponseConstants.SUBSCRIPTION_NOT_FOUND);
                    resp.setStatus(HttpStatus.CONFLICT);
                }
            }else{
                resp.setMessage(ResponseConstants.APIKEY_NOT_FOUND);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            resp.setMessage(ResponseConstants.TOPIC_VALIDATION_FAILED);
            resp.setStatus(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(resp, resp.getStatus());
    }
}
