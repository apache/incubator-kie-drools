package org.drools.decisiontable;

import java.math.BigDecimal;

public class ValueHolder {

    private String name;

    private BigDecimal percentValue;
    private BigDecimal currencyValue1;
    private BigDecimal currencyValue2;
    private BigDecimal currencyValue3;

    private int intValue;
    private double doubleValue;

    public ValueHolder() {
        super();
    }

    public ValueHolder(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentValue() {
        return percentValue;
    }

    public void setPercentValue(BigDecimal percentValue) {
        this.percentValue = percentValue;
    }

    public BigDecimal getCurrencyValue1() {
        return currencyValue1;
    }

    public void setCurrencyValue1(BigDecimal currencyValue1) {
        this.currencyValue1 = currencyValue1;
    }

    public BigDecimal getCurrencyValue2() {
        return currencyValue2;
    }

    public void setCurrencyValue2(BigDecimal currencyValue2) {
        this.currencyValue2 = currencyValue2;
    }

    public BigDecimal getCurrencyValue3() {
        return currencyValue3;
    }

    public void setCurrencyValue3(BigDecimal currencyValue3) {
        this.currencyValue3 = currencyValue3;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public String toString() {
        return "ValueHolder [name=" + name + ", percentValue=" + percentValue + ", currencyValue1=" + currencyValue1 + ", currencyValue2=" + currencyValue2 + ", intValue=" + intValue + ", doubleValue=" + doubleValue +
               "]";
    }

}
