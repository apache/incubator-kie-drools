/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person {

    private String name;
    private int age;

    private Person parent;
    private Address address;
    private List<Address> addresses = new ArrayList<>();
    private Gender gender;

    public Person parentPublic;
    public String nicknamePublic;

    private Map<String, String> items = new HashMap<>();

    private BigDecimal salary;

    private Integer ageAsInteger;

    public Person(String name) {
        this(name, null);
    }

    public Person(String name, Person parent) {
        this(name, parent, Gender.NOT_AVAILABLE);
    }

    public Person(String name, Person parent, Gender gender) {
        this.name = name;
        this.parent = parent;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getParent() {
        return parent;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }


    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary( BigDecimal salary ) {
        this.salary = salary;
    }

    public Integer getAgeAsInteger() {
        return ageAsInteger;
    }

    public void setAgeAsInteger( Integer ageAsInteger ) {
        this.ageAsInteger = ageAsInteger;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public boolean isEven(int value) {
        return true;
    }
}
