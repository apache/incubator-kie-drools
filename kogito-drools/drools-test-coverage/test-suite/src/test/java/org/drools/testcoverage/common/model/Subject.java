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

public class Subject implements Serializable {

    private static final long serialVersionUID = 5115167051047419861L;

    private int id = 0;
    private int age = 0;
    private String name = "";
    private int dummy = 0;
    private String sex = "";
    private int value = 0;

    public Subject() {
    }

    public Subject(final String name) {
        this.name = name;
    }

    public Subject(final int age, final String name, final int dummy, final String sex) {
        this.age = age;
        this.name = name;
        this.dummy = dummy;
        this.sex = sex;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDummy(final int dummy) {
        this.dummy = dummy;
    }

    public int getDummy() {
        return dummy;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
