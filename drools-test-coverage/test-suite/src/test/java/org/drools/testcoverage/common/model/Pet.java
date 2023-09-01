package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Pet implements Serializable {

    private static final long serialVersionUID = 366857408049359963L;

    public enum PetType {
        DOG, CAT, PARROT
    }

    private String name;
    private PetType type;
    private int age;
    private Person owner;

    public Pet(final String name) {
        this.name = name;
    }

    public Pet(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public Pet(final PetType type) {
        this.type = type;
        age = 0;
    }

    public Pet(final PetType type, final int age) {
        super();
        this.type = type;
        this.age = age;
    }

    public PetType getType() {
        return type;
    }

    public void setType(final PetType type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(final Person owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
