/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.process.instance.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanMapTest {

    public class Person {

        private String name;
        private int age;

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

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    Person person;

    @Before
    public void before() {
        person = new Person();
        person.setName("Paul");
        person.setAge(10);
    }

    @Test
    public void get() {
        BeanMap map = new BeanMap(person);
        assertEquals(map.get("name"), "Paul");
    }

    @Test
    public void put() {
        BeanMap map = new BeanMap(person);
        map.put("name", "Ringo");
        assertEquals(person.getName(), "Ringo");
    }


}