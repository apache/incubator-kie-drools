package org.drools.runtime.help.impl;

import org.drools.runtime.rule.FactHandle;


public class RowItemContainer {
    
    private FactHandle factHandle;
    private Object object;
    
    public RowItemContainer() {
        
    }
    
    public RowItemContainer(FactHandle factHandle,
                            Object object) {
        super();
        this.factHandle = factHandle;
        this.object = object;
    }

    public FactHandle getFactHandle() {
        return factHandle;
    }

    public void setFactHandle(FactHandle factHandle) {
        this.factHandle = factHandle;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
