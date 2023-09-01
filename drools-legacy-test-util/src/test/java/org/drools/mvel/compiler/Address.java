package org.drools.mvel.compiler;

import java.io.Serializable;

public class Address implements Serializable {

    private static final long serialVersionUID = 510l;
    
    private String street;
    private String suburb;
    private String zipCode;

    public Address() {

    }
    
    

    public Address(String street,
                   String suburb,
                   String zipCode) {
        this.street = street;
        this.suburb = suburb;
        this.zipCode = zipCode;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + ((suburb == null) ? 0 : suburb.hashCode());
        result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Address other = (Address) obj;
        if ( street == null ) {
            if ( other.street != null ) return false;
        } else if ( !street.equals( other.street ) ) return false;
        if ( suburb == null ) {
            if ( other.suburb != null ) return false;
        } else if ( !suburb.equals( other.suburb ) ) return false;
        if ( zipCode == null ) {
            if ( other.zipCode != null ) return false;
        } else if ( !zipCode.equals( other.zipCode ) ) return false;
        return true;
    }
    
}
