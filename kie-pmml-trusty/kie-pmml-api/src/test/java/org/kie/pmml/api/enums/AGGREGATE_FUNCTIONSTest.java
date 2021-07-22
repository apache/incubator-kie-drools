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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AGGREGATE_FUNCTIONSTest {

    private static Map<String, List<Object>> GROUPED_MAP;

    @BeforeClass
    public static void setup() {
        String[] groupBy = {"GROUP-0", "GROUP-1", "GROUP-2"};
        GROUPED_MAP = new HashMap<>();
        for (String group : groupBy) {
            int limit = new Random().nextInt(2) + 2;
            List<Object> toPut = IntStream.range(0, limit).mapToObj(i -> group).collect(Collectors.toList());
            GROUPED_MAP.put(group, toPut);
        }
    }

    @Test
    public void count()  {
        Object[] inputData = IntStream.range(0, 4).boxed().toArray(Integer[]::new);
        assertEquals(inputData.length, AGGREGATE_FUNCTIONS.count(inputData));
    }


}