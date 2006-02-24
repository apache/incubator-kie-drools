package org.drools.spi;

import org.drools.spi.FieldValue;

public final class MockField
    implements
    FieldValue {

    private final Object value;  

    public MockField(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
}