package org.drools.base.field;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldValue;

public class DoubleFieldImpl
    implements
    FieldValue, Externalizable {

    private static final long serialVersionUID = 400L;
    private double      value;

    public DoubleFieldImpl() {
    }

    public DoubleFieldImpl(final double value) {
        this.value = value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value   = in.readDouble();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(value);
    }

    public Serializable getValue() {
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
		return new BigDecimal(this.value).toBigInteger();
	}

}
