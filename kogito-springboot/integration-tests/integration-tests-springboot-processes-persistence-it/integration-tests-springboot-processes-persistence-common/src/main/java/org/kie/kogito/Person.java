/*
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
package org.kie.kogito;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Person implements Serializable {

    private String id;
    private String name;
    private int age;
    private boolean adult;
    private Person parent;
    private Instant created;
    private ZonedDateTime updated;
    private BigDecimal score;
    private Person[] relatives;
    private Status status;

    private transient String ignoreMe;

    private static String staticallyIgnoreMe;

    private List<Address> addresses;

    public Person() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.ACTIVE;
    }

    public Person(String name, int age, BigDecimal score, Instant created, ZonedDateTime updated) {
        this();
        this.name = name;
        this.age = age;
        this.score = score;
        this.created = created;
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getIgnoreMe() {
        return ignoreMe;
    }

    public void setIgnoreMe(String ignoreMe) {
        this.ignoreMe = ignoreMe;
    }

    public static String getStaticallyIgnoreMe() {
        return staticallyIgnoreMe;
    }

    public static void setStaticallyIgnoreMe(String staticallyIgnoreMe) {
        Person.staticallyIgnoreMe = staticallyIgnoreMe;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getParent() {
        return parent;
    }

    public void setParent(Person parent) {
        this.parent = parent;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Person[] getRelatives() {
        return relatives;
    }

    public void setRelatives(Person[] relatives) {
        this.relatives = relatives;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", adult=" + adult +
                ", parent=" + parent +
                ", created=" + created +
                ", updated=" + updated +
                ", score=" + score +
                ", relatives=" + Arrays.toString(relatives) +
                ", status=" + status +
                ", ignoreMe='" + ignoreMe + '\'' +
                ", addresses=" + addresses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
