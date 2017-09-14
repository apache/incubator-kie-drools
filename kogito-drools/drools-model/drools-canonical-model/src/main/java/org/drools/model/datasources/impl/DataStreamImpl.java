package org.drools.model.datasources.impl;

import org.drools.model.datasources.DataStream;

public class DataStreamImpl<T> extends AbstractObservable implements DataStream<T> {

    @Override
    public void send(T obj) {
        notifyInsert(obj);
    }
}
