package org.drools.xml.support.containers;

public class CommandsObjectContainer {
    
    private Object containedObject;
    
    public CommandsObjectContainer(Object object) {
        this.containedObject = object;
    }

    public Object getContainedObject() {
        return containedObject;
    }
}
