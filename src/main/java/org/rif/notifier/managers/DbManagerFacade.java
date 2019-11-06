package org.rif.notifier.managers;

import org.rif.notifier.constants.TopicParamTypes;
import org.rif.notifier.constants.TopicTypes;
import org.rif.notifier.managers.datamanagers.*;
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
    private SubscriptionTypeManager subscriptionTypeManager;

    @Autowired
    private TopicManager topicManager;

    @Autowired
    private TopicParamsManager topicParamsManager;

    @Autowired
    private NotifEntityManager notifEntityManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private DataFetcherManager dataFetcherManager;

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

    public List<Subscription> getAllActiveSubscriptionsWithBalance(){
        return subscriptionManager.getAllActiveSubscriptionsWithBalance();
    }

    public List<Subscription> getActiveSubscriptionsByTopicId(int idTopic){
        return subscriptionManager.getActiveSubscriptionsByTopicId(idTopic);
    }

    public List<Subscription> getActiveSubscriptionsByTopicIdWithBalance(int idTopic){
        return subscriptionManager.getActiveSubscriptionsByTopicIdWithBalance(idTopic);
    }

    public List<Subscription> findByContractAddressAndSubscriptionActive(String address){
        return subscriptionManager.findByContractAddressAndSubscriptionActive(address);
    }

    public Subscription getActiveSubscriptionByAddress(String user_address){
        return subscriptionManager.getActiveSubscriptionByAddress(user_address);
    }

    public Subscription getSubscriptionByAddress(String user_address){
        return subscriptionManager.getSubscriptionByAddress(user_address);
    }

    public Subscription createSubscription(Date activeUntil, String userAddress, SubscriptionType type, String state) {
        return subscriptionManager.insert(activeUntil, userAddress, type, state);
    }

    public Subscription updateSubscription(Subscription sub) {
        return subscriptionManager.update(sub);
    }

    public SubscriptionType getSubscriptionTypeByType(int id){ return  subscriptionTypeManager.getSubscriptionTypeById(id); }

    public Topic getTopicById(int Id){
        return topicManager.getTopicById(Id);
    }

    public Topic getTopicByHashCode(String hash){
        return topicManager.getTopicByHashCode(hash);
    }

    public Topic saveTopic(TopicTypes type, String hash, Subscription sub){
        return topicManager.insert(type, hash, sub);
    }

    public Topic updateTopic(Topic tp){
        return topicManager.update(tp);
    }

    public TopicParams saveTopicParams(Topic topic, TopicParamTypes type, String value, int order, String valueType, boolean indexed, String filter){
        return topicParamsManager.insert(topic, type, value, order, valueType, indexed, filter);
    }

    @Transactional
    public List<Notification> saveNotificationBatch(List<Notification> notifications){
        return notifications.stream().map(notificationItem ->
                notifEntityManager.insert(notificationItem.getTo_address(), notificationItem.getTimestamp(), notificationItem.isSended(), notificationItem.getData())
        ).collect(Collectors.toList());
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

    public DataFetcherEntity saveLastBlock(BigInteger lastBlock){
        return dataFetcherManager.insert(lastBlock);
    }

    public BigInteger getLastBlock(){
        return dataFetcherManager.get();
    }
}
