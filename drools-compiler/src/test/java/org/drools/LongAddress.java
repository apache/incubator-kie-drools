package org.drools;

public class LongAddress extends Address {

    private String country;

    public LongAddress(String country) {
        this.country = country;
    }

    public LongAddress(String street,
                       String suburb,
                       String zipCode,
                       String country) {
        super(street, suburb, zipCode);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}