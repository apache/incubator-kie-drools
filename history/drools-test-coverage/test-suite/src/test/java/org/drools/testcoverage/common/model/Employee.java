/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

public class Employee extends Person implements Serializable {

    private static final long serialVersionUID = -5411807328989112199L;

    private String position = "";

    public Employee() {
        super();
    }

    public Employee(final String name) {
        super(name);
    }

    public Employee(final String name, final int age) {
        super(name, age);
    }

    public Employee(final String name, final String likes, final int age) {
        super(name, likes, age);
    }

    public Employee(final String name, final String likes, final int age, final String position) {
        super(name, likes, age);
        this.position = position;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(final String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("%s[id='%s', name='%s', position='%s']", getClass().getName(), getId(), getName(), position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Employee employee = (Employee) o;

        return position != null ? position.equals(employee.position) : employee.position == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }
}
