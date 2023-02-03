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

import java.util.List;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.Util;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyTreeMapTest {

    private final KeyDefinition AGE = KeyDefinition.newKeyDefinition()
            .updatable()
            .withId("age")
            .build();
    private final KeyDefinition NAME = KeyDefinition.newKeyDefinition()
            .withId("name")
            .build();

    private KeyTreeMap<Person> map;
    private Person toni;
    private Person eder;
    private Person michael;

    private AnalyzerConfiguration configuration;

    @BeforeEach
    public void setUp() throws
            Exception {
        configuration = new AnalyzerConfigurationMock();

        map = new KeyTreeMap<>(AGE,
                               NAME);

        toni = new Person("Toni",
                          20);
        eder = new Person("Eder",
                          20);
        michael = new Person("Michael",
                             30);

        put(toni);
        put(eder);
        put(michael);
    }

    @Test
    void testFindByUUID() throws
            Exception {
        Util.assertMapContent(map.get(UUIDKey.UNIQUE_UUID),
                toni.uuidKey,
                eder.uuidKey,
                michael.uuidKey);
    }

    @Test
    void testReAdd() throws
            Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            put(toni);
        });
    }

    @Test
    void testFindByName() throws
            Exception {
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition()
                        .withId("name")
                        .build()),
                "Toni",
                "Eder",
                "Michael");
    }

    @Test
    void testFindByAge() throws
            Exception {
        final MultiMap<Value, Person, List<Person>> age = map.get(KeyDefinition.newKeyDefinition()
                .withId("age")
                .build());

        Util.assertMapContent(age,
                20,
                20,
                30);
        assertThat(age.get(new Value(20))
                .contains(toni)).isTrue();
        assertThat(age.get(new Value(20))
                .contains(eder)).isTrue();
    }

    @Test
    void testUpdateAge() throws
            Exception {
        final MultiMapChangeHandler changeHandler = mock(MultiMapChangeHandler.class);
        map.get(AGE).addChangeListener(changeHandler);

        toni.setAge(10);

        final MultiMap<Value, Person, List<Person>> age = map.get(AGE);

        assertThat(age.get(new Value(20))
                .contains(toni)).isFalse();
        assertThat(age.get(new Value(10))
                .contains(toni)).isTrue();
    }

    @Test
    void testRetract() throws
            Exception {

        toni.uuidKey.retract();

        Util.assertMapContent(map.get(UUIDKey.UNIQUE_UUID),
                eder.uuidKey,
                michael.uuidKey);
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition()
                        .withId("name")
                        .build()),
                "Eder",
                "Michael");
        Util.assertMapContent(map.get(KeyDefinition.newKeyDefinition()
                        .withId("age")
                        .build()),
                20,
                30);
    }

    @Test
    void testRemoveWhenItemDoesNotExist() throws
            Exception {
        final UUIDKey uuidKey = mock(UUIDKey.class);
        when(uuidKey.getKeyDefinition()).thenReturn(UUIDKey.UNIQUE_UUID);
        when(uuidKey.getSingleValue()).thenReturn(new Value("DoesNotExist"));
        assertThat(map.remove(uuidKey)).isNull();

        assertThat(map.get(UUIDKey.UNIQUE_UUID)
                .size()).isEqualTo(3);
    }

    private void put(final Person person) {
        map.put(person);
    }

    class Person
            implements HasKeys {

        final String name;
        private final UUIDKey uuidKey = configuration.getUUID(this);
        private UpdatableKey ageKey;

        public Person(final String name,
                      final int age) {
            this.name = name;
            this.ageKey = new UpdatableKey(AGE,
                                           age);
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(NAME,
                            name),
                    ageKey
            };
        }

        public void setAge(final int age) {
            final UpdatableKey oldKey = ageKey;

            final UpdatableKey<Person> newKey = new UpdatableKey<>(AGE,
                                                                   age);
            ageKey = newKey;

            oldKey.update(newKey,
                          this);
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}