package org.drools.spi;

import org.drools.base.FieldFactory.FieldImpl;
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
    
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof MockField)) {
            return false;
        }
        MockField field = (MockField) other;
        
        return (((this.value == null ) && (field.value == null)) ||
                ((this.value != null ) && (this.value.equals(field.value))));
    }
    
    public int hashCode() {
        return this.value.hashCode();
    }
    
}