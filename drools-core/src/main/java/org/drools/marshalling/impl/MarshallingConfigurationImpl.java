package org.drools.marshalling.impl;

import org.drools.marshalling.ObjectMarshallingStrategy;

public class MarshallingConfigurationImpl
    implements
    MarshallingConfiguration {
    private ObjectMarshallingStrategyStore objectMarshallingStrategyStore;
    private boolean                            marshallProcessInstances;
    private boolean                            marshallWorkItems;

    public MarshallingConfigurationImpl() {
        this( null,
              true,
              true );
    }

    public MarshallingConfigurationImpl(ObjectMarshallingStrategy[] strategies,
                                        boolean marshallProcessInstances,
                                        boolean marshallWorkItems) {
        this.objectMarshallingStrategyStore = new ObjectMarshallingStrategyStore( strategies );
        this.marshallProcessInstances = marshallProcessInstances;
        this.marshallWorkItems = marshallWorkItems;
    }

    public boolean isMarshallProcessInstances() {
        return this.marshallProcessInstances;
    }

    public void setMarshallProcessInstances(boolean marshallProcessInstances) {
        this.marshallProcessInstances = marshallProcessInstances;
    }

    public boolean isMarshallWorkItems() {
        return this.marshallWorkItems;
    }

    public void setMarshallWorkItems(boolean marshallWorkItems) {
        this.marshallWorkItems = marshallWorkItems;
    }

    public ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore() {
        return this.objectMarshallingStrategyStore;
    }

    public void setPlaceholderResolverStrategyFactory(ObjectMarshallingStrategyStore placeholderResolverStrategyFactory) {
        this.objectMarshallingStrategyStore = placeholderResolverStrategyFactory;
    }

}
