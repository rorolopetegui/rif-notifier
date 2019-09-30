package org.rif.notifier.services;

import org.rif.notifier.managers.DbManagerFacade;
import org.rif.notifier.models.entities.User;
import org.rif.notifier.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServices {
    @Autowired
    private DbManagerFacade dbManagerFacade;

    public boolean userExists(String address){
        return dbManagerFacade.getUserByAddress(address) != null;
    }

    public User saveUser(String address){
        String apiKey = Utils.generateNewToken();
        return dbManagerFacade.saveUser(address, apiKey);
    }
}
