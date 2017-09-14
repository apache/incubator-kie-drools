package org.drools.model;

public interface Drools {
    void insert(Object object);

    void update(Object object, String... modifiedProperties);

    void update(Object object, BitMask modifiedProperties);

    void delete(Object object);
}