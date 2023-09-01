package org.drools.ruleunits.api;

import org.kie.api.internal.utils.KieService;

/**
 * A strongly typed source of data for a {@link RuleUnit}.
 * @param <T> The type of objects managed by this DataSource.
 */
public interface DataSource<T> {

    /**
     * Subscribes this DataSource to a {@link DataProcessor} that will be notified of all the changes occurred
     * to the facts going through the DataSource.
     */
    void subscribe(DataProcessor<T> subscriber);

    interface Factory extends KieService {
        <T> DataStream<T> createStream();

        <T> DataStream<T> createBufferedStream(int bufferSize);

        <T> DataStore<T> createStore();

        <T> SingletonStore<T> createSingleton();
    }

    /**
     * Creates a {@link DataStream}, a DataSource of immutable facts, without any buffer.
     */
    static <T> DataStream<T> createStream() {
        return FactoryHolder.get().createStream();
    }

    /**
     * Creates a {@link DataStream}, a DataSource of immutable facts, without a buffer retaining at most the number of facts defined in bufferSize.
     */
    static <T> DataStream<T> createBufferedStream(int bufferSize) {
        return FactoryHolder.get().createBufferedStream(bufferSize);
    }

    /**
     * Creates a {@link DataStore}, a DataSource of mutable facts.
     */
    static <T> DataStore<T> createStore() {
        return FactoryHolder.get().createStore();
    }

    /**
     * Creates a {@link SingletonStore}, a data store that contains at most one value.
     */
    static <T> SingletonStore<T> createSingleton() {
        return FactoryHolder.get().createSingleton();
    }

    class FactoryHolder {

        private static class LazyHolder {
            private static Factory INSTANCE = KieService.load(Factory.class);
        }

        public static Factory get() {
            return LazyHolder.INSTANCE;
        }
    }
}
