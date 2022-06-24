package org.model;

public class Person {
    private final String name;
    private final int myAge;

    public Person(String name, int age) {
        this.name = name;
        this.myAge = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return myAge;
    }
}