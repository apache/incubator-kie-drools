package org.drools;

import java.io.Serializable;
import java.util.Arrays;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Primitives implements Serializable {

    private static final long serialVersionUID = -3006488134941876318L;

    private boolean  booleanPrimitive;

    private char     charPrimitive;

    private byte     bytePrimitive;
    private short    shortPrimitive;
    private int      intPrimitive;
    private long     longPrimitive;

    private float    floatPrimitive;
    private float    doublePrimitive;
    private String   stringAttribute;
    private Object[] arrayAttribute;
    private int[]    primitiveArrayAttribute;
    private String[] stringArray;

    private Boolean  booleanWrapper;

    private Object   object;

    public boolean isBooleanPrimitive() {
        return this.booleanPrimitive;
    }

    public void setBooleanPrimitive(final boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }

    public byte getBytePrimitive() {
        return this.bytePrimitive;
    }

    public void setBytePrimitive(final byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }

    public char getCharPrimitive() {
        return this.charPrimitive;
    }

    public void setCharPrimitive(final char charPrimitive) {
        this.charPrimitive = charPrimitive;
    }

    public float getDoublePrimitive() {
        return this.doublePrimitive;
    }

    public void setDoublePrimitive(final float doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }

    public float getFloatPrimitive() {
        return this.floatPrimitive;
    }

    public void setFloatPrimitive(final float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }

    public int getIntPrimitive() {
        return this.intPrimitive;
    }

    public void setIntPrimitive(final int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }

    public long getLongPrimitive() {
        return this.longPrimitive;
    }

    public void setLongPrimitive(final long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }

    public short getShortPrimitive() {
        return this.shortPrimitive;
    }

    public void setShortPrimitive(final short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }

    public Object[] getArrayAttribute() {
        return this.arrayAttribute;
    }

    public void setArrayAttribute(final Object[] arrayAttribute) {
        this.arrayAttribute = arrayAttribute;
    }

    /**
     * @return the primitiveArrayAttribute
     */
    public int[] getPrimitiveArrayAttribute() {
        return this.primitiveArrayAttribute;
    }

    /**
     * @param primitiveArrayAttribute the primitiveArrayAttribute to set
     */
    public void setPrimitiveArrayAttribute(final int[] primitiveArrayAttribute) {
        this.primitiveArrayAttribute = primitiveArrayAttribute;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public String getStringAttribute() {
        return stringAttribute;
    }

    public void setStringAttribute(String stringAttribute) {
        this.stringAttribute = stringAttribute;
    }

    public Boolean getBooleanWrapper() {
        return booleanWrapper;
    }

    public void setBooleanWrapper(Boolean booleanWrapper) {
        this.booleanWrapper = booleanWrapper;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( arrayAttribute );
        result = prime * result + (booleanPrimitive ? 1231 : 1237);
        result = prime * result + ((booleanWrapper == null) ? 0 : booleanWrapper.hashCode());
        result = prime * result + charPrimitive;
        result = prime * result + Float.floatToIntBits( doublePrimitive );
        result = prime * result + Float.floatToIntBits( floatPrimitive );
        result = prime * result + intPrimitive;
        result = prime * result + (int) (longPrimitive ^ (longPrimitive >>> 32));
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + Arrays.hashCode( primitiveArrayAttribute );
        result = prime * result + shortPrimitive;
        result = prime * result + Arrays.hashCode( stringArray );
        result = prime * result + ((stringAttribute == null) ? 0 : stringAttribute.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Primitives other = (Primitives) obj;
        if ( !Arrays.equals( arrayAttribute,
                             other.arrayAttribute ) ) return false;
        if ( booleanPrimitive != other.booleanPrimitive ) return false;
        if ( booleanWrapper == null ) {
            if ( other.booleanWrapper != null ) return false;
        } else if ( !booleanWrapper.equals( other.booleanWrapper ) ) return false;
        if ( bytePrimitive != other.bytePrimitive ) return false;
        if ( charPrimitive != other.charPrimitive ) return false;
        if ( Float.floatToIntBits( doublePrimitive ) != Float.floatToIntBits( other.doublePrimitive ) ) return false;
        if ( Float.floatToIntBits( floatPrimitive ) != Float.floatToIntBits( other.floatPrimitive ) ) return false;
        if ( intPrimitive != other.intPrimitive ) return false;
        if ( longPrimitive != other.longPrimitive ) return false;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        if ( !Arrays.equals( primitiveArrayAttribute,
                             other.primitiveArrayAttribute ) ) return false;
        if ( shortPrimitive != other.shortPrimitive ) return false;
        if ( !Arrays.equals( stringArray,
                             other.stringArray ) ) return false;
        if ( stringAttribute == null ) {
            if ( other.stringAttribute != null ) return false;
        } else if ( !stringAttribute.equals( other.stringAttribute ) ) return false;
        return true;
    }



}