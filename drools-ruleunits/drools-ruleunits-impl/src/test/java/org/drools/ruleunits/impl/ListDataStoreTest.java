/*
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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class ListDataStoreTest {

    @Test
    public void subscribeReplaysInInsertionOrder() {
        DataStore<String> store = DataSource.createStore();
        store.add("A");
        store.add("B");
        store.add("C");
        store.add("D");
        store.add("E");

        RecordingProcessor<String> recorder = new RecordingProcessor<>();
        store.subscribe(recorder);

        assertThat(recorder.insertedObjects).containsExactly("A", "B", "C", "D", "E");
    }

    @Test
    public void iteratorReturnsInsertionOrder() {
        DataStore<String> store = DataSource.createStore();
        store.add("X");
        store.add("Y");
        store.add("Z");

        List<String> iterated = new ArrayList<>();
        for (String s : (Iterable<String>) store) {
            iterated.add(s);
        }

        assertThat(iterated).containsExactly("X", "Y", "Z");
    }

    @Test
    public void subscribeAfterRemovePreservesOrder() {
        DataStore<String> store = DataSource.createStore();
        DataHandle dhA = store.add("A");
        store.add("B");
        DataHandle dhC = store.add("C");
        store.add("D");

        store.remove(dhA);
        store.remove(dhC);

        RecordingProcessor<String> recorder = new RecordingProcessor<>();
        store.subscribe(recorder);

        assertThat(recorder.insertedObjects).containsExactly("B", "D");
    }

    @Test
    public void ruleUnitDataStoreReplayOrder() {
        DataStoreOrderUnit unit = new DataStoreOrderUnit();
        unit.getStrings().add("A");
        unit.getStrings().add("B");
        unit.getStrings().add("C");
        unit.getStrings().add("D");
        unit.getStrings().add("E");

        List<String> insertedObjects = new ArrayList<>();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getRuleRuntimeListeners().add(new RuleRuntimeEventListener() {
            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                if (event.getObject() instanceof String) {
                    insertedObjects.add((String) event.getObject());
                }
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
            }

            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
            }
        });

        try (RuleUnitInstance<DataStoreOrderUnit> instance =
                     RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig)) {
            assertThat(insertedObjects).containsExactly("A", "B", "C", "D", "E");
        }
    }

    private static class RecordingProcessor<T> implements DataProcessor<T> {

        final List<T> insertedObjects = new ArrayList<>();

        @Override
        public FactHandle insert(DataHandle handle, T object) {
            insertedObjects.add(object);
            return null;
        }

        @Override
        public void update(DataHandle handle, T object) {
        }

        @Override
        public void delete(DataHandle handle) {
        }
    }
}
