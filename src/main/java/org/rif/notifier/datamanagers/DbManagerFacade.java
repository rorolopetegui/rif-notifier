package org.rif.notifier.datamanagers;

import org.rif.notifier.models.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
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
    private NotificationManager notificationManager;

    public RawData saveRawData(String type, String data, boolean processed, BigInteger block){
       return rawDataManager.insert(type,data,processed, block);
    }

    @Transactional
    public List<RawData> saveRawDataBatch(List<RawData> rawData){
        return rawData.stream().map(rawData1 -> rawDataManager.insert(rawData1.getType(), rawData1.getData(), rawData1.isProcessed(), rawData1.getBlock())).collect(Collectors.toList());
    }

    @Transactional
    public List<RawData> updateRawDataBatch(List<RawData> rawData){
        return rawData.stream().map(rawData1 -> rawDataManager.update(rawData1.getId(), rawData1.getType(), rawData1.getData(), rawData1.isProcessed(), rawData1.getBlock())).collect(Collectors.toList());
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

    public List<Subscription> getAllActiveSubscriptions(){
        return subscriptionManager.getActiveSubscriptions();
    }

    public List<UserTopic> getUserTopics(String address){
        return userTopicManager.getUserTopics(address);
    }

    public Topic getTopicById(int Id){
        return topicManager.getTopicById(Id);
    }

    public List<TopicParams> getTopicParamsByIdTopic(int Id){
        return topicParamsManager.getTopicParamsByIdTopic(Id);
    }

    @Transactional
    public List<Notification> saveNotificationBatch(List<Notification> notifications){
        return notifications.stream().map(notification1 -> notificationManager.insert(notification1.getTo_address(), notification1.getTimestamp(), notification1.isSended(), notification1.getData())).collect(Collectors.toList());
    }
}
