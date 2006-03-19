package org.drools;

public class Person {
    private final String name;
    private final String likes;
    private final int age;
    
    public Person( String name, String likes ) {
        this( name, likes, 0 );
    }
    
    public Person( String name, String likes, int age ) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public String getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }
        
    public int getAge() {
        return this.age;
    }
}
