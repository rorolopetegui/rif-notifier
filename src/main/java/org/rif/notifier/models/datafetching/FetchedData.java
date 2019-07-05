package org.rif.notifier.models.datafetching;

import java.util.List;

// TODO modify according to the data that we are going to retrieve from blockchain
public abstract class FetchedData<T> {
    public List<T> results;
}
