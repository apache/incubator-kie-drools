package org.drools.model.codegen.execmodel.domain;

import java.util.HashMap;
import java.util.Map;

public class PetPerson extends Person {

    Map<String, Pet> pets;

    public PetPerson() {
        super();
        pets = new HashMap<String, Pet>();
    }

    public PetPerson(String name) {
        super(name);
        pets = new HashMap<String, Pet>();
    }

    public Map<String, Pet> getPets() {
        return pets;
    }

    public void setPets(Map<String, Pet> pets) {
        this.pets = pets;
    }

    public void addPet(String name, Pet p) {
        pets.put(name, p);
    }

    public void removePet(String name) {
        pets.remove(name);
    }

    public void clearPets() {
        pets.clear();
    }

    public Pet getPet(String name) {
        return pets.get(name);
    }
}
