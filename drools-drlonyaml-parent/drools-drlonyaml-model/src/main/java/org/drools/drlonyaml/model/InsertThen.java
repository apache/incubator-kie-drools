package org.drools.drlonyaml.model;

public class InsertThen extends AbstractThen {
    private Object insert; // TODO generic object won't be sufficient, we need a way to programmatically-in-yaml indicate the type.

    public Object getInsert() {
        return insert;
    }

    public void setInsert(Object insert) {
        this.insert = insert;
    }
}