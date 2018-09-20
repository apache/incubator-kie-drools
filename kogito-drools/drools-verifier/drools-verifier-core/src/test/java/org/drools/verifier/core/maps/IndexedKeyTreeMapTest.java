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
 */

package org.drools.verifier.core.maps;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.IndexKey;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.util.HasIndex;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IndexedKeyTreeMapTest {

    private final static KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("name").build();
    private final static KeyDefinition AGE_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("age").updatable().build();

    private IndexedKeyTreeMap<Person> map;
    private Person toni;
    private Person eder;
    private Person michael;

    @Before
    public void setUp() throws Exception {
        map = new IndexedKeyTreeMap<>(NAME_KEY_DEFINITION,
                                      AGE_KEY_DEFINITION);

        toni = new Person("Toni", 20);
        eder = new Person("Eder", 20);
        michael = new Person("Michael", 30);

        put(toni);
        put(eder);
        put(michael);
    }

    private void put(final Person person) {
        map.put(person);
    }

    @Test
    public void testIndexOrder() throws Exception {
        assertEquals(toni, map.get(IndexKey.INDEX_ID).get(new Value(0)).iterator().next());
        assertEquals(eder, map.get(IndexKey.INDEX_ID).get(new Value(1)).iterator().next());
        assertEquals(michael, map.get(IndexKey.INDEX_ID).get(new Value(2)).iterator().next());
    }

    @Test
    public void testAddToMiddle() throws Exception {

        final Person smurf = new Person("Smurf",
                                        55);

        map.put(smurf,
                1);

        assertEquals(4, map.get(IndexKey.INDEX_ID).size());
        assertEquals(toni, map.get(IndexKey.INDEX_ID).get(new Value(0)).iterator().next());
        assertEquals(smurf, map.get(IndexKey.INDEX_ID).get(new Value(1)).iterator().next());
        assertEquals(eder, map.get(IndexKey.INDEX_ID).get(new Value(2)).iterator().next());
        assertEquals(michael, map.get(IndexKey.INDEX_ID).get(new Value(3)).iterator().next());
    }

    @Test
    public void testRemove() throws Exception {

        // Removing one by one to check the index stays on track.

        toni.uuidKey.retract();

        assertEquals(eder, map.get(IndexKey.INDEX_ID).get(new Value(0)).iterator().next());
        assertEquals(michael, map.get(IndexKey.INDEX_ID).get(new Value(1)).iterator().next());

        eder.uuidKey.retract();

        Person next = map.get(IndexKey.INDEX_ID).get(new Value(0)).iterator().next();
        assertEquals(michael, next);
    }

    @Test
    public void testUpdateAge() throws Exception {
        toni.setAge(100);

        assertEquals(100, toni.getAge());

        final Person person = map.get(AGE_KEY_DEFINITION).get(new Value(100)).iterator().next();
        assertEquals(toni, person);
        assertEquals(100, person.getAge());
    }

    class Person
            implements HasIndex,
                       HasKeys {

        private final UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        private UpdatableKey<Person> indexKey;

        final String name;

        private UpdatableKey<Person> ageKey;

        public Person(final String name,
                      final int age) {
            this.name = name;
            ageKey = new UpdatableKey<Person>(AGE_KEY_DEFINITION,
                                              age);
        }

        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    indexKey,
                    new Key(NAME_KEY_DEFINITION,
                            name),
                    ageKey
            };
        }

        @Override
        public int getIndex() {
            return (int) indexKey.getSingleValueComparator();
        }

        @Override
        public void setIndex(final int index) {
            UpdatableKey<Person> oldKey = indexKey;
            final UpdatableKey<Person> newKey = new UpdatableKey<>(IndexKey.INDEX_ID,
                                                                   index);
            indexKey = newKey;

            if (oldKey != null) {
                oldKey.update(newKey,
                              this);
            }
        }

        public int getAge() {
            return (Integer) ageKey.getSingleValueComparator();
        }

        public void setAge(final int age) {

            if (ageKey.getSingleValue().equals(age)) {
                return;
            } else {
                final UpdatableKey<Person> oldKey = ageKey;

                final UpdatableKey<Person> newKey = new UpdatableKey<>(AGE_KEY_DEFINITION,
                                                                       age);
                ageKey = newKey;

                oldKey.update(newKey,
                              this);
            }
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}