package org.drools;

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

public class Primitives {
    private boolean booleanPrimitive;

    private char    charPrimitive;

    private byte    bytePrimitive;
    private short   shortPrimitive;
    private int     intPrimitive;
    private long    longPrimitive;

    private float   floatPrimitive;
    private float   doublePrimitive;
    private Object[] arrayAttribute;
    private int[]    primitiveArrayAttribute;

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
        return arrayAttribute;
    }

    public void setArrayAttribute(Object[] arrayAttribute) {
        this.arrayAttribute = arrayAttribute;
    }

    /**
     * @return the primitiveArrayAttribute
     */
    public int[] getPrimitiveArrayAttribute() {
        return primitiveArrayAttribute;
    }

    /**
     * @param primitiveArrayAttribute the primitiveArrayAttribute to set
     */
    public void setPrimitiveArrayAttribute(int[] primitiveArrayAttribute) {
        this.primitiveArrayAttribute = primitiveArrayAttribute;
    }

}