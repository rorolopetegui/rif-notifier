package org.rif.notifier.tests.mocked;

import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.rif.notifier.constants.EventTypeConstants.*;

public class MockTestData {
    public final Topic mockTopic(){
        Subscription sub = new Subscription(new Date(), 1, "0x0", 0, "PAYED");
        Topic tp = new Topic(CONTRACT_EVENT, "0", sub);
        List<TopicParams> params = new ArrayList<>();
        //Params for the topic
        TopicParams contractAddress = new TopicParams(tp, CONTRACT_EVENT_ADDRESS, "0x0000001", 0, "string", false, "");
        TopicParams eventName = new TopicParams(tp, CONTRACT_EVENT_NAME, "EventName", 0, "string", false, "");
        TopicParams param1 = new TopicParams(tp, CONTRACT_EVENT_PARAM, "Param1", 0, "Address", true, "");
        TopicParams param2 = new TopicParams(tp, CONTRACT_EVENT_PARAM, "Param2", 1, "Utf8String", false, "");
        TopicParams param3 = new TopicParams(tp, CONTRACT_EVENT_PARAM, "Param3", 2, "Uint256", false, "");
        params.add(contractAddress);
        params.add(eventName);
        params.add(param1);
        params.add(param2);
        params.add(param3);

        tp.setTopicParams(params);

        return tp;
    }
}
