package org.drools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Person {

    private String name;
    private int age;

    private Person parent;
    private Address address;
    private Gender gender;

    public Person parentPublic;
    public String nicknamePublic;

    private Map<String, String> items = new HashMap<>();

    private BigDecimal salary;

    private Integer ageAsInteger;

    public Person(String name) {
        this(name, null);
    }

    public Person(String name, Person parent) {
        this(name, parent, Gender.NOT_AVAILABLE);
    }

    public Person(String name, Person parent, Gender gender) {
        this.name = name;
        this.parent = parent;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getParent() {
        return parent;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }


    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary( BigDecimal salary ) {
        this.salary = salary;
    }

    public Integer getAgeAsInteger() {
        return ageAsInteger;
    }

    public void setAgeAsInteger( Integer ageAsInteger ) {
        this.ageAsInteger = ageAsInteger;
    }
}
