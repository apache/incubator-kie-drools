package org.drools;

import java.io.Serializable;

public class Address implements Serializable {

    private static final long serialVersionUID = -8519011705761628197L;
    
    private String street;
    private String suburb;
    private String zipCode;

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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
}
