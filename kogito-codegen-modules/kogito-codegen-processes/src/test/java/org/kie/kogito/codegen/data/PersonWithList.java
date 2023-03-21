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

import java.util.List;
import java.util.Objects;

public class PersonWithList {

    private String name;
    private int age;
    private boolean adult;
    private List<String> stringList;
    private List<Integer> integerList;
    private List<Boolean> booleanList;
    private List<Long> longList;

    public PersonWithList() {
    }

    public PersonWithList(String name, int age, boolean adult, List<String> stringList, List<Integer> integerList, List<Boolean> booleanList, List<Long> longList) {
        this.name = name;
        this.age = age;
        this.adult = adult;
        this.stringList = stringList;
        this.integerList = integerList;
        this.booleanList = booleanList;
        this.longList = longList;
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

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }

    public List<Boolean> getBooleanList() {
        return booleanList;
    }

    public void setBooleanList(List<Boolean> booleanList) {
        this.booleanList = booleanList;
    }

    public List<Long> getLongList() {
        return longList;
    }

    public void setLongList(List<Long> longList) {
        this.longList = longList;
    }

    @Override
    public String toString() {
        return "PersonWithList{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", adult=" + adult +
                ", stringList=" + stringList +
                ", integerList=" + integerList +
                ", booleanList=" + booleanList +
                ", longList=" + longList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersonWithList that = (PersonWithList) o;
        return age == that.age &&
                adult == that.adult &&
                Objects.equals(name, that.name) &&
                Objects.equals(stringList, that.stringList) &&
                Objects.equals(integerList, that.integerList) &&
                Objects.equals(booleanList, that.booleanList) &&
                Objects.equals(longList, that.longList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, adult, stringList, integerList, booleanList, longList);
    }
}
