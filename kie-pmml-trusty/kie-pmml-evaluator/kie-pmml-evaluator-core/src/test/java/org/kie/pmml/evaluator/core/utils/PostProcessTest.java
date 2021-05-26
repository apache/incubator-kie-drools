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

package org.kie.pmml.evaluator.core.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImplTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PostProcessTest {

    @Test
    public void executeTargets() {
        // Build model
        String TARGET_NAME = "TARGET_NAME";
        String FIELD_NAME = "FIELD_NAME";
        KiePMMLTarget kiePMMLTarget = KiePMMLTarget.builder(TARGET_NAME, Collections.emptyList())
                .withMin(4.34)
                .withField(FIELD_NAME)
                .build();
        List<KiePMMLTarget> kiePMMLTargets = Arrays.asList(kiePMMLTarget, KiePMMLTarget.builder("NEW_TARGET", Collections.emptyList()).build());
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel model = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withKiePMMLTargets(kiePMMLTargets)
                .build();
        // Build PMML4Result
        PMML4Result toModify = new PMML4Result();
        toModify.setResultCode(ResultCode.FAIL.getName());
        toModify.addResultVariable(FIELD_NAME, 4.33);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        PostProcess.executeTargets(toModify, model);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        toModify.setResultCode(ResultCode.OK.getName());
        PostProcess.executeTargets(toModify, model);
        assertEquals(4.33, toModify.getResultVariables().get(FIELD_NAME));
        toModify.setResultObjectName(FIELD_NAME);
        PostProcess.executeTargets(toModify, model);
        assertEquals(4.34, toModify.getResultVariables().get(FIELD_NAME));
    }

    @Test
    public void populatePredictedOutputField() {
        final String OUTPUT_NAME = "OUTPUT_NAME";
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.PREDICTED_VALUE)
                .build();
        final PMML4Result toUpdate = new PMML4Result();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        PostProcess.populatePredictedOutputField(outputField, toUpdate, kiePMMLModel, kiePMMLNameValues);
        assertTrue(toUpdate.getResultVariables().isEmpty());
        //
        final String targetField = "targetField";
        outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.PREDICTED_VALUE)
                .withTargetField(targetField)
                .build();
        final Object targetValue = 54346.32454;
        final KiePMMLNameValue kiePMMLNameValue = new KiePMMLNameValue(targetField, targetValue);
        kiePMMLNameValues.add(kiePMMLNameValue);
        PostProcess.populatePredictedOutputField(outputField, toUpdate, kiePMMLModel, kiePMMLNameValues);
        assertFalse(toUpdate.getResultVariables().isEmpty());
        assertTrue(toUpdate.getResultVariables().containsKey(OUTPUT_NAME));
        assertEquals(kiePMMLNameValue.getValue(), toUpdate.getResultVariables().get(OUTPUT_NAME));
    }

    @Test(expected = KiePMMLException.class)
    public void populatePredictedOutputFieldWrongResultFeature() {
        final String OUTPUT_NAME = "OUTPUT_NAME";
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.ANTECEDENT)
                .build();
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        PostProcess.populateTransformedOutputField(outputField, new PMML4Result(), kiePMMLModel, Collections.emptyList());
    }

    @Test
    public void populateTransformedOutputField() {
        final String OUTPUT_NAME = "OUTPUT_NAME";
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .build();
        final PMML4Result toUpdate = new PMML4Result();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap = new HashMap<>();
        IntStream.range(0, 3)
                .forEach(i -> {
                    BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
                        String string = o != null ? o.toString() : "null";
                        return string + i;
                    };
                    functionsMap.put("function" + i, toPut);
                });

        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        PostProcess.populateTransformedOutputField(outputField, toUpdate, kiePMMLModel, kiePMMLNameValues);
        assertTrue(toUpdate.getResultVariables().isEmpty());
        //
        final String value = "String";
        final String CONSTANT_NAME = "CONSTANT_NAME";
        KiePMMLConstant kiePMMLConstant = new KiePMMLConstant(CONSTANT_NAME, Collections.emptyList(), value);

        outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.TRANSFORMED_VALUE)
                .withKiePMMLExpression(kiePMMLConstant)
                .build();
        PostProcess.populateTransformedOutputField(outputField, toUpdate, kiePMMLModel, kiePMMLNameValues);
        assertFalse(toUpdate.getResultVariables().isEmpty());
        assertTrue(toUpdate.getResultVariables().containsKey(OUTPUT_NAME));
        assertEquals(kiePMMLConstant.getValue(), toUpdate.getResultVariables().get(OUTPUT_NAME));
    }

    @Test(expected = KiePMMLException.class)
    public void populateTransformedOutputFieldWrongResultFeature() {
        final String OUTPUT_NAME = "OUTPUT_NAME";
        KiePMMLOutputField outputField = KiePMMLOutputField.builder(OUTPUT_NAME, Collections.emptyList())
                .withResultFeature(RESULT_FEATURE.ANTECEDENT)
                .build();
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        PostProcess.populateTransformedOutputField(outputField, new PMML4Result(), kiePMMLModel, Collections.emptyList());
    }

    @Test
    public void getValueFromKiePMMLExpressionKiePMMLApply() {
        final String functionName = "functionName";
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), functionName).build();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap = new HashMap<>();
        IntStream.range(0, 3)
                .forEach(i -> {
                    BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
                        String string = o != null ? o.toString() : "null";
                        return string + i;
                    };
                    functionsMap.put("function" + i, toPut);
                });
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withFunctionsMap(functionsMap)
                .build();
        //
        final  String nameValue = "NAMEVALUE";
        BiFunction<List<KiePMMLNameValue>, Object, Object>  toPut = (values, o) -> {
            Object fromNameValue = values.stream()
                    .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(nameValue))
                    .findFirst()
                    .map(KiePMMLNameValue::getValue)
                    .orElse(null);
            String string = fromNameValue != null ? fromNameValue.toString() : "null";
            return string + "-" + functionName;
        };
        functionsMap.put(functionName, toPut);
        Object variableValue = 425.46;
        kiePMMLNameValues.add(new KiePMMLNameValue(nameValue, variableValue));
        Optional<Object>  retrieved = PostProcess.getValueFromKiePMMLExpression(kiePMMLApply,
                                                                 kiePMMLModel,
                                                                 kiePMMLNameValues);
        assertTrue(retrieved.isPresent());
        Object expected = variableValue.toString() + "-" + functionName;
        assertEquals(expected, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLExpressionKiePMMLConstant() {
        final String value = "String";
        KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), value);

        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        //
        Optional<Object>  retrieved = PostProcess.getValueFromKiePMMLExpression(kiePMMLConstant,
                                                                                kiePMMLModel,
                                                                                Collections.emptyList());
        assertTrue(retrieved.isPresent());
        assertEquals(value, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLExpressionKiePMMLFieldRef() {
        final String mapMissingTo = "mapMissingTo";
        final String variableName = "variableName";
        KiePMMLExpression kiePMMLFieldRef = new KiePMMLFieldRef(variableName, Collections.emptyList(), mapMissingTo);

        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .build();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        final Object variableValue = 543.65434;
        kiePMMLNameValues.add(new KiePMMLNameValue(variableName, variableValue));
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLExpression(kiePMMLFieldRef,
                                                                               kiePMMLModel,
                                                                               kiePMMLNameValues);
        assertTrue(retrieved.isPresent());
        assertEquals(variableValue, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLApplyFunction() {
        final String functionName = "functionName";
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), functionName).build();
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap = new HashMap<>();
        IntStream.range(0, 3)
                .forEach(i -> {
                    BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
                        String string = o != null ? o.toString() : "null";
                        return string + i;
                    };
                    functionsMap.put("function" + i, toPut);
                });
        PMMLRuntimeInternalImplTest.KiePMMLTestingModel kiePMMLModel = PMMLRuntimeInternalImplTest.KiePMMLTestingModel.builder("TESTINGMODEL", Collections.emptyList(), MINING_FUNCTION.REGRESSION)
                .withFunctionsMap(functionsMap)
                .build();
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLApplyFunction(kiePMMLApply,
                                                                                  kiePMMLModel,
                                                                                  kiePMMLNameValues);
        assertFalse(retrieved.isPresent());
        //
        BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
            String string = o != null ? o.toString() : "null";
            return string + "-" + functionName;
        };
        functionsMap.put(functionName, toPut);
        retrieved = PostProcess.getValueFromKiePMMLApplyFunction(kiePMMLApply,
                                                                 kiePMMLModel,
                                                                 kiePMMLNameValues);
        assertTrue(retrieved.isPresent());
        Object expected =  "null-" + functionName;
        assertEquals(expected, retrieved.get());
        //
        final  String nameValue = "NAMEVALUE";
        toPut = (values, o) -> {
            Object fromNameValue = values.stream()
                    .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(nameValue))
                    .findFirst()
                    .map(KiePMMLNameValue::getValue)
                    .orElse(null);
            String string = fromNameValue != null ? fromNameValue.toString() : "null";
            return string + "-" + functionName;
        };
        functionsMap.put(functionName, toPut);
        Object variableValue = 425.46;
        kiePMMLNameValues.add(new KiePMMLNameValue(nameValue, variableValue));
        retrieved = PostProcess.getValueFromKiePMMLApplyFunction(kiePMMLApply,
                                                                 kiePMMLModel,
                                                                 kiePMMLNameValues);
        assertTrue(retrieved.isPresent());
        expected = variableValue.toString() + "-" + functionName;
        assertEquals(expected, retrieved.get());
    }

    @Test
    public void getValueFromFunctionsMapByFunctionName() {
        final String functionName = "functionName";
        final Object objectParameter = "objectParameter";
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        final Map<String, BiFunction<List<KiePMMLNameValue>, Object, Object>> functionsMap = new HashMap<>();
        IntStream.range(0, 3)
                .forEach(i -> {
                    BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
                        String string = o != null ? o.toString() : "null";
                        return string + i;
                    };
                    functionsMap.put("function" + i, toPut);
                });

        Optional<Object> retrieved = PostProcess.getValueFromFunctionsMapByFunctionName(functionsMap, functionName, kiePMMLNameValues, objectParameter);
        assertFalse(retrieved.isPresent());
        //
        BiFunction<List<KiePMMLNameValue>, Object, Object> toPut = (values, o) -> {
            String string = o != null ? o.toString() : "null";
            return string + "-" + functionName;
        };
        functionsMap.put(functionName, toPut);
        retrieved = PostProcess.getValueFromFunctionsMapByFunctionName(functionsMap, functionName, kiePMMLNameValues, objectParameter);
        assertTrue(retrieved.isPresent());
        Object expected = objectParameter.toString() + "-" + functionName;
        assertEquals(expected, retrieved.get());
        //
        final  String nameValue = "NAMEVALUE";
        toPut = (values, o) -> {
           Object fromNameValue = values.stream()
                   .filter(kiePMMLNameValue -> kiePMMLNameValue.getName().equals(nameValue))
                   .findFirst()
                   .map(KiePMMLNameValue::getValue)
                   .orElse(null);
            String string = fromNameValue != null ? fromNameValue.toString() : "null";
            return string + "-" + functionName;
        };
        functionsMap.put(functionName, toPut);
        Object variableValue = 425.46;
        kiePMMLNameValues.add(new KiePMMLNameValue(nameValue, variableValue));
        retrieved = PostProcess.getValueFromFunctionsMapByFunctionName(functionsMap, functionName, kiePMMLNameValues, objectParameter);
        assertTrue(retrieved.isPresent());
        expected = variableValue.toString() + "-" + functionName;
        assertEquals(expected, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLConstant() {
        KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), null);
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLConstant(kiePMMLConstant);
        assertFalse(retrieved.isPresent());
        final String value = "String";
        kiePMMLConstant = new KiePMMLConstant("NAME", Collections.emptyList(), value);
        retrieved = PostProcess.getValueFromKiePMMLConstant(kiePMMLConstant);
        assertTrue(retrieved.isPresent());
        assertEquals(value, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLApplyMapMissingTo() {
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "function").build();
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLApplyMapMissingTo(kiePMMLApply);
        assertFalse(retrieved.isPresent());
        final String mapMissingTo = "mapMissingTo";
        kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "function")
                .withMapMissingTo(mapMissingTo)
                .build();
        retrieved = PostProcess.getValueFromKiePMMLApplyMapMissingTo(kiePMMLApply);
        assertTrue(retrieved.isPresent());
        assertEquals(mapMissingTo, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLApplyMapDefaultValue() {
        KiePMMLApply kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "function").build();
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLApplyMapDefaultValue(kiePMMLApply);
        assertFalse(retrieved.isPresent());
        final String defaultValue = "defaultValue";
        kiePMMLApply = KiePMMLApply.builder("NAME", Collections.emptyList(), "function")
                .withDefaultValue(defaultValue)
                .build();
        retrieved = PostProcess.getValueFromKiePMMLApplyMapDefaultValue(kiePMMLApply);
        assertTrue(retrieved.isPresent());
        assertEquals(defaultValue, retrieved.get());
    }

    @Test
    public void getValueFromKiePMMLNameValuesByVariableName() {
        final String variableName = "variableName";
        final List<KiePMMLNameValue> kiePMMLNameValues = IntStream.range(0, 3).mapToObj(i -> new KiePMMLNameValue(
                "val-" + i, i)).collect(Collectors.toList());
        Optional<Object> retrieved = PostProcess.getValueFromKiePMMLNameValuesByVariableName(variableName,
                                                                                             kiePMMLNameValues);
        assertFalse(retrieved.isPresent());
        final Object variableValue = 243.94;
        kiePMMLNameValues.add(new KiePMMLNameValue(variableName, variableValue));
        retrieved = PostProcess.getValueFromKiePMMLNameValuesByVariableName(variableName, kiePMMLNameValues);
        assertTrue(retrieved.isPresent());
        assertEquals(variableValue, retrieved.get());
    }

    @Test
    public void getValueFromPMMLResultByVariableName() {
        final String variableName = "variableName";
        final PMML4Result pmml4Result = new PMML4Result();
        Optional<Object> retrieved = PostProcess.getValueFromPMMLResultByVariableName(variableName, pmml4Result);
        assertFalse(retrieved.isPresent());
        final Object variableValue = 243.94;
        pmml4Result.addResultVariable(variableName, variableValue);
        retrieved = PostProcess.getValueFromPMMLResultByVariableName(variableName, pmml4Result);
        assertTrue(retrieved.isPresent());
        assertEquals(variableValue, retrieved.get());
    }

    @Test
    public void getMissingValueFromKiePMMLFieldRefMapMissingTo() {
        KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef("NAME", Collections.emptyList(), null);
        Optional<Object> retrieved = PostProcess.getMissingValueFromKiePMMLFieldRefMapMissingTo(kiePMMLFieldRef);
        assertFalse(retrieved.isPresent());
        String mapMissingTo = "mapMissingTo";
        kiePMMLFieldRef = new KiePMMLFieldRef("NAME", Collections.emptyList(), mapMissingTo);
        retrieved = PostProcess.getMissingValueFromKiePMMLFieldRefMapMissingTo(kiePMMLFieldRef);
        assertTrue(retrieved.isPresent());
        assertEquals(mapMissingTo, retrieved.get());
    }

}