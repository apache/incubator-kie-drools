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

import java.util.List;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UpdatableKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KeyTreeMapMultiValueKeyTest {

    private final KeyDefinition NAME = KeyDefinition.newKeyDefinition()
            .withId("name")
            .build();
    private final KeyDefinition AREA_CODE = KeyDefinition.newKeyDefinition()
            .withId("areaCode")
            .build();
    private KeyTreeMap<Country> map;
    private Country norway;
    private Country finland;
    private Country sweden;

    private AnalyzerConfiguration configuration;

    @BeforeEach
    public void setUp() throws Exception {

        configuration = new AnalyzerConfigurationMock();

        map = new KeyTreeMap<>(NAME, AREA_CODE);

        finland = new Country("Finland", 48100);
        sweden = new Country("Sweden", 12345, 51000);
        norway = new Country("Norway", 00000, 51000);

        map.put(finland);
        map.put(sweden);
        map.put(norway);
    }

    @Test
    void testFindByUUID() throws Exception {
    	assertThat(map.get(UUIDKey.UNIQUE_UUID).keySet()).containsExactlyInAnyOrder(
                finland.uuidKey.getSingleValue(),
                sweden.uuidKey.getSingleValue(),
                norway.uuidKey.getSingleValue());
    }

    @Test
    void testFindByAreaCodeKey() throws Exception {
        assertThat(map.get(AREA_CODE).keySet()).extracting(x -> x.getComparable()).containsExactlyInAnyOrder(48100, 12345, 51000, 00000);
    }

    @Test
    void testFindByAreaCode() throws Exception {
        final MultiMap<Value, Country, List<Country>> areaCode = map.get(AREA_CODE);
        
        assertThat(areaCode.get(new Value(48100))).hasSize(1).contains(finland);
        assertThat(areaCode.get(new Value(12345))).hasSize(1).contains(sweden);
        assertThat(areaCode.get(new Value(51000))).hasSize(2).contains(sweden, norway);
    }

    class Country implements HasKeys {

        final String name;
        private final UUIDKey uuidKey = configuration.getUUID(this);
        private UpdatableKey areaCode;

        public Country(final String name,
                       final Integer... areaCodes) {
            this.name = name;
            this.areaCode = new UpdatableKey(AREA_CODE, new Values(areaCodes));
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(NAME,
                            name),
                    areaCode
            };
        }

        public void setAge(final Integer... areaCodes) {
            final UpdatableKey oldKey = areaCode;

            final UpdatableKey<Country> newKey = new UpdatableKey(AREA_CODE,
                                                                  new Values(areaCodes));
            areaCode = newKey;

            oldKey.update(newKey, this);
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}