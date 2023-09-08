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
package org.drools.verifier.core.index.select;


import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.KeyTreeMap;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectNoMatchesTest {

    private Select<Address> select;

    @BeforeEach
    public void setUp() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition().withId("name").build();
        final KeyTreeMap<Address> map = new KeyTreeMap<>(keyDefinition);
        final Address object = new Address();
        map.put(object);

        select = new Select<>(map.get(UUIDKey.UNIQUE_UUID),
                              new ExactMatcher(keyDefinition,
                                               "Toni"));
    }

    @Test
    void testAll() throws Exception {
        assertThat(select.all()).isEmpty();
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isNull();
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isNull();
    }

    private class Address implements HasKeys {

        private UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}