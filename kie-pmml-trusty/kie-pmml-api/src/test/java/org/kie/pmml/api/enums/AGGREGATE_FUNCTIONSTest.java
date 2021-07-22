/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.api.enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS.JSONIZER;

public class AGGREGATE_FUNCTIONSTest {

    @Test
    public void getValue() {
    }

    @Test
    public void countNoGroupBy() throws JsonProcessingException {
        Object[] inputData = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        assertEquals(inputData.length, AGGREGATE_FUNCTIONS.count(inputData, null));
        String[] groupBy = new String[0];
        assertEquals(inputData.length, AGGREGATE_FUNCTIONS.count(inputData, groupBy));
    }

    @Test
    public void countGroupBy() throws JsonProcessingException {
        Map<String, List<Object>> grouped = getGroupedMap();
        String[] groupBy = grouped.keySet().toArray(new String[0]);
        Object[] inputData = grouped.values().stream().flatMap(Collection::stream).toArray(Object[]::new);
        Object retrieved = AGGREGATE_FUNCTIONS.count(inputData, groupBy);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof String);
        Map<String, Integer> retrievedMap = (Map) JSONIZER.readValue((String) retrieved, Map.class);
        for (String group : groupBy) {
            assertEquals(grouped.get(group).size(), (int) retrievedMap.get(group));
        }
    }

    @Test
    public void multisetNoGroupBy() throws JsonProcessingException {
        Object[] inputData = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        String retrieved = AGGREGATE_FUNCTIONS.multiset(inputData, null);
        assertNotNull(retrieved);
        Map<String, List<Object>> retrievedMap = (Map) JSONIZER.readValue(retrieved, Map.class);
        assertEquals(1, retrievedMap.size());
        List<Object> retrievedList = retrievedMap.get("_null_");
        assertEquals(inputData.length, retrievedList.size());
        for(Object inputItem : inputData) {
            assertTrue(retrievedList.contains(inputItem));
        }
    }

    @Test
    public void multisetGroupBy() throws JsonProcessingException {
        Map<String, List<Object>> grouped = getGroupedMap();
        String[] groupBy = grouped.keySet().toArray(new String[0]);
        Object[] inputData = grouped.values().stream().flatMap(Collection::stream).toArray(Object[]::new);
        String retrieved = AGGREGATE_FUNCTIONS.multiset(inputData, groupBy);
        assertNotNull(retrieved);
        System.out.println(retrieved);
        Map<String, List<Object>> retrievedMap = (Map) JSONIZER.readValue(retrieved, Map.class);
        System.out.println(retrievedMap);
        assertEquals(grouped.size(), retrievedMap.size());
        grouped.forEach((group, groupedList) -> {
            assertTrue(retrievedMap.containsKey(group));
            List<Object> retrievedList = retrievedMap.get(group);
            assertEquals(groupedList.size(), retrievedList.size());
            groupedList.forEach(groupedItem -> assertTrue(retrievedList.contains(groupedItem)));
        });
    }

    @Test
    public void getJsonStringFromMap() throws JsonProcessingException {
        Map<String, List<Object>> grouped = getGroupedMap();
        String retrieved = AGGREGATE_FUNCTIONS.getJsonStringFromMap(grouped);
        Map<String, List<Object>> retrievedMap = (Map) JSONIZER.readValue(retrieved, Map.class);
        assertEquals(grouped.size(), retrievedMap.size());
        grouped.forEach((group, groupedList) -> {
            assertTrue(retrievedMap.containsKey(group));
            List<Object> retrievedList = retrievedMap.get(group);
            assertEquals(groupedList.size(), retrievedList.size());
            groupedList.forEach(groupedItem -> assertTrue(retrievedList.contains(groupedItem)));
        });
    }

    private Map<String, List<Object>> getGroupedMap() {
        String[] groupBy = {"GROUP-0", "GROUP-1", "GROUP-2"};
        Map<String, List<Object>> toReturn = new HashMap<>();
        for (String group : groupBy) {
            int limit = new Random().nextInt(2) + 2;
            List<Object> toPut = IntStream.range(0, limit).mapToObj(i -> group).collect(Collectors.toList());
            toReturn.put(group, toPut);
        }
        return toReturn;
    }
}