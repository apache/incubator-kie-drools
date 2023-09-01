package org.drools.model.codegen.execmodel.domain;

import java.util.Date;

public class ChildFactWithObject {

    private final int id;
    private final int parentId;

    private final Object objectValue;

    private int VAr;

    public ChildFactWithObject(final int id, final int parentId, final Object objectValue) {
        this.id = id;
        this.parentId = parentId;
        this.objectValue = objectValue;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    public Short getIdAsShort() {
        return (short) id;
    }

    public Boolean getIdIsEven() {
        return id % 2 == 0;
    }

    public Date getDate() {
        return new Date(id);
    }

    public int getVAr() {
        return VAr;
    }

    public void setVAr( int VAr ) {
        this.VAr = VAr;
    }
}
