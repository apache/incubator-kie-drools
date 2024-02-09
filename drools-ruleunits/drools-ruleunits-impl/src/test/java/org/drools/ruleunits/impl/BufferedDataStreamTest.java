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
package org.drools.ruleunits.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataProcessor;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class BufferedDataStreamTest {

    @Test
    public void testCreate() {
        Collector collector = new Collector();
        DataStream<Integer> integers = DataSource.createBufferedStream(2);
        integers.append(1);
        integers.append(2);
        integers.append(3);
        integers.subscribe(collector);
        assertThat(collector.size()).isEqualTo(2);
        assertThat(collector.getList()).containsExactly(2, 3);
    }

    @Test
    public void testAppend() {
        Collector collector = new Collector();
        DataStream<Integer> integers = DataSource.createBufferedStream(2);
        integers.subscribe(collector);
        assertThat(collector.size()).isEqualTo(0);
        integers.append(10);
        assertThat(collector.size()).isEqualTo(1);
        integers.append(20);
        integers.append(30);
        assertThat(collector.size()).isEqualTo(3);
    }

    private static class Collector<T> implements DataProcessor<T> {

        private final List<T> list = new ArrayList<>();

        @Override
        public FactHandle insert(DataHandle handle, T object) {
            list.add(object);
            return null;
        }

        @Override
        public void update(DataHandle handle, T object) {

        }

        @Override
        public void delete(DataHandle handle) {

        }

        public int size() {
            return list.size();
        }

        public List<T> getList() {
            return list;
        }
    }
}