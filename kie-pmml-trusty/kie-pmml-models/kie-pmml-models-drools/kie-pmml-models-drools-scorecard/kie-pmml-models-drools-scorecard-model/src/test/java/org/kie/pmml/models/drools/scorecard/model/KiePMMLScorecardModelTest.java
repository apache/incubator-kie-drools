/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.scorecard.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.RESULT_FEATURE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLScorecardModelTest {

    private static final String MODEL_NAME = "SCORE_MODEL";
    private KiePMMLScorecardModel model;
    private List<KiePMMLOutputField> outputFields;
    private Map<String, Object> outputFieldsMap;

    @Before
    public void setup() {
        outputFields = getOutputFields();
        outputFieldsMap = getOutputFieldsMap();
    }

    @Test
    public void populateWithOutputFieldsNoOutputFields() {
        model = KiePMMLScorecardModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        PMML4Result toPopulate = new PMML4Result();
        model.populateWithOutputFields(toPopulate);
        assertTrue(toPopulate.getResultVariables().isEmpty());
        model = KiePMMLScorecardModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withOutputFields(Collections.emptyList())
                .build();
        toPopulate = new PMML4Result();
        model.populateWithOutputFields(toPopulate);
        assertTrue(toPopulate.getResultVariables().isEmpty());
    }

    @Test
    public void populateWithOutputFieldsNoOutputFieldsMap() {
        model = KiePMMLScorecardModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withOutputFields(outputFields)
                .build();
        PMML4Result toPopulate = new PMML4Result();
        model.populateWithOutputFields(toPopulate);
        assertFalse(toPopulate.getResultVariables().isEmpty());
        for (KiePMMLOutputField outputField : outputFields) {
            assertTrue(toPopulate.getResultVariables().containsKey(outputField.getName()));
            assertNull(toPopulate.getResultVariables().get(outputField.getName()));
        }
        model = KiePMMLScorecardModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withOutputFields(outputFields)
                .withOutputFieldsMap(Collections.emptyMap())
                .build();
        toPopulate = new PMML4Result();
        model.populateWithOutputFields(toPopulate);
        assertFalse(toPopulate.getResultVariables().isEmpty());
        for (KiePMMLOutputField outputField : outputFields) {
            assertTrue(toPopulate.getResultVariables().containsKey(outputField.getName()));
            assertNull(toPopulate.getResultVariables().get(outputField.getName()));
        }
    }

    @Test
    public void populateWithOutputFields() {
        model = KiePMMLScorecardModel.builder(MODEL_NAME, Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withOutputFields(outputFields)
                .withOutputFieldsMap(outputFieldsMap)
                .build();
        PMML4Result toPopulate = new PMML4Result();
        model.populateWithOutputFields(toPopulate);
        assertFalse(toPopulate.getResultVariables().isEmpty());
        for (int i = outputFields.size() -1; i == 0; i --) {
            KiePMMLOutputField outputField = outputFields.get(i);
            assertTrue(toPopulate.getResultVariables().containsKey(outputField.getName()));
            // This test works as it is because relationship outputFields rank and outputFieldsMap is made in reverse order
            assertEquals("REASON_CODE_" + i, toPopulate.getResultVariables().get(outputField.getName()));
        }
    }

    private List<KiePMMLOutputField> getOutputFields() {
        return IntStream.range(1, 4)
                .mapToObj(value -> KiePMMLOutputField.builder("RANK-" + value, Collections.emptyList())
                        .withRank(value)
                        .withResultFeature(RESULT_FEATURE.REASON_CODE)
                        .build()).collect(Collectors.toList());
    }

    private Map<String, Object> getOutputFieldsMap() {
        return IntStream.range(1, 4).boxed().collect(Collectors.toMap(index -> "REASON_CODE_" + index,
                                                                      Integer::doubleValue));
    }
}