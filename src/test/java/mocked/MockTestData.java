package mocked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.models.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockTestData {

    private ObjectMapper mapper = new ObjectMapper();

    public Topic mockTopic() throws IOException {
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

    public Topic mockInvalidTopic() throws IOException {
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

    public List<Notification> mockNotifications(){
        List<Notification> retLst = new ArrayList<>();
        for(int i=0;i<10;i++) {
            Notification notif = new Notification("0x0", new Date(), false, "{id: " + i + ", counter: " + i + "}");
            retLst.add(notif);
        }
        return retLst;
    }

    public Subscription mockSubscription(){
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PAYED_PAYMENT);
        return sub;
    }
    public User mockUser(){
        return new User("0x0", "123456789");
    }
    public SubscriptionType mockSubscriptionType(){
        return new SubscriptionType(1000);
    }
}
