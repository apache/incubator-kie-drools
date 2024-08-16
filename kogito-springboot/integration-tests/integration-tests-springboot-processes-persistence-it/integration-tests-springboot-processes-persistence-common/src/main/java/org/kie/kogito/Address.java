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
import java.util.Objects;

public class Address implements Serializable {

    private String street;
    private String city;
    private String zipCode;
    private String country;
    private AddressType type;
    private Status status;

    public Address() {

    }

    public Address(String city) {
        this(null, city, null, null, AddressType.HOME, Status.ACTIVE);
    }

    public Address(String street, String city, String zipCode, String country, AddressType type, Status status) {
        super();
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.type = type;
        this.status = status;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) && Objects.equals(city, address.city) && Objects.equals(zipCode, address.zipCode) && Objects.equals(country,
                address.country)
                && Objects.equals(type,
                        address.type)
                && Objects.equals(status,
                        address.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country, type, status);
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
