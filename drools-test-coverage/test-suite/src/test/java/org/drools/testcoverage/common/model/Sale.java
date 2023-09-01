package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Sale implements Serializable {

    private static final long serialVersionUID = 856715964777288208L;
    private final int saleid;
    private final int custid;
    private final int amount;
    private int discount;
    private boolean rebated = false;

    public Sale(int saleid, int custid, int amount) {
        super();
        this.saleid = saleid;
        this.custid = custid;
        this.amount = amount;
    }

    public int getSaleid() {
        return saleid;
    }

    public int getCustid() {
        return custid;
    }

    public int getAmount() {
        return amount;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isRebated() {
        return rebated;
    }

    public void setRebated() {
        rebated = true;
    }

    @Override
    public String toString() {
        return saleid + " - " + custid + " - " + amount + " - " + discount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sale)) {
            return false;
        }
        Sale oSale = (Sale) o;
        return saleid == oSale.saleid;
    }

    @Override
    public int hashCode() {
        return saleid;
    }
}
