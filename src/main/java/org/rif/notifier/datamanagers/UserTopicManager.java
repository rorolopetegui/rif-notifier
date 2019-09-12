package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.UserTopic;
import org.rif.notifier.repositories.UserTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserTopicManager {

    @Autowired
    private UserTopicRepository userTopicRepository;

    public List<UserTopic> getUserTopics(String userAddress){
        List<UserTopic> lst = new ArrayList<>();
        userTopicRepository.findByUserAddress(userAddress).forEach(lst::add);
        return lst;
    }
}
