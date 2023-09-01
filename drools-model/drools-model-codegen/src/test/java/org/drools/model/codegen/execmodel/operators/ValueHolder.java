package org.drools.model.codegen.execmodel.operators;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ValueHolder {

    private Integer integerValue = null;
    private Long longValue = null;
    private Byte byteValue = null;
    private Character characterValue = null;
    private Short shortValue = null;
    private Float floatValue = null;
    private Double doubleValue = null;
    private BigInteger bigIntegerValue = null;
    private BigDecimal bigDecimalValue = null;

    public ValueHolder() {}

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    public void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public BigInteger getBigIntegerValue() {
        return bigIntegerValue;
    }

    public void setBigIntegerValue(BigInteger bigIntegerValue) {
        this.bigIntegerValue = bigIntegerValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public static Character constantCharacterValue() {
        return Character.valueOf('a');
    }
}
