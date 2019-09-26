package org.rif.notifier.managers.datamanagers;

import org.rif.notifier.models.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbManagerFacade {
    @Autowired
    private RawDataManager rawDataManager;

    @Autowired
    private SubscriptionManager subscriptionManager;

    @Autowired
    private UserTopicManager userTopicManager;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private TopicParamsManager topicParamsManager;

    @Autowired
    private NotifEntityManager notifEntityManager;

    @Autowired
    private UserManager userManager;

    public RawData saveRawData(String type, String data, boolean processed, BigInteger block, int idTopic){
       return rawDataManager.insert(type,data,processed, block, idTopic);
    }

    @Transactional
    public List<RawData> saveRawDataBatch(List<RawData> rawData){
        return rawData.stream().map(rawData1 -> rawDataManager.insert(rawData1.getType(), rawData1.getData(), rawData1.isProcessed(), rawData1.getBlock(), rawData1.getIdTopic())).collect(Collectors.toList());
    }

    @Transactional
    public List<RawData> updateRawDataBatch(List<RawData> rawData){
        return rawData.stream().map(rawData1 -> rawDataManager.update(rawData1.getId(), rawData1.getType(), rawData1.getData(), rawData1.isProcessed(), rawData1.getBlock(), rawData1.getIdTopic())).collect(Collectors.toList());
    }

    public List<RawData> getAllRawData(){
        return rawDataManager.getAllRawData();
    }

    public List<RawData> getRawDataByType(String type){
        return rawDataManager.getRawDataByType(type);
    }

    public List<RawData> getRawDataByProcessed(boolean processed){
        return rawDataManager.getRawDataByProcessed(processed);
    }

    public List<RawData> getRawDataByTypeAndProcessed(String type, boolean processed){
        return rawDataManager.getRawDataByTypeAndProcessed(type, processed);
    }

    public List<RawData> getRawDataFilteredByTopic(){
        return new ArrayList<>();
    }

    public List<Subscription> getAllActiveSubscriptions(){
        return subscriptionManager.getActiveSubscriptions();
    }

    public List<Subscription> getActiveSubscriptionsByTopicId(int idTopic){
        return subscriptionManager.getActiveSubscriptionsByTopicId(idTopic);
    }

    public Subscription getSubscriptionByAddress(String user_address){
        return subscriptionManager.getSubscriptionByAddress(user_address);
    }

    public Subscription saveSubscription(Date activeUntil, int active, String userAddress, int type, String state) {
        return subscriptionManager.insert(activeUntil, active, userAddress, type, state);
    }

    public Topic getTopicById(int Id){
        return topicManager.getTopicById(Id);
    }

    public Topic getTopicByHashCode(String hash){
        return topicManager.getTopicByHashCode(hash);
    }

    public Topic saveTopic(String type, String hash){
        return topicManager.insert(type, hash);
    }

    public TopicParams saveTopicParams(Topic topic, String type, String value, int order, String valueType, boolean indexed){
        return topicParamsManager.insert(topic, type, value, order, valueType, indexed);
    }

    @Transactional
    public List<Notification> saveNotificationBatch(List<Notification> notifications){
        return notifications.stream().map(notification1 -> notifEntityManager.insert(notification1.getTo_address(), notification1.getTimestamp(), notification1.isSended(), notification1.getData())).collect(Collectors.toList());
    }

    public List<Notification> getNotificationByUserAddress(String user_address){
        return notifEntityManager.getNotificationsByUserAddress(user_address);
    }

    public User saveUser(String address, String apiKey){
        return userManager.insert(address, apiKey);
    }

    public User getUserByApiKey(String apiKey){
        return userManager.getUserByApikey(apiKey);
    }

    public User getUserByAddress(String address){
        return userManager.getUserByAddress(address);
    }

    public UserTopic saveUserTopic(Topic topic, Subscription sub){
        return userTopicManager.insert(topic, sub);
    }
}
