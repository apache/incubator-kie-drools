/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Person implements Serializable {

    private static final long serialVersionUID = -5411807328989112195L;

    private int id = 0;
    private String name = "";
    private int age;
    private String likes;
    private Address address;

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

    public Person(final String name, final String likes, final int age) {
        this.name = name;
        this.likes = likes;
        this.age = age;
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

    public void setLikes(final String likes) {
        this.likes = likes;
    }

    public String getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return String.format("%s[id='%s', name='%s']", getClass().getName(), id, name);
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
    public boolean equals(Object obj) {
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
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
