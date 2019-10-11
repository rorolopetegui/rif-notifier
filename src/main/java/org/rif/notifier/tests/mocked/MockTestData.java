package org.rif.notifier.tests.mocked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.models.entities.Subscription;
import org.rif.notifier.models.entities.Topic;
import org.rif.notifier.models.entities.TopicParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.rif.notifier.constants.EventTypeConstants.*;

public class MockTestData {

    private ObjectMapper mapper = new ObjectMapper();

    public final Topic mockTopic() throws IOException {
        String sTp = "{" +
                "\"type\": \"CONTRACT_EVENT\"," +
                "\"topicParams\":[" +
                "{" +
                "\"type\": \"CONTRACT_ADDRESS\"," +
                "\"value\": \"0x5ea3dc5fb6f5167d5673e8a370e411cff9a4125f\"," +
                "\"valueType\": \"string\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_NAME\"," +
                "\"value\": \"LogSellArticle\"," +
                "\"valueType\": \"string\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"seller\"," +
                "\"order\": 0," +
                "\"valueType\": \"Address\"," +
                "\"indexed\": 1" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"article\"," +
                "\"order\": 1," +
                "\"valueType\": \"Utf8String\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"price\"," +
                "\"order\": 2," +
                "\"valueType\": \"Uint256\"," +
                "\"indexed\": 0" +
                "}" +
                "]" +
                "}";
        return mapper.readValue(sTp, Topic.class);
    }

    public final Topic mockInvalidTopic() throws IOException {
        String sTp = "{" +
                "\"type\": \"CONTRACT_EVENT\"," +
                "\"topicParams\":[" +
                "{" +
                "\"type\": \"EVENT_NAME\"," +
                "\"value\": \"LogSellArticle\"," +
                "\"valueType\": \"string\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"seller\"," +
                "\"order\": 0," +
                "\"valueType\": \"Address\"," +
                "\"indexed\": 1" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"article\"," +
                "\"order\": 1," +
                "\"valueType\": \"Utf8String\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"price\"," +
                "\"order\": 2," +
                "\"valueType\": \"Uint256\"," +
                "\"indexed\": 0" +
                "}" +
                "]" +
                "}";
        return mapper.readValue(sTp, Topic.class);
    }

}
