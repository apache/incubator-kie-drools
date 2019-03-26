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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyTreeMapMergeTest {

    private final static KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("name")
            .build();
    private final static KeyDefinition AGE_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("age")
            .build();

    private KeyTreeMap<Person> treeMap;
    private Person pat;

    private KeyTreeMap<Person> otherKeyTreeMap;
    private Person mat;

    private Matcher nameMatcher = new Matcher(NAME_KEY_DEFINITION);
    private AnalyzerConfigurationMock configuration;

    @Before
    public void setUp() throws
            Exception {

        configuration = new AnalyzerConfigurationMock();

        treeMap = new KeyTreeMap<>(NAME_KEY_DEFINITION,
                                   AGE_KEY_DEFINITION);
        pat = new Person("Pat",
                         10);
        add(treeMap,
            pat);

        otherKeyTreeMap = new KeyTreeMap<>(NAME_KEY_DEFINITION,
                                           AGE_KEY_DEFINITION);
        mat = new Person("mat",
                         15);
        add(otherKeyTreeMap,
            mat);
    }

    private void add(final KeyTreeMap<Person> treeMap,
                     final Person person) {
        treeMap.put(person);
    }

    @Test
    public void testMergeToEmptyMap() throws
            Exception {
        final KeyTreeMap<Person> empty = new KeyTreeMap<>(UUIDKey.UNIQUE_UUID,
                                                          NAME_KEY_DEFINITION,
                                                          AGE_KEY_DEFINITION);
        empty.merge(otherKeyTreeMap);

        assertEquals(1,
                     empty.get(nameMatcher.getKeyDefinition())
                             .allValues()
                             .size());
    }

    @Test
    public void testNames() throws
            Exception {
        treeMap.merge(otherKeyTreeMap);

        final MultiMap<Value, Person, List<Person>> multiMap = treeMap.get(nameMatcher.getKeyDefinition());

        assertEquals(2,
                     multiMap.allValues()
                             .size());
    }

    @Test
    public void testAge() throws
            Exception {
        treeMap.merge(otherKeyTreeMap);

        assertEquals(2,
                     allPersons(treeMap).size());
    }

    @Test
    public void testRetract() throws
            Exception {
        KeyTreeMap<Person> thirdKeyTreeMap = new KeyTreeMap<>(NAME_KEY_DEFINITION,
                                                              AGE_KEY_DEFINITION);
        thirdKeyTreeMap.merge(treeMap);
        thirdKeyTreeMap.merge(otherKeyTreeMap);

        assertEquals(2,
                     allPersons(thirdKeyTreeMap).size());
        assertEquals(1,
                     allPersons(treeMap).size());
        assertEquals(1,
                     allPersons(otherKeyTreeMap).size());

        pat.uuidKey.retract();

        assertEquals(1,
                     allPersons(thirdKeyTreeMap).size());
        assertEquals(0,
                     allPersons(treeMap).size());
        assertEquals(1,
                     allPersons(otherKeyTreeMap).size());
    }

    private Collection<Person> allPersons(final KeyTreeMap<Person> personKeyTreeMap) {
        final Matcher age = new Matcher(KeyDefinition.newKeyDefinition()
                                                .withId("age")
                                                .build());
        final MultiMap<Value, Person, List<Person>> personChangeHandledMultiMap = personKeyTreeMap.get(age.getKeyDefinition());
        if (personChangeHandledMultiMap != null) {
            return personChangeHandledMultiMap.allValues();
        } else {
            return Collections.emptyList();
        }
    }

    private class Person
            implements HasKeys {

        private String name;
        private Integer age;
        private UUIDKey uuidKey = configuration.getUUID(this);

        public Person(final String name,
                      final Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(NAME_KEY_DEFINITION,
                            name),
                    new Key(AGE_KEY_DEFINITION,
                            age)};
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}