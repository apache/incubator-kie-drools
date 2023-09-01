package org.drools.xml.support.containers;

import org.kie.api.runtime.rule.FactHandle;


public class RowItemContainer {

    private String identifier;
    private FactHandle factHandle;
    private Object object;

    public RowItemContainer() {

    }

    public RowItemContainer(String identifier,
                            FactHandle factHandle,
                            Object object) {
        super();
        this.identifier = identifier;
        this.factHandle = factHandle;
        this.object = object;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier( String identifier ) {
        this.identifier = identifier;
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
