package org.drools.util.asm;

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
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public int getPersonAge() {
        return personAge;
    }

    public void setPersonAge(int personAge) {
        this.personAge = personAge;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public BigDecimal getPersonWeight() {
        return personWeight;
    }

    public void setPersonWeight(BigDecimal personWeight) {
        this.personWeight = personWeight;
    }

    public char getSomeChar() {
        return someChar;
    }

    public void setSomeChar(char someChar) {
        this.someChar = someChar;
    }

    public long getSomeLong() {
        return someLong;
    }

    public void setSomeLong(long someLong) {
        this.someLong = someLong;
    }

    public short getSomeShort() {
        return someShort;
    }

    public void setSomeShort(short someShort) {
        this.someShort = someShort;
    }

}
