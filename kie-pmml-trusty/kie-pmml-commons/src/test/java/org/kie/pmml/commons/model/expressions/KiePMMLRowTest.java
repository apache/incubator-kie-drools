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
package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLRowTest {

    private static final Map<String, Object> COLUMN_VALUES;
    private static final String REGEX_FIELD = "regexField";

    static {
        COLUMN_VALUES = IntStream.range(0, 4)
                .boxed()
                .collect(Collectors.toMap(i -> "KEY-" + i,
                                          integer -> integer));
        COLUMN_VALUES.put(REGEX_FIELD, true);
    }

    @Test
    void evaluateKeyNotFound() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("NOT-KEY", 0), "KEY-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundNotMatching() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", 435345), "KEY-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatching() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", 1), "KEY-0", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(COLUMN_VALUES.get("KEY-0"));
    }

    @Test
    void evaluateKeyFoundNotMatchingRegex() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", "[435345]"), "KEY-0",
                                                         REGEX_FIELD);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatchingRegex() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", "[0-9]"), "KEY-0",
                                                         REGEX_FIELD);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(COLUMN_VALUES.get("KEY-0"));
    }

    @Test
    void evaluateKeyFoundMultipleNotMatching() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Map<String, Object> columnPairsMap = IntStream.range(0, 3).boxed()
                .collect(Collectors.toMap(i -> "KEY-" + i,
                                          integer -> integer));
        columnPairsMap.put("NOT-KEY", 4);
        Optional<Object> retrieved = kiePMMLRow.evaluate(columnPairsMap, "KEY-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatchingNoOutputColumnFound() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", 1), "NOT-KEY", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatchingOutputColumnFound() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Optional<Object> retrieved = kiePMMLRow.evaluate(Collections.singletonMap("KEY-1", 1), "KEY-0", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(COLUMN_VALUES.get("KEY-0"));
    }

    @Test
    void evaluateKeyFoundMultipleMatching() {
        KiePMMLRow kiePMMLRow = new KiePMMLRow(COLUMN_VALUES);
        Map<String, Object> columnPairsMap = IntStream.range(0, 3).boxed()
                .collect(Collectors.toMap(i -> "KEY-" + i,
                                          integer -> integer));
        Optional<Object> retrieved = kiePMMLRow.evaluate(columnPairsMap, "KEY-0", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(COLUMN_VALUES.get("KEY-0"));
    }

}