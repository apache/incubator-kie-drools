package org.drools;

public class Person {
    private final String name;
    private final String likes;
    private final int age;
    
    private char sex;
    
    private boolean alive;
    
    private String status;
    
    public Person( String name  ) {
        this( name, "", 0 );
    }
    
    
    public Person( String name, String likes ) {
        this( name, likes, 0 );
    }
    
    public Person( String name, String likes, int age ) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    
    
    
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String toString() {
        return "[Person name='" + this.name + "']";
    }
}
