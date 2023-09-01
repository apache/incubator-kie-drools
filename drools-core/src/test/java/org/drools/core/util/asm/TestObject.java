package org.drools.core.util.asm;

import java.math.BigDecimal;

public class TestObject {

    private String     personName;
    private int        personAge;
    private BigDecimal personWeight;
    private boolean    happy;
    private long       someLong;
    private char       someChar;
    private short      someShort;

    public boolean isHappy() {
        return this.happy;
    }

    public void setHappy(final boolean happy) {
        this.happy = happy;
    }

    public int getPersonAge() {
        return this.personAge;
    }

    public void setPersonAge(final int personAge) {
        this.personAge = personAge;
    }

    public String getPersonName() {
        return this.personName;
    }

    public void setPersonName(final String personName) {
        this.personName = personName;
    }

    public BigDecimal getPersonWeight() {
        return this.personWeight;
    }

    public void setPersonWeight(final BigDecimal personWeight) {
        this.personWeight = personWeight;
    }

    public char getSomeChar() {
        return this.someChar;
    }

    public void setSomeChar(final char someChar) {
        this.someChar = someChar;
    }

    public long getSomeLong() {
        return this.someLong;
    }

    public void setSomeLong(final long someLong) {
        this.someLong = someLong;
    }

    public short getSomeShort() {
        return this.someShort;
    }

    public void setSomeShort(final short someShort) {
        this.someShort = someShort;
    }

}
