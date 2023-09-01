package org.drools.reliability.core;

import java.io.Serializable;

public class SerializableStoredObject extends BaseStoredObject {

    protected final Serializable object;

    public SerializableStoredObject(Object object, boolean propagated) {
        super(propagated);
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
        return "SerializableStoredObject{" +
                "object=" + object +
                ", propagated=" + propagated +
                '}';
    }
}