package org.drools.ruleunits.impl.datasources;

import org.drools.ruleunits.api.DataStream;

public class DirectDataStream<T> extends AbstractDataSource<T> implements DataStream<T> {

    protected DirectDataStream() {

    }

    @Override
    public void append(T value) {
        forEachSubscriber(s -> s.insert(value));
    }
}
