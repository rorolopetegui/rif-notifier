package org.rif.notifier.services;

import org.rif.notifier.constants.TopicParamTypes;
import org.rif.notifier.constants.TopicTypes;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LuminoEventServices {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeServices.class);

    private List<String> tokenList = new ArrayList<>();

    public void addToken(String token){
        tokenList.add(token);
    }

    public List<String> getTokens(){
        return tokenList;
    }

    public boolean isToken(String token){
        return tokenList.stream().anyMatch(item -> item.equals(token));
    }

    public Topic getTopicForToken(String token){
        Topic topic = null;
        if(tokenList.stream().anyMatch(item -> item.equals(token))){
            topic = new Topic();
            topic.setType(TopicTypes.CONTRACT_EVENT);
            List<TopicParams> params = new ArrayList<>();
            TopicParams param = new TopicParams(null, TopicParamTypes.CONTRACT_ADDRESS, token, 0, null, false, null);
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_NAME, "ChannelOpened", 0, null, false, null);
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "channel_identifier", 0, "Uint256", true, null);
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant1", 1, "Address", true, null);
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant2", 2, "Address", true, null);
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "settle_timeout", 3, "Uint256", false, null);
            params.add(param);
            topic.setTopicParams(params);
        }
        return topic;
    }
}
