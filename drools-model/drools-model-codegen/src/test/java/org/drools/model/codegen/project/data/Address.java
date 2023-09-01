package org.drools.model.codegen.project.data;

public class Address {

    private String street;
    private String city;
    private String zipCode;
    private String country;

    public Address() {

    }

    public Address(String city) {
        this(null, city, null, null);
    }

    public Address(String street, String city, String zipCode, String country) {
        super();
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address [street=" + street + ", city=" + city + ", zipCode=" + zipCode + ", country=" + country + "]";
    }
}
