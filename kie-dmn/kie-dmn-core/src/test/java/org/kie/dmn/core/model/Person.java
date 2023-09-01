package org.kie.dmn.core.model;

import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.FEELType;

@FEELType
public class Person {
    private String firstName;
    private String lastName;
    private int age;
    
    public Person(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(String firstName, String lastName, int age) {
        this(firstName, lastName);
        this.setAge(age);
    }

    @FEELProperty("first name")
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    @FEELProperty("last name")
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;   
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [firstName=").append(firstName).append(", lastName=").append(lastName).append("]");
        return builder.toString();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
}
