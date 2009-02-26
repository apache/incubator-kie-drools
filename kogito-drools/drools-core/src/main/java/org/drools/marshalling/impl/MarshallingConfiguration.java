package org.drools.marshalling.impl;

public interface MarshallingConfiguration {
    ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore();

    boolean isMarshallProcessInstances();

    boolean isMarshallWorkItems();
}
