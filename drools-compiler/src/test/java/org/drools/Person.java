package org.drools;

public class Person {
    private final String name;
    private final String likes;
    
    public Person( String name, String likes ) {
        this.name = name;
        this.likes = likes;
    }

    public String getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }
        
}
