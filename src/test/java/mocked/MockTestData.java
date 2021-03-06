package mocked;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.constants.SubscriptionConstants;
import org.rif.notifier.constants.TopicParamTypes;
import org.rif.notifier.constants.TopicTypes;
import org.rif.notifier.models.datafetching.FetchedEvent;
import org.rif.notifier.models.entities.*;
import org.rif.notifier.models.listenable.EthereumBasedListenable;
import org.rif.notifier.models.listenable.EthereumBasedListenableTypes;
import org.rif.notifier.models.web3Extensions.RSKTypeReference;
import org.rif.notifier.util.Utils;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static org.rif.notifier.constants.TopicParamTypes.*;

public class MockTestData {

    private static final String PATH_TO_TYPES = "org.web3j.abi.datatypes.";

    private ObjectMapper mapper = new ObjectMapper();

    public Topic mockTopic() throws IOException {
        String sTp = "{" +
                "\"type\": \"CONTRACT_EVENT\"," +
                "\"topicParams\":[" +
                "{" +
                "\"type\": \"CONTRACT_ADDRESS\"," +
                "\"value\": \"0x0\"," +
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
    public Topic mockTopicWithFilters() throws IOException {
        String sTp = "{" +
                "\"type\": \"CONTRACT_EVENT\"," +
                "\"topicParams\":[" +
                "{" +
                "\"type\": \"CONTRACT_ADDRESS\"," +
                "\"value\": \"0x0\"," +
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
                "\"indexed\": 1," +
                "\"filter\": \"0x913eebc253aeb9d6a42b45b66b690f9c4619fa14\"" +
                "}," +
                "{" +
                "\"type\": \"EVENT_PARAM\"," +
                "\"value\": \"article\"," +
                "\"order\": 1," +
                "\"valueType\": \"Utf8String\"," +
                "\"indexed\": 0," +
                "\"filter\": \"Article 1\"" +
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
    public Topic mockTopicWithoutParams() throws IOException {
        String sTp = "{" +
                "\"type\": \"CONTRACT_EVENT\"," +
                "\"topicParams\":[" +
                "{" +
                "\"type\": \"CONTRACT_ADDRESS\"," +
                "\"value\": \"0x0\"," +
                "\"valueType\": \"string\"," +
                "\"indexed\": 0" +
                "}," +
                "{" +
                "\"type\": \"EVENT_NAME\"," +
                "\"value\": \"LogSellArticle\"," +
                "\"valueType\": \"string\"," +
                "\"indexed\": 0" +
                "}" +
                "]" +
                "}";
        return mapper.readValue(sTp, Topic.class);
    }
    public Topic mockTopicOpenChannelWithoutFilters() {
        Topic topic = new Topic();
        topic.setType(TopicTypes.CONTRACT_EVENT);
        List<TopicParams> params = new ArrayList<>();
        TopicParams param = new TopicParams(null, TopicParamTypes.CONTRACT_ADDRESS, "123456789", 0, null, false, null);
        params.add(param);
        param = new TopicParams(null, TopicParamTypes.EVENT_NAME, "ChannelOpened", 0, null, false, null);
        params.add(param);
        param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "channel_identifier", 0, "Uint256", true, null);
        params.add(param);
        param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant1", 1, "Address", true, null);
        params.add(param);
        param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "participant2", 2, "Address", true,  null);
        params.add(param);
        param = new TopicParams(null, TopicParamTypes.EVENT_PARAM, "settle_timeout", 3, "Uint256", false, null);
        params.add(param);
        topic.setTopicParams(params);
        return topic;
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
        Date date = new Date();
        for(int i=0;i<10;i++) {
            Notification notif = new Notification("0x0", new Timestamp(date.getTime()).toString(), false, "{id: " + i + ", counter: " + i + "}", 0);
            retLst.add(notif);
        }
        return retLst;
    }
    public Subscription mockSubscription() throws IOException {
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PAYED_PAYMENT);
        Topic topic = this.mockTopic();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        sub.setTopics(topics);
        return sub;
    }
    public Subscription mockSubscriptionWithInvalidTopic() throws IOException {
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PAYED_PAYMENT);
        Topic topic = this.mockInvalidTopic();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        sub.setTopics(topics);
        return sub;
    }
    public Subscription mockSubscriptionWithFilters() throws IOException {
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PAYED_PAYMENT);
        Topic topic = this.mockTopicWithFilters();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        sub.setTopics(topics);
        return sub;
    }
    public Subscription mockSubscriptionWithTopicWithoutParameters() throws IOException {
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PAYED_PAYMENT);
        Topic topic = this.mockTopicWithoutParams();
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        sub.setTopics(topics);
        return sub;
    }
    public Subscription mockInactiveSubscription(){
        SubscriptionType type = this.mockSubscriptionType();
        User user = this.mockUser();
        Subscription sub = new Subscription(new Date(), user.getAddress(), type, SubscriptionConstants.PENDING_PAYMENT);
        sub.setActive(false);
        return sub;
    }
    public User mockUser(){
        return new User("0x7bDB21b2d21EE4b30FB4Bb791781F7D17f465309", "123456789");
    }
    public SubscriptionType mockSubscriptionType(){
        return new SubscriptionType(1000);
    }
    public List<Subscription> mockListActiveSubs() throws IOException {
        List<Subscription> lstSubs = new ArrayList<>();
        Set<Topic> lstTopics = new HashSet<>();
        Subscription subscription = mockSubscription();
        Topic topic = mockTopic();
        lstTopics.add(topic);
        subscription.setTopics(lstTopics);
        lstSubs.add(subscription);
        return lstSubs;
    }
    public EthereumBasedListenable mockEthereumBasedListeneable() throws IOException, ClassNotFoundException {
        List<TypeReference<?>> params = new ArrayList<>();
        Topic tp = mockTopic();
        String address = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals(CONTRACT_ADDRESS)).findFirst().get().getValue();
        String eventName = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals(EVENT_NAME)).findFirst().get().getValue();
        List<TopicParams> topicParams = tp.getTopicParams().stream()
                .filter(item -> item.getType().equals(EVENT_PARAM))
                .collect(Collectors.toList());
        for(TopicParams param : topicParams){
            String value = param.getValueType();
            boolean indexed = param.getIndexed();
            Class myClass;
            //Get the reflection of the datatype
            if(Utils.isClass(PATH_TO_TYPES + value)){
                myClass = Class.forName(PATH_TO_TYPES + value);
            }else{
                myClass = Class.forName(PATH_TO_TYPES + "generated." + value);
            }

            TypeReference paramReference = RSKTypeReference.createWithIndexed(myClass, indexed);

            params.add(paramReference);
        }
        return new EthereumBasedListenable(address, EthereumBasedListenableTypes.CONTRACT_EVENT, params, eventName, tp.getId());
    }
    public EthereumBasedListenable mockInvalidEthereumBasedListeneable(){
        return new EthereumBasedListenable("0x0", EthereumBasedListenableTypes.CONTRACT_EVENT, new ArrayList<>(), "InvalidName", 0);
    }
    public FetchedEvent mockFetchedEvent(){
        List<Type > values = new ArrayList<>();
        Address address = new Address("0x913eebc253aeb9d6a42b45b66b690f9c4619fa14");
        Utf8String article = new Utf8String("Article 1");
        Uint256 price = new Uint256(100000);
        values.add(address);
        values.add(article);
        values.add(price);
        FetchedEvent fetchedEvent = new FetchedEvent
                ("LogSellArticle", values, new BigInteger("55"), "0x0", 0);

        return  fetchedEvent;
    }
    public FetchedEvent mockFetchedEventAlternative(){
        List<Type > values = new ArrayList<>();
        Address address = new Address("0x1");
        Utf8String article = new Utf8String("Article 2");
        Uint256 price = new Uint256(10000);
        values.add(address);
        values.add(article);
        values.add(price);
        FetchedEvent fetchedEvent = new FetchedEvent
                ("LogUpdateArticle", values, new BigInteger("80"), "0x0", 1);

        return  fetchedEvent;
    }
    public List<RawData> mockRawData(){
        List<RawData> rtnLst = new ArrayList<>();
        RawData rwDt = new RawData("0","CONTRACT_EVENT", mockFetchedEvent().toString(), false, new BigInteger("55"), mockFetchedEvent().getTopicId());
        rtnLst.add(rwDt);
        rwDt = new RawData("1", "CONTRACT_EVENT", mockFetchedEventAlternative().toString(), false, new BigInteger("60"), mockFetchedEventAlternative().getTopicId());
        rtnLst.add(rwDt);
        return rtnLst;
    }
}
