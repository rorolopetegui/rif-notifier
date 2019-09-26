package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.RawData;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.UserTopic;
import org.rif.notifier.repositories.UserTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserTopicManager {

    @Autowired
    private UserTopicRepository userTopicRepository;

    public UserTopic insert(Topic topic, Subscription sub){
        UserTopic ut = new UserTopic(topic, sub);
        UserTopic result = userTopicRepository.save(ut);
        return result;
    }
}
