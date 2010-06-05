package org.drools.runtime.help.impl;

public class ObjectsObjectContainer {
    
    private Object containedObject;
    
    public ObjectsObjectContainer(Object object) {
        this.containedObject = object;
    }

    public Object getContainedObject() {
        return containedObject;
    }
}
