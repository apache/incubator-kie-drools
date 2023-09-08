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

import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.KeyTreeMap;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectWithNegativeExactMatcherWhenTheValueIsNotInTheMapTest {

    private Select<Item> select;

    private MultiMap<Value, Item, List<Item>> makeMap() {
        final MultiMap<Value, Item, List<Item>> map = MultiMapFactory.make(false);

        map.put(new Value(0), new Item(0));
        map.put(new Value(56), new Item(56));
        map.put(new Value(100), new Item(100));
        map.put(new Value(1200), new Item(1200));
        return map;
    }

    private void fill(final KeyTreeMap<Item> itemKeyTreeMap,
                      final Item item) {
        itemKeyTreeMap.put(item);
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.select = new Select<>(makeMap(),
                                   new ExactMatcher(null,
                                                    "cost",
                                                    true));
    }

    @Test
    void testAll() throws Exception {
        final Collection<Item> all = select.all();

        assertThat(all).hasSize(4);
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first().cost).isEqualTo(0);
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last().cost).isEqualTo(1200);
    }

    private class Item implements HasKeys {

        private int cost;
        private UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        public Item(final int cost) {
            this.cost = cost;
        }

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(KeyDefinition.newKeyDefinition().withId("cost").build(),
                            cost)
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}