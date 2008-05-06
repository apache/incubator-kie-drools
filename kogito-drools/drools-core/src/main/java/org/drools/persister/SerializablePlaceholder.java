package org.drools.persister;

public class SerializablePlaceholder implements ObjectPlaceholder {
    private final Object object;
    
    public SerializablePlaceholder(Object object) {
        this.object = object;
    }
    
    public Object resolveObject() {
        return object;
    }

}
