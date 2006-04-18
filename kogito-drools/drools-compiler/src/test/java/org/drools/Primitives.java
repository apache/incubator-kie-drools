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
    
    public boolean isBooleanPrimitive() {
        return this.booleanPrimitive;
    }
    public void setBooleanPrimitive(boolean booleanPrimitive) {
        this.booleanPrimitive = booleanPrimitive;
    }
    public byte getBytePrimitive() {
        return this.bytePrimitive;
    }
    public void setBytePrimitive(byte bytePrimitive) {
        this.bytePrimitive = bytePrimitive;
    }
    public char getCharPrimitive() {
        return this.charPrimitive;
    }
    public void setCharPrimitive(char charPrimitive) {
        this.charPrimitive = charPrimitive;
    }
    public float getDoublePrimitive() {
        return this.doublePrimitive;
    }
    public void setDoublePrimitive(float doublePrimitive) {
        this.doublePrimitive = doublePrimitive;
    }
    public float getFloatPrimitive() {
        return this.floatPrimitive;
    }
    public void setFloatPrimitive(float floatPrimitive) {
        this.floatPrimitive = floatPrimitive;
    }
    public int getIntPrimitive() {
        return this.intPrimitive;
    }
    public void setIntPrimitive(int intPrimitive) {
        this.intPrimitive = intPrimitive;
    }
    public long getLongPrimitive() {
        return this.longPrimitive;
    }
    public void setLongPrimitive(long longPrimitive) {
        this.longPrimitive = longPrimitive;
    }
    public short getShortPrimitive() {
        return this.shortPrimitive;
    }
    public void setShortPrimitive(short shortPrimitive) {
        this.shortPrimitive = shortPrimitive;
    }
    
    

}