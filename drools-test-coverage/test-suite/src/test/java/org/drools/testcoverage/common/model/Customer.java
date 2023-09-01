package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Customer implements Serializable {

    private static final long serialVersionUID = -1247190303439997770L;
    private int custid;
    private String name;
    private int sales;
    private boolean tenthSaleFree;

    public Customer(int custid, String name) {
        super();
        this.custid = custid;
        this.name = name;
        sales = 0;
        tenthSaleFree = true;
    }

    public int getCustid() {
        return custid;
    }

    public String getName() {
        return name;
    }

    public int getSales() {
        return sales;
    }

    public void incrementSales() {
        sales++;
    }

    public boolean isTenthSaleFree() {
        return tenthSaleFree;
    }

    public void setTenthSaleFree(boolean tenthSaleFree) {
        this.tenthSaleFree = tenthSaleFree;
    }

    @Override
    public String toString() {
        return custid + " - " + name + " - " + sales;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Customer)) {
            return false;
        }
        Customer oCust = (Customer) o;
        return custid == oCust.custid;
    }

    @Override
    public int hashCode() {
        return custid;
    }
}
