/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.data;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Address {

    private static final String ADDRESS_SEPARATOR = ", ";

    private transient final Map<Integer, Entry<Supplier<String>, Consumer<String>>> ADDRESS = Map.of(
            0, new SimpleImmutableEntry<Supplier<String>, Consumer<String>>(this::getStreet, this::setStreet),
            1, new SimpleImmutableEntry<Supplier<String>, Consumer<String>>(this::getCity, this::setCity),
            2, new SimpleImmutableEntry<Supplier<String>, Consumer<String>>(this::getZipCode, this::setZipCode),
            3, new SimpleImmutableEntry<Supplier<String>, Consumer<String>>(this::getCountry, this::setCountry));

    private String street;
    private String city;
    private String zipCode;
    private String country;

    public Address() {

    }

    public Address(String city) {
        this(null, city, null, null);
    }

    public Address(String street, String city, String zipCode, String country) {
        super();
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
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

    @Override
    public String toString() {
        return "Address [street=" + street + ", city=" + city + ", zipCode=" + zipCode + ", country=" + country + "]";
    }

    public String getAddress() {
        return IntStream.range(0, ADDRESS.size()).mapToObj(i -> ADDRESS.get(i).getKey().get()).collect(Collectors.joining(ADDRESS_SEPARATOR));
    }

    public void setAddress(String address) {
        String[] addressSections = address.split(ADDRESS_SEPARATOR);
        IntStream.range(0, addressSections.length).forEach(i -> ADDRESS.get(i).getValue().accept(addressSections[i]));
    }

    public static Address of(String address) {
        Address a = new Address();
        a.setAddress(address);
        return a;
    }
}
