package org.drools.modelcompiler.bigdecimaltest;

import java.math.BigDecimal;

public class Customer {

    private String code;

    private BigDecimal rate;

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
}
