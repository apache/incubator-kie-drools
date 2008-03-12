package org.drools;

import java.io.Serializable;

public class Address implements Serializable {

    private static final long serialVersionUID = -8519011705761628197L;
    
    private String street;
    private String suburb;
    
    public Address() {
        
    }
    
    public Address(String street) {
        this.street = street;
    }
    
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getSuburb() {
        return suburb;
    }
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }
    
}
