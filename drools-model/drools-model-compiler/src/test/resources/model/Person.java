package org.model;

import org.openjdk.jmh.util.Optional;

public class Person {
    private final String name;
    private final int myAge;
    private final Optional<Address> address;

    public Person(String name, int age) {
        this.name = name;
        this.myAge = age;
        this.address = Optional.none();
    }

    public Person(String name, int myAge, Address address) {
        this.name = name;
        this.myAge = myAge;
        this.address = Optional.of(address);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return myAge;
    }

    public Optional<Address> getAddress() {
        return address;
    }
}