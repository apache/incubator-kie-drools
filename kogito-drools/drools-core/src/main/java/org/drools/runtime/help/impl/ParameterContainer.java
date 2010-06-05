package org.drools.runtime.help.impl;

public class ParameterContainer {
    
    private String identifier;
    private Object object;
    
    public ParameterContainer() {
        
    }
    
    public ParameterContainer(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
