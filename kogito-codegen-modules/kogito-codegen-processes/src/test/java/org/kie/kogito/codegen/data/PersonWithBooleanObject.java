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
package org.kie.kogito.codegen.data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.util.Arrays.asList;

public class PersonWithBooleanObject {

    private static final String ADDRESS_SEPARATOR = "; ";

    private String id;
    private String name;
    private int age;
    private byte[] bytes;
    private boolean adult;
    private Boolean married;
    private PersonWithBooleanObject parent;
    private PersonWithBooleanObject[] relatives;
    private Instant instant;
    private LocalDateTime localDateTime;
    private LocalDate localDate;
    private Duration duration;
    private ZonedDateTime zonedDateTime;
    private OffsetDateTime offsetDateTime;
    private Date date;
    private BigDecimal bigDecimal;
    @JsonIgnore
    private Money salary;
    @JsonIgnore
    private Money[] earnings;
    @JsonIgnore
    private List<Money> expenses;

    private transient String ignoreMe;

    private static String staticallyIgnoreMe;

    private transient List<Address> addresses = new ArrayList<>();

    public PersonWithBooleanObject() {
    }

    public PersonWithBooleanObject(String name, int age) {
        this.name = name;
        this.age = age;
        salary = Money.of(BigDecimal.valueOf(100));
        earnings = new Money[] { Money.of(BigDecimal.valueOf(200)) };
        expenses = asList(Money.of(BigDecimal.valueOf(50)));
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
        PersonWithBooleanObject.staticallyIgnoreMe = staticallyIgnoreMe;
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

    public PersonWithBooleanObject getParent() {
        return parent;
    }

    public void setParent(PersonWithBooleanObject parent) {
        this.parent = parent;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
    }

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public Money getSalary() {
        return salary;
    }

    public void setSalary(Money salary) {
        this.salary = salary;
    }

    public PersonWithBooleanObject[] getRelatives() {
        return relatives;
    }

    public void setRelatives(PersonWithBooleanObject[] relatives) {
        this.relatives = relatives;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @JsonIgnore
    public String getAddressesStr() {
        return getAddresses() != null ? getAddresses().stream().map(
                a -> a.getAddress()).collect(Collectors.joining(ADDRESS_SEPARATOR)) : null;
    }

    public void setAddressesStr(String addresses) {
        setAddresses(
                Arrays.stream(addresses.split(ADDRESS_SEPARATOR)).map(a -> Address.of(a)).collect(Collectors.toList()));
    }

    public Money[] getEarnings() {
        return earnings;
    }

    public void setEarnings(Money[] earnings) {
        this.earnings = earnings;
    }

    public List<Money> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Money> expenses) {
        this.expenses = expenses;
    }

    public Boolean isMarried() {
        return married;
    }

    public void setMarried(Boolean married) {
        this.married = married;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", adult=" + adult +
                ", married=" + married +
                ", parent=" + parent +
                ", relatives=" + Arrays.toString(relatives) +
                ", instant=" + instant +
                ", localDateTime=" + localDateTime +
                ", localDate=" + localDate +
                ", duration=" + duration +
                ", zonedDateTime=" + zonedDateTime +
                ", offsetDateTime=" + offsetDateTime +
                ", date=" + date +
                ", bigDecimal=" + bigDecimal +
                ", salary=" + salary +
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
        PersonWithBooleanObject person = (PersonWithBooleanObject) o;
        return age == person.age && adult == person.adult && married == person.married && Objects.equals(name, person.name) && Objects.equals(parent, person.parent)
                && Objects.equals(ignoreMe, person.ignoreMe)
                && Objects.equals(addresses, person.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, adult, married, parent, ignoreMe, addresses);
    }
}
