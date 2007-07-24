package org.drools.examples;

public class Discount {
    private Customer customer;
    private int amount;

    public Discount(Customer customer,
                    int amount) {
        this.customer = customer;
        this.amount = amount;
    }    
    
    public Customer getCustomer() {
        return customer;
    }

    public int getAmount() {
        return amount;
    }


    
}
