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
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KnownKeysKeyTreeMapTest {

    private KeyTreeMap<Person> map;

    @BeforeEach
    public void setUp() throws Exception {
        map = new KeyTreeMap<>(KeyDefinition.newKeyDefinition().withId("age").build());
    }

    @Test
    void testExisting() throws Exception {
        assertThat(map.get(KeyDefinition.newKeyDefinition().withId("age").build())).isNotNull();
    }

    @Test
    void testUnknown() throws Exception {
        assertThat(map.get(KeyDefinition.newKeyDefinition().withId("unknown").build())).isNull();
    }

    class Person implements HasKeys {

        private UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(KeyDefinition.newKeyDefinition().withId("name").build(),
                            "hello")
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}