package org.rif.notifier.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.managers.datamanagers.DbManagerFacade;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Onboarding Resource"})
@RestController
public class OnBoardingController {
    private static final Logger logger = LoggerFactory.getLogger(OnBoardingController.class);

    @Autowired
    private DbManagerFacade dbManagerFacade;

    @ApiOperation(value = "Register to notifications giving an Address",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/registerToNotifications", method = RequestMethod.GET, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> register(@RequestParam(name = "address") String address) {
        DTOResponse resp = new DTOResponse();
        String apiKey = "";
        if(address != null && !address.isEmpty()){
            if(dbManagerFacade.getUserByAddress(address) == null) {
                apiKey = Utils.generateNewToken();
                resp.setData(dbManagerFacade.saveUser(address, apiKey));
            }else{
                //User already have an apikey
                resp.setMessage(ResponseConstants.APIKEY_ALREADY_ADDED);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(resp, resp.getStatus());
    }
}
