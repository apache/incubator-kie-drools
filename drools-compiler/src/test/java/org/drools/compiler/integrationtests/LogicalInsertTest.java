/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.integrationtests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Tests insertLogical on elements of a collection. One collection element then is then modified which should retract
 * the logically inserted fact.
 */
public class LogicalInsertTest {

    private KieSession kieSession;

    @Before
    public void init() {
        final KieBase kieBase = new KieHelper().addFromClassPath("/org/drools/compiler/integrationtests/logical-insert-from-collection.drl").build();
        this.kieSession = kieBase.newKieSession();
    }

    @After
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    @Test
    public void testRemoveElement() {
        Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(new Integer(i));
        }

        FactHandle handle = kieSession.insert(collection);
        kieSession.fireAllRules();

        for (int i = 5; i > 1; i--) {

            // before remove 5,4,3,2,1 facts
            assertEquals(i, kieSession.getFactCount());

            collection.remove(collection.iterator().next());
            kieSession.update(handle, collection);
            kieSession.fireAllRules();
            // after removing 4,3,2,1,0 facts
            assertEquals(i - 1, kieSession.getFactCount());
        }
    }

    @Test
    public void testAddElement() {
        Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < 4; i++) {
            collection.add(new Integer(i));
        }

        FactHandle handle = kieSession.insert(collection);
        kieSession.fireAllRules();

        // before adding 5 facts
        assertEquals(5, kieSession.getFactCount());

        collection.add(new Integer(42));
        kieSession.update(handle, collection);
        kieSession.fireAllRules();

        // after adding should be 6 facts
        assertEquals(6, kieSession.getFactCount());
    }

    @Test
    public void testChangeElement() {
        // BZ 1274394
        Collection<Person> collection = new ArrayList<Person>();

        for (int i = 1; i < 5; i++) {
            collection.add(new Person("Person " + i, 10 * i));
        }

        FactHandle handle = kieSession.insert(collection);
        kieSession.fireAllRules();

        // before change - expecting 5 facts
        assertEquals(5, kieSession.getFactCount());

        collection.iterator().next().setAge(80);
        kieSession.update(handle, collection);
        kieSession.fireAllRules();

        // after change - expecting 4 facts
        assertEquals(4, kieSession.getFactCount());

        collection.iterator().next().setAge(30);
        kieSession.update(handle, collection);
        kieSession.fireAllRules();

        assertEquals(5, kieSession.getFactCount());
    }

    public static class Person {

        private int id = 0;
        private String name = "";
        private int age;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("%s[id='%s', name='%s']", getClass().getName(), id, name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            //result = prime * result + age;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Person other = (Person) obj;
            if (id != other.id) {
                return false;
            }
            // uncomment to make the test pass
            //if (age != other.age) {
            //    return false;
            //}
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

}
