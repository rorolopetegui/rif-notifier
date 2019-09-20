package org.rif.notifier.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.models.entities.Notification;
import org.rif.notifier.notificationmanagers.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = {"Notification Resource"})
@RestController
public class NotificationController {
    private static final String BASE_CONTROLLER_PATH = "/getNotifications";

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationManager notificationManager;

    @ApiOperation(value = "Retrieve notifications for a address",
            response = Notification.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/getNotifications", method = RequestMethod.GET, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<List<Notification>> GetNotifications(@RequestParam(name = "address", required=false) String address) {
        List<Notification> notifications = new ArrayList<>();
        if(address != null && !address.isEmpty()){
            notifications = notificationManager.getNotificationsForAddress(address);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
