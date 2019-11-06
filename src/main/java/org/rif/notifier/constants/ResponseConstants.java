package org.rif.notifier.constants;

public interface ResponseConstants {

    String OK = "OK";

    String INCORRECT_APIKEY = "Apikey not found, first register the user to the notifier service";

    String APIKEY_ALREADY_ADDED = "The user is already registered";

    String ADDRESS_NOT_PROVIDED = "Address is a required param, please insert a correct address";

    String SUBSCRIPTION_NOT_FOUND = "Subscription not found, first try to subscribe";

    String NO_ACTIVE_SUBSCRIPTION = "No active subscription found, check if you are subscribed first, or have a invoice pending of payment";

    String SUBSCRIPTION_ALREADY_ACTIVE = "This subscription is already active";

    String SUBSCRIPTION_ALREADY_ADDED = "This address is already subscribed";

    String SUBSCRIPTION_INCORRECT_TYPE = "The type you select is not a valid one";

    String TOPIC_VALIDATION_FAILED = "Topic structure failed, please review your json";
}
