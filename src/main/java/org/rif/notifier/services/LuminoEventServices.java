package org.rif.notifier.services;

import org.rif.notifier.constants.TopicParamTypes;
import org.rif.notifier.constants.TopicTypes;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LuminoEventServices {

    private Set<String> tokenList = new HashSet<>();

    public void addToken(String token){
        tokenList.add(token);
    }

    public Set<String> getTokens(){
        return tokenList;
    }

    public boolean isToken(String token){
        return tokenList.contains(token);
    }

    /**
     * Creates a topic for openchannel given a token and participant1 and participant2, if none of the participants where given, it creates a listeneable without filters
     * @param token
     * @param participantOne
     * @param participantTwo
     * @return
     */
    public Topic getTopicForToken(String token, String participantOne, String participantTwo){
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
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant1", 1, "Address", true, (participantOne == null || participantOne.isEmpty()) ? null : participantOne.toLowerCase());
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant2", 2, "Address", true, (participantTwo == null || participantTwo.isEmpty()) ? null : participantTwo.toLowerCase());
            params.add(param);
            param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "settle_timeout", 3, "Uint256", false, null);
            params.add(param);
            topic.setTopicParams(params);
        }
        return topic;
    }
}
