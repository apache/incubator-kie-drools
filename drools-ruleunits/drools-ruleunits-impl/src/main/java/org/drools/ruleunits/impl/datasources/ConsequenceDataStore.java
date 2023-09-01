package org.drools.ruleunits.impl.datasources;

public interface ConsequenceDataStore<T> {
    void add(T object);

    void addLogical(T object);

    void update(T object, String... modifiedProperties);

    void remove(T object);
}
