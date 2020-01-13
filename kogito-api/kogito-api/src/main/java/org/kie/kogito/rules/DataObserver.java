package org.kie.kogito.rules;

import java.util.function.Consumer;

import org.kie.api.runtime.rule.FactHandle;

public interface DataObserver {

    static <T> DataProcessor<T> of(Consumer<T> consumer) {
        return new DataProcessor<T>() {
            @Override
            public FactHandle insert(DataHandle handle, T object) {
                consumer.accept(object);
                return null;
            }

            @Override
            public void update(DataHandle handle, T object) {
                consumer.accept(object);
            }

            @Override
            public void delete(DataHandle handle) {
                consumer.accept(null);
            }
        };
    }
}
