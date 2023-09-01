/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.testcoverage.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person implements Serializable {

    private static final long serialVersionUID = -5411807328989112195L;

    private int id = 0;
    private String name = "";
    private int age;
    private String likes;
    private Address address;
    private List<Address> addresses = new ArrayList<>();
    private Pet pet;
    private boolean alive;
    private boolean happy;
    private Cheese cheese;
    private String hair;
    private BigDecimal salary;
    private Map<String, Comparable> map = new HashMap<>();

    private Object object;

    public Person() {
    }

    public Person(final String name) {
        super();
        this.name = name;
    }

    public Person(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public Person(final int id, final String name, final int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Person(final String name, final String likes, final int age) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public Person(final String name, final String likes) {
        this.name = name;
        this.likes = likes;
    }

    public Person(final String name, final int age, final BigDecimal salary) {
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    public Person(final String name, final int age, final BigDecimal salary, String likes) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public int getDoubleAge() {
        return this.age * 2;
    }

    public void setLikes(final String likes) {
        this.likes = likes;
    }

    public String getLikes() {
        return likes;
    }

    public void addAddress(final Address address) {
        addresses.add(address);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(final Pet pet) {
        this.pet = pet;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(final boolean happy) {
        this.happy = happy;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    public void setCheese(final Cheese cheese) {
        this.cheese = cheese;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(final String hair) {
        this.hair = hair;
    }

    @Override
    public String toString() {
        return String.format("%s[id='%s', name='%s']", getClass().getName(), id, name);
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Map<String, Comparable> getMap() {
        return map;
    }

    public void setMap(Map<String, Comparable> map) {
        this.map = map;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }
}
