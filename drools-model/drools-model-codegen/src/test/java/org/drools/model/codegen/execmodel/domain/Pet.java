package org.drools.model.codegen.execmodel.domain;

public class Pet {

    public enum PetType {
        dog, cat, parrot
    }

    private PetType type;
    private int age;
    private Person owner;

    public Pet(PetType type) {
        this.type = type;
        age = 0;
    }

    public Pet(PetType type, int age) {
        super();
        this.type = type;
        this.age = age;
    }

    public PetType getType() {
        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}