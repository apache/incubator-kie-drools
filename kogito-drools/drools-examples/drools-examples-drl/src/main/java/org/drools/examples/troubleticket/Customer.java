/**
 * 
 */
package org.drools.examples.troubleticket;

public class Customer {
    private String name;
    private String subscription;
    private String project;

    public Customer() {

    }

    public Customer(final String name,
                    final String project,
                    final String subscription) {
        super();
        this.name = name;
        this.project = project;
        this.subscription = subscription;
    }

    public String getName() {
        return this.name;
    }

    public String getSubscription() {
        return this.subscription;
    }
    
    public String getProject() {
        return this.project;
    }

    public String toString() {
        return "[Customer " + this.name + " : " + this.project + " : " + this.subscription + "]";
    }

}