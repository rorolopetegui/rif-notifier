package org.rif.notifier.models.subscription;

import org.web3j.abi.TypeReference;

import java.util.List;

public class SubscriptionChannel {
    private String address;
    private AvailableSubscriptionChannels kind;

    public List<TypeReference<?>> getEventFields() {
        return eventFields;
    }



    private List<TypeReference<?>> eventFields;
    private String eventName;

    public SubscriptionChannel(String address, AvailableSubscriptionChannels kind,  List<TypeReference<?>> eventFields, String eventName) {
        this.address = address;
        this.kind = kind;
        this.eventFields = eventFields;
        this.eventName = eventName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AvailableSubscriptionChannels getKind() {
        return kind;
    }

    public void setKind(AvailableSubscriptionChannels kind) {
        this.kind = kind;
    }

    public void setEventFields(List<TypeReference<?>> eventFields) {
        this.eventFields = eventFields;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
