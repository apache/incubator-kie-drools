package org.drools.testcoverage.common.model;

public class LongAddress extends Address {

    private String country;

    public LongAddress(final String country) {
        this.country = country;
    }

    public LongAddress(final String street,
                       final String suburb,
                       final String zipCode,
                       final String country) {
        super(street, suburb, zipCode);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }
}
