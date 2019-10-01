package org.rif.notifier.constants;

public interface ResponseConstants {

    String OK = "OK";

    String APIKEY_NOT_FOUND = "Apikey not found, first register the user to the notifier service";

    String APIKEY_ALREADY_ADDED = "The user is already registered";

    String SUBSCRIPTION_NOT_FOUND = "Subscription not found, first try to subscribe";

    String SUBSCRIPTION_ALREADY_ADDED = "This address is already subscribed";

    String TOPIC_VALIDATION_FAILED = "Topic structure failed, please review your json";
}
