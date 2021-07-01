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
package org.kie.pmml.models.mining.evaluator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.api.enums.ResultCode.FAIL;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.AVERAGE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MAJORITY_VOTE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MAX;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MEDIAN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.MODEL_CHAIN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SELECT_ALL;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SELECT_FIRST;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.SUM;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_AVERAGE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_MAJORITY_VOTE;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_MEDIAN;
import static org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD.WEIGHTED_SUM;

public class PMMLMiningModelEvaluatorTest {

    private final static List<MULTIPLE_MODEL_METHOD> RAW_OBJECT_METHODS = Arrays.asList(MAJORITY_VOTE,
                                                                                        SELECT_ALL,
                                                                                        SELECT_FIRST);
    private final static List<MULTIPLE_MODEL_METHOD> VALUE_WEIGHT_METHODS = Arrays.asList(MAX,
                                                                                          SUM,
                                                                                          MEDIAN,
                                                                                          AVERAGE,
                                                                                          WEIGHTED_SUM,
                                                                                          WEIGHTED_MEDIAN,
                                                                                          WEIGHTED_AVERAGE);
    private final static List<MULTIPLE_MODEL_METHOD> NOT_IMPLEMENTED_METHODS = Arrays.asList(MODEL_CHAIN,
                                                                                             WEIGHTED_MAJORITY_VOTE);
    private PMMLMiningModelEvaluator evaluator;

    @Before
    public void setUp() {
        evaluator = new PMMLMiningModelEvaluator();
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.MINING_MODEL, evaluator.getPMMLModelType());
    }

    @Test
    public void getPMML4ResultOK() {
        String name = "NAME";
        String targetField = "TARGET";
        String prediction = "FIRST_VALUE";
        final Map<String, Object> outputFieldsMap = new HashMap<>();
        IntStream.range(0,3).forEach(index -> {
            outputFieldsMap.put("KEY_" + index, "OBJECT_" + index);
        });
        KiePMMLSegmentation kiePMMLSegmentation = KiePMMLSegmentation.builder("SEGM_1", Collections.emptyList(), SELECT_FIRST).build();
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField(targetField)
                .withSegmentation(kiePMMLSegmentation)
                .withOutputFieldsMap(outputFieldsMap)
                .build();
        final LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        inputData.put("FIRST_KEY", new KiePMMLNameValue("FIRST_NAME", prediction));
        inputData.put("SECOND_KEY", new KiePMMLNameValue("SECOND_NAME", "SECOND_VALUE"));
        PMML4Result retrieved = evaluator.getPMML4Result(kiePMMLMiningModel, inputData);
        assertNotNull(retrieved);
        assertEquals(OK.getName(), retrieved.getResultCode());
        assertEquals(targetField, retrieved.getResultObjectName());
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertTrue(resultVariables.containsKey(targetField));
        assertEquals(prediction, resultVariables.get(targetField));
    }

    @Test
    public void getPMML4ResultFAIL() {
        String name = "NAME";
        String targetField = "TARGET";
        final Map<String, Object> outputFieldsMap = new HashMap<>();
        IntStream.range(0,3).forEach(index -> {
            outputFieldsMap.put("KEY_" + index, "OBJECT_" + index);
        });
        KiePMMLSegmentation kiePMMLSegmentation = KiePMMLSegmentation.builder("SEGM_1", Collections.emptyList(), AVERAGE).build();
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField(targetField)
                .withSegmentation(kiePMMLSegmentation)
                .withOutputFieldsMap(outputFieldsMap)
                .build();
        final LinkedHashMap<String, KiePMMLNameValue> inputData = new LinkedHashMap<>();
        inputData.put("FIRST_KEY", new KiePMMLNameValue("FIRST_NAME", "FIRST_VALUE"));
        inputData.put("SECOND_KEY", new KiePMMLNameValue("SECOND_NAME", "SECOND_VALUE"));
        PMML4Result retrieved = evaluator.getPMML4Result(kiePMMLMiningModel, inputData);
        assertNotNull(retrieved);
        assertEquals(FAIL.getName(), retrieved.getResultCode());
        assertEquals(targetField, retrieved.getResultObjectName());
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertTrue(resultVariables.containsKey(targetField));
        assertNull(resultVariables.get(targetField));
    }

    @Test
    public void getPMMLRuntime() {
        final KieServices kieServices = KieServices.Factory.get();
        final KieContainer kieContainer = kieServices.newKieClasspathContainer();
        final KieBase kieBase = kieContainer.getKieBase();
        String kModulePackageName = "kModulePackageNameA";
        String containerModelName = "containerModelNameA";
        PMMLRuntime firstRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertNotNull(firstRetrieved);
        assertTrue(firstRetrieved instanceof PMMLRuntimeInternal);
        PMMLRuntimeInternal firstPMMLRuntimeInternal = (PMMLRuntimeInternal) firstRetrieved;
        PMMLRuntime secondRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertTrue(secondRetrieved instanceof PMMLRuntimeInternal);
        PMMLRuntimeInternal secondPMMLRuntimeInternal = (PMMLRuntimeInternal) secondRetrieved;
        assertEquals(firstPMMLRuntimeInternal.getKnowledgeBase(), secondPMMLRuntimeInternal.getKnowledgeBase());
        kModulePackageName = "kModulePackageNameB";
        containerModelName = "containerModelNameB";
        PMMLRuntime thirdRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertNotNull(thirdRetrieved);
        assertTrue(thirdRetrieved instanceof PMMLRuntimeInternal);
        PMMLRuntimeInternal thirdPMMLRuntimeInternal = (PMMLRuntimeInternal) thirdRetrieved;
        assertNotEquals(firstPMMLRuntimeInternal.getKnowledgeBase(), thirdPMMLRuntimeInternal.getKnowledgeBase());
    }

    @Test
    public void getKiePMMLNameRawObject() {
        final Object rawObject = "OBJ";
        final PMML4Result pmml4Result = getPMML4Result(rawObject);
        RAW_OBJECT_METHODS.forEach(multipleModelMethod -> {
            KiePMMLNameValue retrieved = evaluator.getKiePMMLNameValue(pmml4Result, multipleModelMethod, 34.2);
            assertEquals(pmml4Result.getResultObjectName(), retrieved.getName());
            assertNotNull(retrieved.getValue());
            assertEquals(rawObject, retrieved.getValue());
        });
    }

    @Test
    public void getKiePMMLNameValueValueWeightNumber() {
        final Integer rawObject = 24;
        final PMML4Result pmml4Result = getPMML4Result(rawObject);
        final double weight = 2.23;
        double expected = rawObject.doubleValue();
        VALUE_WEIGHT_METHODS.forEach(multipleModelMethod -> {
            KiePMMLNameValue retrieved = evaluator.getKiePMMLNameValue(pmml4Result, multipleModelMethod, weight);
            assertNotNull(retrieved);
            assertEquals(pmml4Result.getResultObjectName(), retrieved.getName());
            assertNotNull(retrieved.getValue());
            assertTrue(retrieved.getValue() instanceof KiePMMLValueWeight);
            KiePMMLValueWeight kiePMMLValueWeight = (KiePMMLValueWeight) retrieved.getValue();
            assertEquals(expected, kiePMMLValueWeight.getValue(), 0.0);
            assertEquals(weight, kiePMMLValueWeight.getWeight(), 0.0);
        });
    }

    @Test
    public void getKiePMMLNameValueWeightNoNumber() {
        final PMML4Result pmml4Result = getPMML4Result("OBJ");
        VALUE_WEIGHT_METHODS.forEach(multipleModelMethod -> {
            try {
                evaluator.getKiePMMLNameValue(pmml4Result, multipleModelMethod, 34.2);
                fail(multipleModelMethod + " is supposed to throw exception because raw object is not a number");
            } catch (KiePMMLException e) {
                // expected
            }
        });
    }

    @Test
    public void getKiePMMLNameValueNotImplemented() {
        final PMML4Result pmml4Result = getPMML4Result("OBJ");
        NOT_IMPLEMENTED_METHODS.forEach(multipleModelMethod -> {
            try {
                evaluator.getKiePMMLNameValue(pmml4Result, multipleModelMethod, 34.2);
                fail(multipleModelMethod + " is supposed to throw exception because not implemented");
            } catch (KiePMMLException e) {
                // expected
            }
        });
    }

    @Test
    public void getEventuallyWeightedResultRawObject() {
        final Object rawObject = "OBJ";
        RAW_OBJECT_METHODS.forEach(multipleModelMethod -> {
            Object retrieved = evaluator.getEventuallyWeightedResult(rawObject, multipleModelMethod, 34.2);
            assertNotNull(retrieved);
            assertEquals(rawObject, retrieved);
        });
    }

    @Test
    public void getEventuallyWeightedResultValueWeightNumber() {
        final Integer rawObject = 24;
        final double weight = 2.23;
        VALUE_WEIGHT_METHODS.forEach(multipleModelMethod -> {
            Object retrieved = evaluator.getEventuallyWeightedResult(rawObject, multipleModelMethod, weight);
            assertNotNull(retrieved);
            assertTrue(retrieved instanceof KiePMMLValueWeight);
            KiePMMLValueWeight kiePMMLValueWeight = (KiePMMLValueWeight) retrieved;
            assertEquals(rawObject.doubleValue(), kiePMMLValueWeight.getValue(), 0.0);
            assertEquals(weight, kiePMMLValueWeight.getWeight(), 0.0);
        });
    }

    @Test
    public void getEventuallyWeightedResultValueWeightNoNumber() {
        VALUE_WEIGHT_METHODS.forEach(multipleModelMethod -> {
            try {
                evaluator.getEventuallyWeightedResult("OBJ", multipleModelMethod, 34.2);
                fail(multipleModelMethod + " is supposed to throw exception because raw object is not a number");
            } catch (KiePMMLException e) {
                // expected
            }
        });
    }

    @Test
    public void getEventuallyWeightedResultNotImplemented() {
        NOT_IMPLEMENTED_METHODS.forEach(multipleModelMethod -> {
            try {
                evaluator.getEventuallyWeightedResult("OBJ", multipleModelMethod, 34.2);
                fail(multipleModelMethod + " is supposed to throw exception because not implemented");
            } catch (KiePMMLException e) {
                // expected
            }
        });
    }

    @Test
    public void validateKiePMMLMiningModel() {
        String name = "NAME";
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField("TARGET")
                .build();
        evaluator.validate(kiePMMLMiningModel);
    }

    @Test(expected = KiePMMLModelException.class)
    public void validateNoKiePMMLMiningModel() {
        String name = "NAME";
        KiePMMLModel kiePMMLModel = new KiePMMLTestingModel(name, Collections.emptyList());
        evaluator.validate(kiePMMLModel);
    }

    @Test
    public void validateMiningTargetField() {
        String name = "NAME";
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField("TARGET")
                .build();
        evaluator.validateMining(kiePMMLMiningModel);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void validateMiningEmptyTargetField() {
        String name = "NAME";
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField("     ")
                .build();
        evaluator.validateMining(kiePMMLMiningModel);
    }

    @Test(expected = KiePMMLInternalException.class)
    public void validateMiningNoTargetField() {
        String name = "NAME";
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES).build();
        evaluator.validateMining(kiePMMLMiningModel);
    }

    private PMML4Result getPMML4Result(Object rawObject) {
        final String resultObjectName = "RESULT";
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(resultObjectName, rawObject);
        toReturn.setResultObjectName(resultObjectName);
        return toReturn;
    }
}
