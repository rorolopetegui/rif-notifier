package org.rif.notifier.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rif.notifier.datamanagers.DbManagerFacade;
import org.rif.notifier.models.dataprocessor.DataRawProcessed;
import org.rif.notifier.models.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.rif.notifier.constants.EventTypeConstants.*;


@Component
public class DataProcessorJob {
    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private DbManagerFacade dbManagerFacade;
    @Scheduled(fixedRateString = "${notifier.run.fixedRateProcessJob}", initialDelayString = "${notifier.run.fixedDelayProcessJob}")
    public void run() throws Exception {
        //Topics to be marked as processed
        List<RawData> processedRows = new ArrayList<>();
        List<Subscription> activeSubs = dbManagerFacade.getAllActiveSubscriptions();
        logger.info(Thread.currentThread().getId() + " - Active subscriptions = " + activeSubs.size());
        for (Subscription sub : activeSubs) {
            //Getting the topics for each user
            List<UserTopic> usrTopics = dbManagerFacade.getUserTopics(sub.getUserAddress());
            logger.info(Thread.currentThread().getId() + String.format(" - User %s subscribed to %s topics", sub.getUserAddress(), usrTopics.size()));
            for (UserTopic uTopic : usrTopics) {
                //Bring the topic to get more info about it
                Topic topic = dbManagerFacade.getTopicById(uTopic.getIdTopic());
                //Bring topic params for filtering, this can be upgraded only to get the CONTACT_ADDRESS
                List<TopicParams> lstParams = dbManagerFacade.getTopicParamsByIdTopic(topic.getId());

                //Bring all the raw data not processed
                List<RawData> rawData = dbManagerFacade.getRawDataByTypeAndProcessed(topic.getType(), false);
                logger.info(Thread.currentThread().getId() + " - Rawdata not processed = " + rawData.size());
                if(rawData.size() > 0) {
                    //Filter the rawdata to get the topic needed
                    ObjectMapper mapper = new ObjectMapper();
                    //myTest.get(0) will be the Param for contract address
                    List<TopicParams> contractAdd = lstParams.stream()
                            .filter(item ->
                                    item.getType().equals("CONTRACT_ADDRESS")
                            )
                            .collect(Collectors.toList());

                    Predicate<RawData> byTopic = data -> {
                        if (topic.getType().equals(CONTRACT_EVENT)) {
                            try {
                                return data.getType().equals(topic.getType())
                                        && mapper.readValue(data.getData(), DataRawProcessed.class)
                                        .getContractAddress().equals(contractAdd.get(0).getValue());
                            } catch (IOException e) {
                                return false;
                            }
                        } else {
                            return data.getType().equals(topic.getType());
                        }
                    };
                    List<RawData> rawDataByTopic = rawData.stream().filter(byTopic)
                            .collect(Collectors.toList());
                    logger.info(Thread.currentThread().getId() + String.format(" - Rawdata count = %s, for %s topic", rawDataByTopic.size(), topic.getType()));

                    if (rawDataByTopic.size() > 0) {
                        //Checks and adds the topic to mark it as processed later
                        logger.info(Thread.currentThread().getId() + " - Processing notifications");
                        List<Notification> ntfsData = rawDataByTopic.stream().map(rawData1 -> {
                            String dataForNotification = "";
                            logger.info(Thread.currentThread().getId() + " - Processing raw " + rawData1);
                            logger.info(Thread.currentThread().getId() + " - processedRows.indexOf(rawData1) " + processedRows.stream().filter(item -> item.getId().equals(rawData1.getId())).findFirst().isPresent());
                            logger.info(Thread.currentThread().getId() + " - rawData1.id " + rawData1.getId());
                            if (!processedRows.stream().filter(item -> item.getId().equals(rawData1.getId())).findFirst().isPresent())
                                processedRows.add(rawData1);
                            switch (topic.getType()) {
                                case CONTRACT_EVENT:
                                   //break;
                                case NEW_TRANSACTIONS:
                                    //break;
                                case PENDING_TRANSACTIONS:
                                    //break;
                                case NEW_BLOCK:
                                    //break;
                                default:
                                    dataForNotification = rawData1.getData();
                                    break;
                            }
                            return new Notification(sub.getUserAddress(), new Date(), false, dataForNotification);
                        }).collect(Collectors.toList());
                        logger.info(Thread.currentThread().getId() + String.format(" - Finished processing notifications, count = %s", ntfsData.size()));
                        if (!ntfsData.isEmpty()) {
                            List<Notification> sNotfs = dbManagerFacade.saveNotificationBatch(ntfsData);
                            logger.info(Thread.currentThread().getId() + String.format(" - Saved all notifications, count = %s", sNotfs.size()));
                        }
                    }
                }
            }
        }
        logger.info(Thread.currentThread().getId() + " - Rawdata to mark as processed -" + processedRows.size());
        if (processedRows.size() > 0) {
            //Now i need to mark all processed raw data
            processedRows.forEach(item -> item.setProcessed(true));
            dbManagerFacade.updateRawDataBatch(processedRows);
            logger.info(Thread.currentThread().getId() + " - Rawdata settled as processed -");
        }
    }
}