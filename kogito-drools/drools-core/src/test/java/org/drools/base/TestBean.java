package org.drools.base;

import java.util.Collections;
import java.util.List;

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

public class TestBean {
    private final String  name        = "michael";
    private final int     age         = 42;

    private boolean booleanAttr = true;
    private byte    byteAttr    = 1;
    private char    charAttr    = 'a';
    private short   shortAttr   = 3;
    private int     intAttr     = 4;
    private long    longAttr    = 5;
    private float   floatAttr   = 6.0f;
    private double  doubleAttr  = 7.0;
    private List    listAttr    = Collections.EMPTY_LIST;

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isBooleanAttr() {
        return this.booleanAttr;
    }

    public byte getByteAttr() {
        return this.byteAttr;
    }

    public char getCharAttr() {
        return this.charAttr;
    }

    public double getDoubleAttr() {
        return this.doubleAttr;
    }

    public float getFloatAttr() {
        return this.floatAttr;
    }

    public int getIntAttr() {
        return this.intAttr;
    }

    public List getListAttr() {
        return this.listAttr;
    }

    public long getLongAttr() {
        return this.longAttr;
    }

    public short getShortAttr() {
        return this.shortAttr;
    }

}