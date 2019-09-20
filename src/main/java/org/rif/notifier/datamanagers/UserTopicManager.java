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
}
