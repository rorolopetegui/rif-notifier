package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.TopicParams;
import org.rif.notifier.repositories.TopicParamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicParamsManager {
    @Autowired
    private TopicParamsRepository topicParamsRepository;

    public List<TopicParams> getTopicParamsByIdTopic(int id_topic){
        return topicParamsRepository.findByIdTopic(id_topic);
    }
}
