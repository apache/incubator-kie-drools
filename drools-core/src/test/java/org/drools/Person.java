package org.drools;

import java.util.HashMap;
import java.util.Map;

public class Person {

    private String name;
    private int    age;
    
    private String street;
    private String city;
    private String state;
    private String country;
    
    private Map addresses;

    public Person(final String name,
                  final int age) {
        this.name = name;
        this.age = age;
        this.addresses = new HashMap();
    }

    /**
     * @return the age
     */
    public int getAge() {
        return this.age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Map getAddresses() {
        return addresses;
    }

    public void setAddresses(Map addresses) {
        this.addresses = addresses;
    }

}
