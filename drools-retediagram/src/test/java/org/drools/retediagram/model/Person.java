package org.drools.retediagram.model;

public class Person {
    private String name;
    private long age;
    private Cheese favouriteCheese;

    public Person(String name, Cheese favouriteCheese) {
        this.name = name;
        this.favouriteCheese = favouriteCheese;
    }

    public Person(String name, long age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public Cheese getFavouriteCheese() {
        return favouriteCheese;
    }
    
    public long getAge() {
        return age;
    }
}
