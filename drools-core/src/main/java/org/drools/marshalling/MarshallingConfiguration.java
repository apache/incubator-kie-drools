package org.drools.marshalling;

public interface MarshallingConfiguration {
    PlaceholderResolverStrategyFactory getPlaceholderResolverStrategyFactory();
    boolean isMarshallProcessInstances();
    boolean  isMarshallWorkItems();
    boolean isMarshallTimers();
} 
