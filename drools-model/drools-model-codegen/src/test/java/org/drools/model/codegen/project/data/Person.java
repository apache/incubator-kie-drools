package org.drools.model.codegen.project.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {

    private String name;
    private int age;
    private boolean adult;

    private transient String ignoreMe;

    private static String staticallyIgnoreMe;

    private transient List<Address> addresses = new ArrayList<>();

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getIgnoreMe() {
        return ignoreMe;
    }

    public void setIgnoreMe(String ignoreMe) {
        this.ignoreMe = ignoreMe;
    }

    public static String getStaticallyIgnoreMe() {
        return staticallyIgnoreMe;
    }

    public static void setStaticallyIgnoreMe(String staticallyIgnoreMe) {
        Person.staticallyIgnoreMe = staticallyIgnoreMe;
    }

    public void addAddress(final Address address) {
        addresses.add(address);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", adult=" + adult +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return age == person.age &&
                adult == person.adult &&
                Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, adult);
    }
}
