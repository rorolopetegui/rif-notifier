package org.rif.notifier.models.datafetching;

import org.web3j.protocol.core.methods.response.Transaction;

public class FetchedTransaction {

    private Transaction transaction;

    public FetchedTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    //TODO modify tostring because web3j transaction toString isnt implemented.
    @Override
    public String toString() {
        return "FetchedTransaction{" +
                "transaction=" + transaction +
                '}';
    }
}
