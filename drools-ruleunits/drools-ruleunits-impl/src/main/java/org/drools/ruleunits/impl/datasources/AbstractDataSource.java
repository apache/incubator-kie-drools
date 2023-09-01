package org.drools.ruleunits.impl.datasources;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.impl.EntryPointDataProcessor;

public abstract class AbstractDataSource<T> implements DataSource<T> {

    protected final List<DataProcessor> subscribers = new CopyOnWriteArrayList<>();

    protected final List<EntryPointDataProcessor> entryPointSubscribers = new CopyOnWriteArrayList<>();

    @Override
    public void subscribe(DataProcessor<T> processor) {
        if (processor instanceof EntryPointDataProcessor) {
            entryPointSubscribers.add((EntryPointDataProcessor) processor);
        } else {
            subscribers.add(processor);
        }
    }

    protected void forEachSubscriber(Consumer<DataProcessor> consumer) {
        subscribers.forEach(consumer);
        entryPointSubscribers.forEach(consumer);
    }
}
