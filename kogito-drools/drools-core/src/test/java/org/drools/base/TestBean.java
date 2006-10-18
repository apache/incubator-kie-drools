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

    private final boolean booleanAttr = true;
    private final byte    byteAttr    = 1;
    private final char    charAttr    = 'a';
    private final short   shortAttr   = 3;
    private final int     intAttr     = 4;
    private final long    longAttr    = 5;
    private final float   floatAttr   = 6.0f;
    private final double  doubleAttr  = 7.0;
    private final List    listAttr    = Collections.EMPTY_LIST;

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isBooleanAttr() {
        return booleanAttr;
    }

    public byte getByteAttr() {
        return byteAttr;
    }

    public char getCharAttr() {
        return charAttr;
    }

    public double getDoubleAttr() {
        return doubleAttr;
    }

    public float getFloatAttr() {
        return floatAttr;
    }

    public int getIntAttr() {
        return intAttr;
    }

    public List getListAttr() {
        return listAttr;
    }

    public long getLongAttr() {
        return longAttr;
    }

    public short getShortAttr() {
        return shortAttr;
    }
}