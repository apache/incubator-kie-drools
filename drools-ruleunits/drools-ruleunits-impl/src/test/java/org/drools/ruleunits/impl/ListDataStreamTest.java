/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.impl;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListDataStreamTest {

    @Test
    public void testCreate() {
        Counter counter = new Counter();
        ListDataStream<Integer> integers = ListDataStream.create(1, 2, 3);
        integers.subscribe(counter);
        assertEquals(3, counter.count);
    }

    @Test
    public void testAppend() {
        Counter counter = new Counter();
        ListDataStream<Integer> integers = ListDataStream.create();
        integers.subscribe(counter);
        assertEquals(0, counter.count);
        integers.append(10);
        assertEquals(1, counter.count);
        integers.append(20);
        integers.append(30);
        assertEquals(3, counter.count);
    }

    private static class Counter<T> implements DataProcessor<T> {

        int count = 0;

        @Override
        public FactHandle insert(DataHandle handle, T object) {
            count++;
            return null;
        }

        @Override
        public void update(DataHandle handle, T object) {

        }

        @Override
        public void delete(DataHandle handle) {

        }
    }

    ;
}