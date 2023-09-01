package org.drools.reliability.core;

import java.io.Serializable;

public class SerializableStoredEvent extends BaseStoredEvent {

    private final Serializable object;

    public SerializableStoredEvent(Object object, boolean propagated, long timestamp, long duration) {
        super(propagated, timestamp, duration);
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("Object must be serializable : " + object.getClass().getCanonicalName());
        }
        this.object = (Serializable) object;
    }

    @Override
    public Serializable getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "SerializableStoredEvent{" +
                "object=" + object +
                ", propagated=" + propagated +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                '}';
    }
}