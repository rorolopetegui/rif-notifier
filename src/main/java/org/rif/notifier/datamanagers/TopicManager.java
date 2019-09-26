package org.rif.notifier.datamanagers;

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

    public Topic insert(Topic tp){
        return topicRepository.save(tp);
    }
}
