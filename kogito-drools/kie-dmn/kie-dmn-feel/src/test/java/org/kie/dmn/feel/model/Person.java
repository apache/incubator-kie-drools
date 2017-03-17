/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.model;

import org.kie.dmn.feel.lang.FEELProperty;

public class Person {
    private String firstName;
    private String lastName;
    private Address homeAddress;
    private int age;
    
    public Person(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Person(String firstName, String lastName, Address homeAddress) {
        this(firstName, lastName);
        this.homeAddress = homeAddress;
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
    
    @FEELProperty("home address")
    public Address getHomeAddress() {
        return homeAddress;
    }
    
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
    
    /**
     * Alias for {@link #getHomeAddress()}.
     */
    public Address getAddress() {
        return homeAddress;
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
