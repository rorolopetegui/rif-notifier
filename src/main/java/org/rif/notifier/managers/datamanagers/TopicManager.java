package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.constants.TopicTypes;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicManager {
    @Autowired
    private TopicRepository topicRepository;

    public Topic getTopicById(int Id){
        return topicRepository.findById(Id);
    }

    public Topic getTopicByHashCode(String hash){
        return topicRepository.findByHash(hash);
    }

    public Topic insert(TopicTypes type, String hash, Subscription sub){
        Topic tp = new Topic(type, hash, sub);
        return topicRepository.save(tp);
    }

    public Topic update(Topic tp){
        return topicRepository.save(tp);
    }
}
