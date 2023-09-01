package org.drools.core.base;

import java.util.Collections;
import java.util.List;

public class TestBean {
    private final String name        = "michael";
    private final int    age         = 42;

    private final boolean      booleanAttr = true;
    private final byte         byteAttr    = 1;
    private final char         charAttr    = 'a';
    private final short        shortAttr   = 3;
    private final int          intAttr     = 4;
    private final long         longAttr    = 5;
    private final float        floatAttr   = 6.0f;
    private final double       doubleAttr  = 7.0;
    private final List         listAttr    = Collections.EMPTY_LIST;
    private final Object       nullAttr    = null;

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

    public Object getNullAttr() {
        return this.nullAttr;
    }
}
