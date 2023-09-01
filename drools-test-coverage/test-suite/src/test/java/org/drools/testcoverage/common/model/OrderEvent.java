package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class OrderEvent implements Serializable {

    private String id;
    private String customer;
    private double total;

    public OrderEvent(final String id, final String customer, final double total) {
        super();
        this.id = id;
        this.customer = customer;
        this.total = total;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(final String customer) {
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(final double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "OrderEvent( id=" + id + " customer=" + customer + " total=" + total + " )";
    }
}
