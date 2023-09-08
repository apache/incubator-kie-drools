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
package org.kie.pmml.commons.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLOutputFieldTest {

    private static final String CUSTOM_FIELD = "CUSTOM_FIELD";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    void getValueFromKiePMMLNameValuesByVariableName() {
        final String variableName = "variableName";
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        Optional<Object> retrieved = KiePMMLOutputField.getValueFromKiePMMLNameValuesByVariableName(variableName,
                                                                                                    kiePMMLNameValues);
        assertThat(retrieved).isNotPresent();
        final Object variableValue = 243.94;
        kiePMMLNameValues.add(new KiePMMLNameValue(variableName, variableValue));
        retrieved = KiePMMLOutputField.getValueFromKiePMMLNameValuesByVariableName(variableName, kiePMMLNameValues);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(variableValue);
    }

    @Test
    void getValueFromPMMLResultByVariableName() {
        final String variableName = "variableName";
        final Map<String, Object> resultsVariables = new HashMap<>();
        Optional<Object> retrieved = KiePMMLOutputField.getValueFromPMMLResultByVariableName(variableName,
                                                                                             resultsVariables);
        assertThat(retrieved).isNotPresent();
        final Object variableValue = 243.94;
        resultsVariables.put(variableName, variableValue);
        retrieved = KiePMMLOutputField.getValueFromPMMLResultByVariableName(variableName, resultsVariables);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(variableValue);
    }

    @Test
    void evaluatePredictedValue() {
        final String variableName = "variableName";
        KiePMMLOutputField kiePMMLOutputField = KiePMMLOutputField.builder("outputfield", Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.PREDICTED_VALUE)
                .withTargetField(variableName)
                .build();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(),
                                                       kiePMMLNameValues, Collections.emptyList());
        assertThat(kiePMMLOutputField.evaluate(processingDTO)).isNull();
        final Object variableValue = 243.94;
        kiePMMLNameValues.add(new KiePMMLNameValue(variableName, variableValue));
        processingDTO = getProcessingDTO(Collections.emptyList(),
                                         kiePMMLNameValues, Collections.emptyList());
        Object retrieved = kiePMMLOutputField.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(variableValue);
    }

    @Test
    void evaluateReasonCodeValue() {
        KiePMMLOutputField kiePMMLOutputField = KiePMMLOutputField.builder("outputfield", Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.REASON_CODE)
                .withRank(4)
                .build();
        final List<String> reasonCodes = IntStream.range(0, 3).mapToObj(i ->
                                                                                "reasonCode-" + i)
                .collect(Collectors.toList());
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(), Collections.emptyList(), reasonCodes);
        assertThat(kiePMMLOutputField.evaluate(processingDTO)).isNull();
        final String variableValue = "reasonCode-3";
        reasonCodes.add(variableValue);
        processingDTO = getProcessingDTO(Collections.emptyList(), Collections.emptyList(), reasonCodes);
        Object retrieved = kiePMMLOutputField.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(variableValue);
    }

    @Test
    void evaluateTransformedValueFromConstant() {
        // <OutputField name="CUSTOM_FIELD" optype="continuous" dataType="double" feature="transformedValue">
        //     <Constant>100.0</Constant>
        // </OutputField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLOutputField outputField = KiePMMLOutputField.builder(CUSTOM_FIELD, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLConstant1)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(), new ArrayList<>(),
                                                       Collections.emptyList());
        Object retrieved = outputField.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value1);
    }

    @Test
    void evaluateTransformedValueFromFieldRef() {
        // <OutputField name="CUSTOM_FIELD" optype="continuous" dataType="double" feature="transformedValue">
        //     <FieldRef field="PARAM_1"/>
        // </OutputField>
        final KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLOutputField outputField = KiePMMLOutputField.builder(CUSTOM_FIELD, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLFieldRef)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(),
                                                       List.of(new KiePMMLNameValue(PARAM_1, value1)),
                                                       Collections.emptyList());
        Object retrieved = outputField.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value1);
    }

    @Test
    void evaluateTransformedValueFromApplyWithKiePMMLNameValues() {
        // <OutputField name="CUSTOM_FIELD" optype="continuous" dataType="double" feature="transformedValue">
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <FieldRef>PARAM_2</FieldRef>
        //      </Apply>
        // </OutputField>
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(PARAM_2, Collections.emptyList(), null);
        final KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLFieldRef2))
                .build();
        final KiePMMLOutputField outputField = KiePMMLOutputField.builder(CUSTOM_FIELD, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLApply)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList(), getKiePMMLNameValues(),
                                                       Collections.emptyList());
        Object retrieved = outputField.evaluate(processingDTO);
        Object expected = value1 / value2;
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void evaluateTransformedValueFromApplyWithOutputFields() {
        // <OutputField name="CUSTOM_FIELD" optype="continuous" dataType="double" feature="transformedValue">
        //     <Apply function="/">
        //        <FieldRef>PARAM_1</FieldRef>
        //        <FieldRef>PARAM_2</FieldRef>
        //      </Apply>
        // </OutputField>
        final KiePMMLFieldRef kiePMMLFieldRef1 = new KiePMMLFieldRef(PARAM_1, Collections.emptyList(), null);
        final KiePMMLFieldRef kiePMMLFieldRef2 = new KiePMMLFieldRef(PARAM_2, Collections.emptyList(), null);
        final KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "/")
                .withKiePMMLExpressions(Arrays.asList(kiePMMLFieldRef1, kiePMMLFieldRef2))
                .build();
        final KiePMMLOutputField outputField = KiePMMLOutputField.builder(CUSTOM_FIELD, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLApply)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        ProcessingDTO processingDTO = getProcessingDTO(getOutputFields(), new ArrayList<>(), Collections.emptyList());
        Object retrieved = outputField.evaluate(processingDTO);
        Object expected = value1 / value2;
        assertThat(retrieved).isEqualTo(expected);
    }

    private List<KiePMMLNameValue> getKiePMMLNameValues() {
        return Arrays.asList(new KiePMMLNameValue(PARAM_1, value1), new KiePMMLNameValue(PARAM_2, value2));
    }

    private List<KiePMMLOutputField> getOutputFields() {
        // <OutputField name="PARAM_1" optype="continuous" dataType="double" feature="transformedValue">
        //     <Constant>100.0</Constant>
        // </OutputField>
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant(PARAM_1, Collections.emptyList(), value1, null);
        final KiePMMLOutputField outputField1 = KiePMMLOutputField.builder(PARAM_1, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLConstant1)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        // <OutputField name="PARAM_1" optype="continuous" dataType="double" feature="transformedValue">
        //     <Constant>5.0</Constant>
        // </OutputField>
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant(PARAM_2, Collections.emptyList(), value2, null);
        final KiePMMLOutputField outputField2 = KiePMMLOutputField.builder(PARAM_2, Collections.emptyList())
                .withKiePMMLExpression(kiePMMLConstant2)
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        return Arrays.asList(outputField1, outputField2);
    }

}