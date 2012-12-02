package org.drools.marshalling;


public interface ObjectMarshallingStrategyStore {

    // Old marshalling algorithm methods
    public abstract ObjectMarshallingStrategy getStrategy(int index);

    public abstract int getStrategy(Object object);

    // New marshalling algorithm methods
    public abstract ObjectMarshallingStrategy getStrategyObject(String strategyClassName);

    public abstract ObjectMarshallingStrategy getStrategyObject(Object object);

}