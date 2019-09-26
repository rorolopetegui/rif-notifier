package org.rif.notifier.scheduled;

import org.rif.notifier.managers.datamanagers.DbManagerFacade;
import org.rif.notifier.models.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.rif.notifier.constants.EventTypeConstants.*;


@Component
public class DataProcessorJob {
    private static final Logger logger = LoggerFactory.getLogger(DataFetchingJob.class);

    @Autowired
    private DbManagerFacade dbManagerFacade;
    @Scheduled(fixedRateString = "${notifier.run.fixedRateProcessJob}", initialDelayString = "${notifier.run.fixedDelayProcessJob}")
    public void run() throws Exception {
        //Rawdata to be marked as processed
        List<RawData> processedRows = new ArrayList<>();

        //Bring all the raw data not processed
        List<RawData> rawData = dbManagerFacade.getRawDataByProcessed(false);
        if (rawData.size() > 0) {
            logger.info(Thread.currentThread().getId() + String.format(" - Rawdata not processed = %d", rawData.size()));
            List<Notification> ntfsData = new ArrayList<>();
            rawData.stream().forEach(rawData1 -> {
                String dataForNotification = "";
                List<Subscription> activeSubs = dbManagerFacade.getActiveSubscriptionsByTopicId(rawData1.getIdTopic());
                logger.info(Thread.currentThread().getId() + String.format(" - Active subscriptions for the topic_id (%d) = %d", rawData1.getIdTopic(), activeSubs.size()));
                for (Subscription sub : activeSubs) {
                    if (!processedRows.stream().anyMatch(item -> item.getId().equals(rawData1.getId())))
                        processedRows.add(rawData1);

                    //Here we can add some logic to each type of event
                    switch (rawData1.getType()) {
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
                    ntfsData.add(new Notification(sub.getUserAddress(), new Date(), false, dataForNotification));
                }
            });
            logger.info(Thread.currentThread().getId() + String.format(" - Finished processing notifications, count = %s", ntfsData.size()));
            if (!ntfsData.isEmpty()) {
                List<Notification> sNotfs = dbManagerFacade.saveNotificationBatch(ntfsData);
                logger.info(Thread.currentThread().getId() + String.format(" - Saved all notifications, count = %d", sNotfs.size()));
            }
        }
        logger.info(Thread.currentThread().getId() + String.format(" - Rawdata to mark as processed - %d", processedRows.size()));
        if (processedRows.size() > 0) {
            //Now i need to mark all processed raw data
            processedRows.forEach(item -> item.setProcessed(true));
            dbManagerFacade.updateRawDataBatch(processedRows);
            logger.info(Thread.currentThread().getId() + " - Rawdata settled as processed -");
        }
    }
}