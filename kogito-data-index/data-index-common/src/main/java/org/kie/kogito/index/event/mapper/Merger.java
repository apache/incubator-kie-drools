package org.kie.kogito.index.event.mapper;

public interface Merger<I, R> {
    boolean accept(Object input);

    R merge(R instance, I input);
}
