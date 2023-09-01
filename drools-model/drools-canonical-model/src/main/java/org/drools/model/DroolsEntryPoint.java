package org.drools.model;

public interface DroolsEntryPoint {

    void insert(Object object);

    void insert(Object object, boolean dynamic);

    void update(Object object, String... modifiedProperties);

    void update(Object object, BitMask modifiedProperties);

    void delete(Object object);
}
