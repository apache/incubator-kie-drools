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
package org.acme.travels;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class Traveller implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String nationality;
    private Address address;
    private Date testDate;
    private Instant testInstant;
    private Integer testInteger;
    private Long testLong;
    private Float testFloat;
    private Double testDouble;

    private boolean processed;

    public Traveller() {
    }

    public Traveller(String firstName, String lastName, String email, String nationality) {
        this(firstName, lastName, email, nationality, null);
    }

    public Traveller(String firstName, String lastName, String email, String nationality, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.nationality = nationality;
        this.address = address;
    }

    public Traveller(String firstName, String lastName, String email, String nationality, Address address,
            Date testDate, Instant testInstant, boolean processed, Integer testInteger, Long testLong, Float testFloat,
            Double testDouble) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.nationality = nationality;
        this.address = address;
        this.testDate = testDate;
        this.testInstant = testInstant;
        this.processed = processed;
        this.testInteger = testInteger;
        this.testLong = testLong;
        this.testDouble = testDouble;
        this.testFloat = testFloat;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public Instant getTestInstant() {
        return testInstant;
    }

    public void setTestInstant(Instant testInstant) {
        this.testInstant = testInstant;
    }

    public Integer getTestInteger() {
        return testInteger;
    }

    public void setTestInteger(Integer testInteger) {
        this.testInteger = testInteger;
    }

    public Long getTestLong() {
        return testLong;
    }

    public void setTestLong(Long testLong) {
        this.testLong = testLong;
    }

    public Float getTestFloat() {
        return testFloat;
    }

    public void setTestFloat(Float testFloat) {
        this.testFloat = testFloat;
    }

    public Double getTestDouble() {
        return testDouble;
    }

    public void setTestDouble(Double testDouble) {
        this.testDouble = testDouble;
    }

    @Override
    public String toString() {
        return "Traveller [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", nationality=" +
                nationality + ", address=" + address + ", processed=" + processed + " testDate=" + testDate +
                " testInstant=" + testInstant + " testInteger=" + testInteger + " testLong=" + testLong +
                " testFloat=" + testFloat + " testDouble=" + testDouble + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
        result = prime * result + (processed ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Traveller other = (Traveller) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (nationality == null) {
            if (other.nationality != null)
                return false;
        } else if (!nationality.equals(other.nationality))
            return false;
        if (processed != other.processed)
            return false;
        return true;
    }

}
