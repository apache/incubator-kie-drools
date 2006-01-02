package org.drools.spi;

import org.drools.spi.Field;

public final class MockField
    implements
    Field {

    private final String name;
    private final Object value;
    private final int    index;

    public MockField(String name,
                     Object value,
                     int index) {
        this.index = index;
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public int getIndex() {
        return this.index;
    }
}