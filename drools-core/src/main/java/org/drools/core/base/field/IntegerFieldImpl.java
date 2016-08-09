/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.field;

import org.drools.core.spi.FieldValue;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public class IntegerFieldImpl
    implements
    FieldValue, Externalizable {

    private static final long serialVersionUID = 510l;
    private int value;

    public IntegerFieldImpl() {

    }

    public IntegerFieldImpl( final int value ) {
        this.value = value;
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        value = in.readInt();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( value );
    }

    public Serializable getValue() {
        return this.value;
    }

    public String toString() {
        return String.valueOf( this.value );
    }

    public boolean getBooleanValue() {
        throw new RuntimeException( "Conversion to boolean not supported for type long" );
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
        return this.value;
    }

    public int getIntValue() {
        return this.value;
    }

    public long getLongValue() {
        return this.value;
    }

    public short getShortValue() {
        return (short) this.value;
    }

    public boolean equals( final Object object ) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || !( object instanceof IntegerFieldImpl ) ) {
            return false;
        }
        final IntegerFieldImpl other = (IntegerFieldImpl) object;

        return this.value == other.value;
    }

    public int hashCode() {
        return this.value;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isBooleanField() {
        return false;
    }

    public boolean isFloatNumberField() {
        return false;
    }

    public boolean isIntegerNumberField() {
        return true;
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
        return new BigDecimal( this.value );
    }

    public BigInteger getBigIntegerValue() {
        return BigInteger.valueOf( this.value );
    }
}