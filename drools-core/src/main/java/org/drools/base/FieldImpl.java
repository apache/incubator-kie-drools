/**
 * 
 */
package org.drools.base;

import org.drools.spi.FieldValue;

public class FieldImpl
    implements
    FieldValue {
    private Object value;

    public FieldImpl(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean equals(Object other) {
        if ( this == other ) {
            return true;
        }
        if ( !(other instanceof FieldImpl) ) {
            return false;
        }
        FieldImpl field = (FieldImpl) other;

        return (((this.value == null) && (field.value == null)) || ((this.value != null) && (this.value.equals( field.value ))));
    }

    public int hashCode() {
        if ( this.value != null ) {
            return this.value.hashCode();
        } else {
            return 0;
        }
    }
}