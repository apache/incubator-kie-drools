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

package org.drools.retediagram.model;

public class Person {
    private String name;
    private long age;
    private Cheese favouriteCheese;

    public Person(String name, Cheese favouriteCheese) {
        this.name = name;
        this.favouriteCheese = favouriteCheese;
    }

    public Person(String name, long age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() {
        return name;
    }
    
    public Cheese getFavouriteCheese() {
        return favouriteCheese;
    }
    
    public long getAge() {
        return age;
    }
}
