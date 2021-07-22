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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

/**
 * @see <a http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_Aggregate>function</a>
 */
public enum AGGREGATE_FUNCTIONS {

    COUNT("count", null),
    SUM("sum", BUILTIN_FUNCTIONS.SUM),
    AVERAGE("average", BUILTIN_FUNCTIONS.AVG),
    MIN("min", BUILTIN_FUNCTIONS.MIN),
    MAX("max", BUILTIN_FUNCTIONS.MAX),
    MULTISET("multiset", null);

    private final String name;
    private final BUILTIN_FUNCTIONS builtinFunctions;
    public static final ObjectMapper JSONIZER  = new ObjectMapper();


    AGGREGATE_FUNCTIONS(String name, BUILTIN_FUNCTIONS builtinFunctions) {
        this.name = name;
        this.builtinFunctions = builtinFunctions;
    }

    public static AGGREGATE_FUNCTIONS byName(String name) {
        return Arrays.stream(AGGREGATE_FUNCTIONS.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find AGGREGATE_FUNCTIONS with name: " + name));
    }

    public String getName() {
        return name;
    }

    public boolean requiresNumbers() {
        return builtinFunctions != null;
    }

    public Object getValue(final Object[] inputData, final String[] groupBy) {
        try {
            switch (this) {
                case COUNT:
                    return count(inputData, groupBy);
                case SUM:
                case AVERAGE:
                case MIN:
                case MAX:
                    return builtinFunctions.getValue(inputData);
                case MULTISET:
                    return multiset(inputData, groupBy);
                default:
                    throw new KiePMMLException("Unmanaged AGGREGATE_FUNCTIONS " + this);
            }
        } catch (JsonProcessingException e) {
            throw new KiePMMLException(e);
        }
    }

    static Object count(final Object[] inputData, final String[] groupBy) throws JsonProcessingException {
        if (groupBy == null || groupBy.length < 2) {
            return inputData.length;
        } else {
            List<Object> inputDataList = Arrays.asList(inputData);
            Map<String, Integer> countMap = new HashMap<>();
            for (String group : groupBy) {
                countMap.put(group, (int) inputDataList.stream().filter(input -> group.equals(input.toString())).count());
            }
            return getJsonStringFromMap(countMap);
        }
    }

    static String  multiset(final Object[] inputData, final String[] groupBy) throws JsonProcessingException {
        Map<String, List<Object>> multisetMap = new HashMap<>();
        List<Object> inputDataList = Arrays.asList(inputData);
        if (groupBy == null || groupBy.length < 2) {
            multisetMap.put("_null_", inputDataList);
        } else {
            for (String group : groupBy) {
                multisetMap.put(group, inputDataList.stream().filter(input -> group.equals(input.toString())).collect(Collectors.toList()));
            }
        }
        return getJsonStringFromMap(multisetMap);
    }

    static String getJsonStringFromMap(Map<String, ?> toConvert) throws JsonProcessingException {
        return JSONIZER.writerWithDefaultPrettyPrinter().writeValueAsString(toConvert);
    }

}
