package org.drools.base.field;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldValue;

public class DoubleFieldImpl
    implements
    FieldValue {

    private static final long serialVersionUID = 400L;
    private final double      value;

    public DoubleFieldImpl(final double value) {
        this.value = value;
    }

    public Object getValue() {
        return new Double( this.value );
    }

    public String toString() {
        return String.valueOf( this.value );
    }

    public boolean getBooleanValue() {
        throw new RuntimeDroolsException( "Conversion to boolean not supported for type double" );
    }

    public byte getByteValue() {
        return (byte) this.value;
    }

    public char getCharValue() {
        return (char) this.value;
    }

    public double getDoubleValue() {
        return this.value;
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
        if ( object == null || !(object instanceof DoubleFieldImpl) ) {
            return false;
        }
        final DoubleFieldImpl other = (DoubleFieldImpl) object;

        return this.value == other.value;
    }

    public int hashCode() {
        return (int) this.value;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isBooleanField() {
        return false;
    }

    public boolean isFloatNumberField() {
        return true;
    }

    public boolean isIntegerNumberField() {
        return false;
    }

    public boolean isObjectField() {
        return false;
    }

    public boolean isCollectionField() {
        return false;
    }

    public boolean isStringField() {
        return false;
    }

	public BigDecimal getBigDecimalValue() {
		return new BigDecimal(this.value);
	}

	public BigInteger getBigIntegerValue() {
		throw new RuntimeDroolsException( "Conversion to BigInteger not supported for type double" );
	}

}
