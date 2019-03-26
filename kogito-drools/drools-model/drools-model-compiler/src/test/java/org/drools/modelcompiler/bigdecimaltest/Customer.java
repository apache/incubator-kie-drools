package org.drools.modelcompiler.bigdecimaltest;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Customer {

    private String code;

    private BigDecimal rate;
    private BigInteger value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue( BigInteger value ) {
        this.value = value;
    }
}
