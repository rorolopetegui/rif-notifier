package org.rif.notifier.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.rif.notifier.constants.ControllerConstants;
import org.rif.notifier.constants.ResponseConstants;
import org.rif.notifier.models.DTO.DTOResponse;
import org.rif.notifier.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Onboarding Resource"})
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServices userServices;

    @ApiOperation(value = "Register to notifications giving an Address",
            response = DTOResponse.class, responseContainer = ControllerConstants.LIST_RESPONSE_CONTAINER)
    @RequestMapping(value = "/users", method = RequestMethod.POST, produces = {ControllerConstants.CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    public ResponseEntity<DTOResponse> register(@RequestParam(name = "address") String address) {
        DTOResponse resp = new DTOResponse();
        if(address != null && !address.isEmpty()){
            if(!userServices.userExists(address)) {
                resp.setData(userServices.saveUser(address));
            }else{
                //User already have an apikey
                resp.setMessage(ResponseConstants.APIKEY_ALREADY_ADDED);
                resp.setStatus(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(resp, resp.getStatus());
    }
}
