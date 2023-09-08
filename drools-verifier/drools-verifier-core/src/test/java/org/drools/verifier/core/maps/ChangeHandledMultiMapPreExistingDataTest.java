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

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.keys.Values;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeHandledMultiMapPreExistingDataTest {


    private MultiMap<Value, String, List<String>> map;
    private MultiMapChangeHandler.ChangeSet<Value, String> changeSet;

    private int timesCalled = 0;

    @BeforeEach
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = MultiMapFactory.make(true);

        this.map.put(new Value("hello"), "a");
        this.map.put(new Value("ok"), "b");
        this.map.put(new Value("ok"), "c");

        this.map.addChangeListener(new MultiMapChangeHandler<Value, String>() {
            @Override
            public void onChange(final ChangeSet<Value, String> changeSet) {
                ChangeHandledMultiMapPreExistingDataTest.this.changeSet = changeSet;
                timesCalled++;
            }
        });
    }

    @Test
    void move() throws Exception {
        map.move(new Values<>(new Value("ok")), new Values<>(new Value("hello")), "b");

        assertThat(timesCalled).isEqualTo(1);

        // Check data moved
        assertThat(map.get(new Value("hello"))).hasSize(2).contains("a", "b");
        assertThat(map.get(new Value("ok"))).hasSize(1).contains("c");

        // Updates should be up to date
        assertThat(changeSet.getRemoved().get(new Value("ok"))).hasSize(1);
        assertThat(changeSet.getAdded().get(new Value("hello"))).hasSize(1);
    }

    @Test
    void testRemove() throws Exception {
        map.remove(new Value("ok"));

        assertThat(changeSet.getRemoved().get(new Value("ok"))).hasSize(2);
        assertThat(timesCalled).isEqualTo(1);
    }

    @Test
    void testRemoveValue() throws Exception {
        map.removeValue(new Value("ok"), "b");

        assertThat(changeSet.getRemoved().get(new Value("ok"))).hasSize(1).contains("b");
        assertThat(timesCalled).isEqualTo(1);
    }

    @Test
    void testClear() throws Exception {
        map.clear();

        assertThat(changeSet.getRemoved().get(new Value("hello"))).hasSize(1).contains("a");
        assertThat(changeSet.getRemoved().get(new Value("ok"))).hasSize(2).contains("b", "c");
        assertThat(timesCalled).isEqualTo(1);
    }

    @Test
    void testMerge() throws Exception {
        final MultiMap<Value, String, List<String>> other = MultiMapFactory.make();
        other.put(new Value("hello"), "d");
        other.put(new Value("ok"), "e");
        other.put(new Value("newOne"), "f");

        MultiMap.merge(map, other);

        assertThat(changeSet.getAdded().get(new Value("hello"))).hasSize(1).contains("d");
        assertThat(changeSet.getAdded().get(new Value("ok"))).hasSize(1).contains("e");
        assertThat(changeSet.getAdded().get(new Value("newOne"))).hasSize(1).contains("f");

        assertThat(timesCalled).isEqualTo(1);
    }
}