package org.drools.serialization.protobuf.marshalling;

import org.kie.api.marshalling.MarshallingConfiguration;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;

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
        if ( strategies != null ) {
            this.objectMarshallingStrategyStore = new ObjectMarshallingStrategyStoreImpl( strategies );
        }
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
