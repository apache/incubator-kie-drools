/**
 * 
 */
package org.drools.examples.troubleticket;

public class Ticket {
    private Customer customer;
    private String   status;

    public Ticket() {

    }

    public Ticket(final Customer customer) {
        super();
        this.customer = customer;
        this.status = "New";
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public String toString() {
        return "[Ticket " + this.customer + " : " + this.status + "]";
    }

}