package net.tarilabs.model;


public class Person {
    private final String name;
    private final Cheese favouriteCheese;
    public Person(String name, Cheese favouriteCheese) {
        super();
        this.name = name;
        this.favouriteCheese = favouriteCheese;
    }
    
    public String getName() {
        return name;
    }
    
    public Cheese getFavouriteCheese() {
        return favouriteCheese;
    }
    
}
