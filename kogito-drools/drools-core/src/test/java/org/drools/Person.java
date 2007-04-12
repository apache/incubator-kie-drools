package org.drools;

public class Person {

    private String name;
    private int    age;

    public Person(final String name,
                  final int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return this.age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

}
