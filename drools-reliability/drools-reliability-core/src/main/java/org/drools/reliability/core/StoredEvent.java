package org.drools.reliability.core;

public interface StoredEvent extends StoredObject {

    @Override
    default boolean isEvent() {
        return true;
    }

    long getTimestamp();

    long getDuration();
}