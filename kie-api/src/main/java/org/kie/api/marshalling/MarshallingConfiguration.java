package org.kie.api.marshalling;


public interface MarshallingConfiguration {
    ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore();

    boolean isMarshallProcessInstances();

    boolean isMarshallWorkItems();
}
