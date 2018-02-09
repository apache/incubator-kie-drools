package org.drools.model.impl;

import org.drools.model.UnitData;

public class UnitDataImpl<T> extends GlobalImpl<T> implements UnitData<T> {

    public UnitDataImpl( String name ) {
        this(null, name);
    }

    public UnitDataImpl( Class<T> type, String name ) {
        super(type, null, name);
    }

    @Override
    public String toString() {
        return "UnitData " + getName();
    }
}
