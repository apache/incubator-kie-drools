package org.drools.facttemplates;

import org.drools.base.ValueType;

public interface FieldTemplate {

    public abstract int getIndex();

    public abstract String getName();

    public abstract ValueType getValueType();

}