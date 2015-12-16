/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveList;

import java.util.List;

public class Child extends Person {

    private String mother;

    private final List<Toy> toys = new ReactiveList<Toy>();

    public Child(String name, int age) {
        super(name, age);
    }

    public List<Toy> getToys() {
        return toys;
    }

    public void addToy(Toy toy) {
        toys.add(toy);
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }
}
