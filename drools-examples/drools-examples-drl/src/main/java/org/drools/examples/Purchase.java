package org.drools.examples;

public class Purchase {
    private Customer customer;
    private Product product;
    
    public Purchase(Customer customer,
                    Product product) {
        this.customer = customer;
        this.product = product;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public Product getProduct() {
        return product;
    }            
}
