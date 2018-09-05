/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.domain;

import org.drools.core.phreak.ReactiveList;

import java.util.List;

public class Adult extends Person {

    private final List<Child> children = new ReactiveList<Child>();
    private Person[] childrenA = new Person[0];

    public Adult(String name, int age) {
        super(name, age);
    }

    public List<Child> getChildren() {
        return children;
    }

    public void addChild(Child child) {
        children.add(child);
    }


    public Person[] getChildrenA() {
        return childrenA;
    }

    public void setChildrenA(Person[] children) {
        this.childrenA = children;
    }
}

