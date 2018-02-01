package org.drools.modelcompiler.bigdecimaltest;

import java.math.BigDecimal;

public class Policy {

    private String customer;

    private BigDecimal rate;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
