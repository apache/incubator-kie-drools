package org.drools.ruleunits.impl.datasources;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.SingletonStore;

public class DataSourceFactoryImpl implements DataSource.Factory {

    @Override
    public <T> DataStream<T> createStream() {
        return new DirectDataStream<>();
    }

    @Override
    public <T> DataStream<T> createBufferedStream(int bufferSize) {
        return new BufferedDataStream<>(bufferSize);
    }

    @Override
    public <T> DataStore<T> createStore() {
        return new ListDataStore<>();
    }

    @Override
    public <T> SingletonStore<T> createSingleton() {
        return new FieldDataStore<>();
    }
}
