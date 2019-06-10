package org.drools;

public class Person {

    private String name;
    private int age;

    private Person parent;
    private Address address;

    public Person parentPublic;
    public String nicknamePublic;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, Person parent) {
        this.name = name;
        this.parent = parent;
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
}
