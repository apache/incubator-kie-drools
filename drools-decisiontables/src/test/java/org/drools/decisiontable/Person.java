package org.drools.decisiontable;

public class Person {
    private String name;
    private String likes;
    private int    age;

    private char         sex;

    private boolean      alive;

    private String       status;

    private Boolean canBuyAlcohol;

    public Person() {

    }
    public Person(final String name) {
        this( name,
              "",
              0 );
    }

    public Person(final String name,
                  final String likes) {
        this( name,
              likes,
              0 );
    }

    public Person(final String name,
                  final String likes,
                  final int age) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getLikes() {
        return this.likes;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public char getSex() {
        return this.sex;
    }

    public void setSex(final char sex) {
        this.sex = sex;
    }

    public Boolean getCanBuyAlcohol() {
        return canBuyAlcohol;
    }

    public void setCanBuyAlcohol(Boolean canBuyAlcohol) {
        this.canBuyAlcohol = canBuyAlcohol;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return "[Person name='" + this.name + "']";
    }
}
