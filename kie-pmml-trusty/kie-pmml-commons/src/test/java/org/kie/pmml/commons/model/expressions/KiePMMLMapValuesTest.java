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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLMapValuesTest {

    private static final KiePMMLInlineTable INLINE_TABLE;
    private static final List<KiePMMLFieldColumnPair> FIELDCOLUMNPAIRS;
    private static final String OUTPUTCOLUMN = "outputColumn";
    private static final String MAPMISSINGTO = "mapMissingTo";
    private static final String DEFAULTVALUE = "defaultValue";

    static {
        List<KiePMMLRow> rows = IntStream.range(0, 4)
                .mapToObj(i -> {
                    Map<String, Object> columnValues = IntStream.range(0, 3)
                            .boxed()
                            .collect(Collectors.toMap(j -> "KEY-" + i + "-" + j,
                                                      j -> "VALUE-" + i + "-" + j));
                    return new KiePMMLRow(columnValues);
                })
                .collect(Collectors.toList());
        INLINE_TABLE = new KiePMMLInlineTable("name", Collections.emptyList(), rows);
        FIELDCOLUMNPAIRS = IntStream.range(0, 2).mapToObj(i -> new KiePMMLFieldColumnPair("FIELD-" + i,
                                                                                          Collections.emptyList(),
                                                                                          "VALUE-1-" + i))
                .collect(Collectors.toList());
    }

    @Test
    void evaluateKeyNotFound() {
        KiePMMLMapValues kiePMMLMapValues = getKiePMMLMapValues();
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
        assertThat(kiePMMLMapValues.evaluate(processingDTO)).isEqualTo(MAPMISSINGTO);
    }

    @Test
    void evaluateKeyFoundNotMatching() {
        KiePMMLMapValues kiePMMLMapValues = getKiePMMLMapValues();
        List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 2)
                .mapToObj(i -> new KiePMMLNameValue("FIELD-" + i, "NOT-VALUE-1-" + i))
                .collect(Collectors.toList());
        ProcessingDTO processingDTO = getProcessingDTO(kiePMMLNameValues);
        assertThat(kiePMMLMapValues.evaluate(processingDTO)).isEqualTo(DEFAULTVALUE);
    }

    @Test
    void evaluateKeyFoundMatching() {
        KiePMMLMapValues kiePMMLMapValues = getKiePMMLMapValues();
        List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 2)
                .mapToObj(i -> new KiePMMLNameValue("FIELD-" + i, "VALUE-1-" + i))
                .collect(Collectors.toList());
        ProcessingDTO processingDTO = getProcessingDTO(kiePMMLNameValues);
        Object retrieved = kiePMMLMapValues.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
    }

    private KiePMMLMapValues getKiePMMLMapValues() {
        return KiePMMLMapValues.builder("name", Collections.emptyList(), OUTPUTCOLUMN)
                .withMapMissingTo(MAPMISSINGTO)
                .withDefaultValue(DEFAULTVALUE)
                .withKiePMMLInlineTable(INLINE_TABLE)
                .withKiePMMLFieldColumnPairs(FIELDCOLUMNPAIRS)
                .build();
    }
}