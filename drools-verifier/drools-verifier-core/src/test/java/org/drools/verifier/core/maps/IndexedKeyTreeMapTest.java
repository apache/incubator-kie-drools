/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexedKeyTreeMapTest {

    private final static KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("name").build();
    private final static KeyDefinition AGE_KEY_DEFINITION = KeyDefinition.newKeyDefinition().withId("age").updatable().build();

    private IndexedKeyTreeMap<Person> map;
    private Person toni;
    private Person eder;
    private Person michael;

    @BeforeEach
    public void setUp() throws Exception {
        map = new IndexedKeyTreeMap<>(NAME_KEY_DEFINITION, AGE_KEY_DEFINITION);

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
    void testIndexOrder() throws Exception {
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(0))).containsExactly(toni);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(1))).containsExactly(eder);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(2))).containsExactly(michael);
    }

    @Test
    void testAddToMiddle() throws Exception {

        final Person smurf = new Person("Smurf", 55);
        map.put(smurf, 1);

        assertThat(map.get(IndexKey.INDEX_ID).size()).isEqualTo(4);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(0))).containsExactly(toni);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(1))).containsExactly(smurf);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(2))).containsExactly(eder);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(3))).containsExactly(michael);
    }

    @Test
    void testRemove() throws Exception {

        // Removing one by one to check the index stays on track.

        toni.uuidKey.retract();

        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(0))).containsExactly(eder);
        assertThat(map.get(IndexKey.INDEX_ID).get(new Value(1))).containsExactly(michael);

        eder.uuidKey.retract();

        Person next = map.get(IndexKey.INDEX_ID).get(new Value(0)).iterator().next();
        assertThat(next).isEqualTo(michael);
    }

    @Test
    void testUpdateAge() throws Exception {
        toni.setAge(100);

        assertThat(toni.getAge()).isEqualTo(100);

        final Person person = map.get(AGE_KEY_DEFINITION).get(new Value(100)).iterator().next();
        assertThat(person).isEqualTo(toni);
        assertThat(person.getAge()).isEqualTo(100);
    }

    class Person implements HasIndex, HasKeys {

        private final UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        private UpdatableKey<Person> indexKey;

        final String name;

        private UpdatableKey<Person> ageKey;

        public Person(final String name, final int age) {
            this.name = name;
            ageKey = new UpdatableKey<Person>(AGE_KEY_DEFINITION, age);
        }

        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    indexKey,
                    new Key(NAME_KEY_DEFINITION, name),
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
            final UpdatableKey<Person> newKey = new UpdatableKey<>(IndexKey.INDEX_ID, index);
            indexKey = newKey;

            if (oldKey != null) {
                oldKey.update(newKey, this);
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

                final UpdatableKey<Person> newKey = new UpdatableKey<>(AGE_KEY_DEFINITION, age);
                ageKey = newKey;

                oldKey.update(newKey, this);
            }
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}