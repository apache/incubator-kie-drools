/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.compiler.oopath.model;


import org.drools.core.phreak.AbstractReactiveObject;

import java.util.Arrays;

public class Company extends AbstractReactiveObject {

    private String name;

    private Employee[] employees;

    public Company(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        notifyModification();
    }

    public Employee[] getEmployees() {
        return  employees;
    }

    public void setEmployees(final Employee[] employees) {
        this.employees = employees;
        notifyModification();
    }

    @Override
    public String toString() {
        return ("Company: " + name);
    }
}
