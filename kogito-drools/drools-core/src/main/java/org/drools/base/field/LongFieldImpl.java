package org.drools.base.field;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldValue;

public class LongFieldImpl
    implements
    FieldValue {

    private static final long serialVersionUID = 320;
    private long              value;

    public LongFieldImpl(final long value) {
        this.value = value;
    }

    public Object getValue() {
        return new Long(this.value);
    }

    public String toString() {
        return String.valueOf( this.value );
    }

    public boolean getBooleanValue() {
        throw new RuntimeDroolsException( "Conversion to boolean not supported for type long");
    }

    public byte getByteValue() {
        return (byte) this.value;
    }

    public char getCharValue() {
        return (char) this.value;
    }

    public double getDoubleValue() {
        return (double) this.value;
    }

    public float getFloatValue() {
        return (float) this.value;
    }

    public int getIntValue() {
        return (int) this.value;
    }

    public long getLongValue() {
        return (long) this.value;
    }

    public short getShortValue() {
        return (short) this.value;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || !(object instanceof LongFieldImpl) ) {
            return false;
        }
        final LongFieldImpl other = (LongFieldImpl) object;

        return this.value == other.value;
    }

    public int hashCode() {
        return (int) this.value;
    }
}
