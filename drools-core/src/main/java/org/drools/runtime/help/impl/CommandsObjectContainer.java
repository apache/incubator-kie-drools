package org.drools.runtime.help.impl;

public class CommandsObjectContainer {
    
    private Object containedObject;
    
    public CommandsObjectContainer(Object object) {
        this.containedObject = object;
    }

    public Object getContainedObject() {
        return containedObject;
    }
}
