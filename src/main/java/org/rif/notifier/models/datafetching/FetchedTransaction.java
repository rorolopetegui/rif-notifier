package org.rif.notifier.models.datafetching;

import org.web3j.protocol.core.methods.response.Transaction;

public class FetchedTransaction {

    private int topicId;

    private Transaction transaction;

    public FetchedTransaction(Transaction transaction, int topicId) {
        this.transaction = transaction;
        this.topicId = topicId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    //TODO modify tostring because web3j transaction toString isnt implemented.
    @Override
    public String toString() {
        return "FetchedTransaction{" +
                "transaction=" + transaction +
                ",topicId=" + topicId +
                '}';
    }
}
