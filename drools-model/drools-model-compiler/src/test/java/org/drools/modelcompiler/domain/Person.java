package org.drools.modelcompiler.domain;

import org.drools.core.phreak.AbstractReactiveObject;
import org.kie.api.definition.type.Position;

public class Person extends AbstractReactiveObject {

    @Position(0)
    private String name;

    @Position(1)
    private int age;

    private Address address;
    private int id = 0;
    private String likes;

    public Person() { }

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, Address address) {
        this.name = name;
        this.age = age;
        this.address = address;
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
        notifyModification();
    }

    public Address getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes( String likes ) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;
        return age == person.age && name.equals(person.name);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }
}
