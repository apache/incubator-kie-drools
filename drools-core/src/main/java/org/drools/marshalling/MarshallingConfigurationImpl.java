package org.drools.marshalling;

public class MarshallingConfigurationImpl
    implements
    MarshallingConfiguration {
    private PlaceholderResolverStrategyFactory placeholderResolverStrategyFactory;
    private boolean                            marshallProcessInstances;
    private boolean                            marshallWorkItems;
    private boolean                            marshallTimers;

    public MarshallingConfigurationImpl() {
        this( null,
              true,
              true,
              true );
    }

    public MarshallingConfigurationImpl(PlaceholderResolverStrategyFactory placeholderResolverStrategyFactory,
                                        boolean marshallProcessInstances,
                                        boolean marshallWorkItems,
                                        boolean marshallTimers) {
        this.placeholderResolverStrategyFactory = placeholderResolverStrategyFactory;
        this.marshallProcessInstances = marshallProcessInstances;
        this.marshallWorkItems = marshallWorkItems;
        this.marshallTimers = marshallTimers;
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

    public boolean isMarshallTimers() {
        return this.marshallTimers;
    }

    public void setMarshallTimers(boolean marshallTimers) {
        this.marshallTimers = marshallTimers;
    }

    public PlaceholderResolverStrategyFactory getPlaceholderResolverStrategyFactory() {
        return this.placeholderResolverStrategyFactory;
    }

    public void setPlaceholderResolverStrategyFactory(PlaceholderResolverStrategyFactory placeholderResolverStrategyFactory) {
        this.placeholderResolverStrategyFactory = placeholderResolverStrategyFactory;
    }

}
