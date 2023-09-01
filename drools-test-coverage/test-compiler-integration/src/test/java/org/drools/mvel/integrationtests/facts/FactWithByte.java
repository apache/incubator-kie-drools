package org.drools.mvel.integrationtests.facts;

public class FactWithByte {

    private final byte byteValue;
    private final Byte byteObjectValue;

    public FactWithByte(final byte byteValue) {
        this.byteValue = byteValue;
        this.byteObjectValue = byteValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public Byte getByteObjectValue() {
        return byteObjectValue;
    }
}
