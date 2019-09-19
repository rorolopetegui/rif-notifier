package org.rif.notifier.models.listenable;

import org.web3j.abi.TypeReference;

import java.util.List;

public class EthereumBasedListenable extends Listenable {

    private List<TypeReference<?>> eventFields;
    private String eventName;
    private EthereumBasedListenableTypes kind;
    private int topicId;

    public EthereumBasedListenable(String address, EthereumBasedListenableTypes kind, List<TypeReference<?>> eventFields, String eventName, int topicId) {
        super(address);
        this.kind = kind;
        this.eventFields = eventFields;
        this.eventName = eventName;
        this.topicId = topicId;
    }

    public List<TypeReference<?>> getEventFields() {
        return eventFields;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public EthereumBasedListenableTypes getKind() {
        return kind;
    }

    public void setKind(EthereumBasedListenableTypes kind) {
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

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "EthereumBasedListenable{" +
                "eventFields=" + eventFields +
                ", eventName='" + eventName + '\'' +
                ", kind=" + kind +
                ", address='" + address + '\'' +
                ", topicId='" + topicId + '\'' +
                '}';
    }
}
