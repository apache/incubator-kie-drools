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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLInlineTableTest {

    private static final List<KiePMMLRow> ROWS;

    static {
        ROWS = IntStream.range(0, 4)
                .mapToObj(i -> {
                    Map<String, Object> columnValues = IntStream.range(0, 3)
                            .boxed()
                            .collect(Collectors.toMap(j -> "KEY-" + i + "-" + j,
                                                      j -> "VALUE-" + i + "-" + j));
                    return new KiePMMLRow(columnValues);
                })
                .collect(Collectors.toList());
    }

    @Test
    void evaluateKeyNotFound() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("NOT-KEY", 0), "KEY-0-0",
                                                                 null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundNotMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("KEY-1-1", 435345), "KEY-0" +
                "-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("KEY-1-1", "VALUE-1-1"),
                                                                 "KEY-1-2", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo("VALUE-1-2");
    }

    @Test
    void evaluateKeyFoundMultipleNotMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Map<String, Object> columnPairsMap = IntStream.range(0, 2).boxed()
                .collect(Collectors.toMap(i -> "KEY-1-" + i,
                                          i -> "VALUE-1-" + i));
        columnPairsMap.put("KEY-1-2", 4);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(columnPairsMap, "KEY-0-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMultipleMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Map<String, Object> columnPairsMap = IntStream.range(0, 3).boxed()
                .collect(Collectors.toMap(i -> "KEY-1-" + i,
                                          i -> "VALUE-1-" + i));
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(columnPairsMap, "KEY-1-2", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo("VALUE-1-2");
    }
}