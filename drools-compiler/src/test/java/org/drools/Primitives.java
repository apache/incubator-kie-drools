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

public class Primitives
    implements
    Serializable {

    private static final long serialVersionUID = -3006488134941876318L;

    private boolean           booleanPrimitive;

    private char              charPrimitive;

    private byte              bytePrimitive;
    private short             shortPrimitive;
    private int               intPrimitive;
    private long              longPrimitive;

    private float             floatPrimitive;
    private double            doublePrimitive;
    private String            stringAttribute;
    private Object[]          arrayAttribute;
    private boolean[]         primitiveBooleanArray;
    private byte[]            primitiveByteArray;
    private short[]           primitiveShortArray;
    private char[]            primitiveCharArray;
    private int[]             primitiveIntArray;
    private long[]            primitiveLongArray;
    private float[]           primitiveFloatArray;
    private double[]          primitiveDoubleArray;
    private String[]          stringArray;

    private Boolean           booleanWrapper;

    private Object            object;

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

    public double getDoublePrimitive() {
        return this.doublePrimitive;
    }

    public void setDoublePrimitive(final double doublePrimitive) {
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
    
    public boolean[] getPrimitiveBooleanArray() {
        return primitiveBooleanArray;
    }

    public void setPrimitiveBooleanArray(boolean[] primitiveBooleanArray) {
        this.primitiveBooleanArray = primitiveBooleanArray;
    }

    public byte[] getPrimitiveByteArray() {
        return primitiveByteArray;
    }

    public void setPrimitiveByteArray(byte[] primitiveByteArray) {
        this.primitiveByteArray = primitiveByteArray;
    }

    public short[] getPrimitiveShortArray() {
        return primitiveShortArray;
    }

    public void setPrimitiveShortArray(short[] primitiveShortArray) {
        this.primitiveShortArray = primitiveShortArray;
    }

    public char[] getPrimitiveCharArray() {
        return primitiveCharArray;
    }

    public void setPrimitiveCharArray(char[] primitiveCharArray) {
        this.primitiveCharArray = primitiveCharArray;
    }

    /**
     * @return the primitiveArrayAttribute
     */
    public int[] getPrimitiveIntArray() {
        return this.primitiveIntArray;
    }

    /**
     * @param primitiveArrayAttribute the primitiveArrayAttribute to set
     */
    public void setPrimitiveIntArray(final int[] primitiveArrayAttribute) {
        this.primitiveIntArray = primitiveArrayAttribute;
    }

    public long[] getPrimitiveLongArray() {
        return primitiveLongArray;
    }

    public void setPrimitiveLongArray(long[] primitiveLongArray) {
        this.primitiveLongArray = primitiveLongArray;
    }

    public float[] getPrimitiveFloatArray() {
        return primitiveFloatArray;
    }

    public void setPrimitiveFloatArray(float[] floatDoubleArray) {
        this.primitiveFloatArray = floatDoubleArray;
    }

    public double[] getPrimitiveDoubleArray() {
        return primitiveDoubleArray;
    }

    public void setPrimitiveDoubleArray(double[] primitiveDoubleArrayAttribute) {
        this.primitiveDoubleArray = primitiveDoubleArrayAttribute;
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
        long doubeAsLongBits = Double.doubleToLongBits( doublePrimitive );
        result = prime * result + (int) (doubeAsLongBits ^ (doubeAsLongBits >>> 32));
        result = prime * result + Float.floatToIntBits( floatPrimitive );
        result = prime * result + intPrimitive;
        result = prime * result + (int) (longPrimitive ^ (longPrimitive >>> 32));
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + Arrays.hashCode( primitiveIntArray );
        result = prime * result + Arrays.hashCode( primitiveDoubleArray );
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
        if ( Double.doubleToLongBits( doublePrimitive ) != Double.doubleToLongBits( other.doublePrimitive ) ) return false;
        if ( Float.floatToIntBits( floatPrimitive ) != Float.floatToIntBits( other.floatPrimitive ) ) return false;
        if ( intPrimitive != other.intPrimitive ) return false;
        if ( longPrimitive != other.longPrimitive ) return false;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        if ( !Arrays.equals( primitiveIntArray,
                             other.primitiveIntArray ) ) return false;
        if ( !Arrays.equals( primitiveDoubleArray,
                             other.primitiveDoubleArray ) ) return false;
        if ( shortPrimitive != other.shortPrimitive ) return false;
        if ( !Arrays.equals( stringArray,
                             other.stringArray ) ) return false;
        if ( stringAttribute == null ) {
            if ( other.stringAttribute != null ) return false;
        } else if ( !stringAttribute.equals( other.stringAttribute ) ) return false;
        return true;
    }

}