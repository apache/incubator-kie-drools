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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieContainer;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.testingutility.PMMLContextTest;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PMMLMiningModelEvaluatorTest {

    private final static List<MULTIPLE_MODEL_METHOD> RAW_OBJECT_METHODS = Arrays.asList(MAJORITY_VOTE,
                                                                                        SELECT_ALL,
                                                                                        SELECT_FIRST,
                                                                                        MODEL_CHAIN);
    private final static List<MULTIPLE_MODEL_METHOD> VALUE_WEIGHT_METHODS = Arrays.asList(MAX,
                                                                                          SUM,
                                                                                          MEDIAN,
                                                                                          AVERAGE,
                                                                                          WEIGHTED_SUM,
                                                                                          WEIGHTED_MEDIAN,
                                                                                          WEIGHTED_AVERAGE);
    private final static List<MULTIPLE_MODEL_METHOD> NOT_IMPLEMENTED_METHODS = Arrays.asList(WEIGHTED_MAJORITY_VOTE);
    private PMMLMiningModelEvaluator evaluator;

    @Before
    public void setUp() {
        evaluator = new PMMLMiningModelEvaluator();
    }

    @Test
    public void getPMMLModelType() {
        assertThat(evaluator.getPMMLModelType()).isEqualTo(PMML_MODEL.MINING_MODEL);
    }

    @Test
    public void getPMML4ResultOK() {
        String name = "NAME";
        String targetField = "TARGET";
        String prediction = "FIRST_VALUE";
        KiePMMLSegmentation kiePMMLSegmentation = KiePMMLSegmentation.builder("SEGM_1", Collections.emptyList(), SELECT_FIRST).build();
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField(targetField)
                .withSegmentation(kiePMMLSegmentation)
                .build();
        final LinkedHashMap<String, PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple> inputData = new LinkedHashMap<>();
        inputData.put("FIRST_KEY", new PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple(new KiePMMLNameValue("FIRST_NAME", prediction), new ArrayList<>()));
        inputData.put("SECOND_KEY", new PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple(new KiePMMLNameValue("SECOND_NAME", "SECOND_VALUE"), new ArrayList<>()));
        PMML4Result retrieved = evaluator.getPMML4Result(kiePMMLMiningModel, inputData, new PMMLContextTest());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getResultCode()).isEqualTo(OK.getName());
        assertThat(retrieved.getResultObjectName()).isEqualTo(targetField);
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertThat(resultVariables).containsKey(targetField);
        assertThat(resultVariables.get(targetField)).isEqualTo(prediction);
    }

    @Test
    public void getPMML4ResultFAIL() {
        String name = "NAME";
        String targetField = "TARGET";
        KiePMMLSegmentation kiePMMLSegmentation = KiePMMLSegmentation.builder("SEGM_1", Collections.emptyList(), AVERAGE).build();
        KiePMMLMiningModel kiePMMLMiningModel = KiePMMLMiningModel.builder(name, Collections.emptyList(),
                                                                           MINING_FUNCTION.ASSOCIATION_RULES)
                .withTargetField(targetField)
                .withSegmentation(kiePMMLSegmentation)
                .build();
        final LinkedHashMap<String, PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple> inputData = new LinkedHashMap<>();
        inputData.put("FIRST_KEY", new PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple(new KiePMMLNameValue("FIRST_NAME", "FIRST_VALUE"), new ArrayList<>()));
        inputData.put("SECOND_KEY", new PMMLMiningModelEvaluator.KiePMMLNameValueProbabilityMapTuple(new KiePMMLNameValue("SECOND_NAME", "SECOND_VALUE"), new ArrayList<>()));
        PMML4Result retrieved = evaluator.getPMML4Result(kiePMMLMiningModel, inputData, new PMMLContextTest());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getResultCode()).isEqualTo(FAIL.getName());
        assertThat(retrieved.getResultObjectName()).isEqualTo(targetField);
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertThat(resultVariables).containsKey(targetField);
        assertThat(resultVariables.get(targetField)).isNull();
    }

    @Test
    public void getPMMLRuntime() {
        final KieServices kieServices = KieServices.Factory.get();
        final KieContainer kieContainer = kieServices.newKieClasspathContainer();
        final KieBase kieBase = kieContainer.getKieBase();
        String kModulePackageName = "kModulePackageNameA";
        String containerModelName = "containerModelNameA";
        PMMLRuntime firstRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertThat(firstRetrieved).isNotNull();
        assertThat(firstRetrieved).isInstanceOf(PMMLRuntimeInternal.class);
        PMMLRuntimeInternal firstPMMLRuntimeInternal = (PMMLRuntimeInternal) firstRetrieved;
        PMMLRuntime secondRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertThat(secondRetrieved).isInstanceOf(PMMLRuntimeInternal.class);
        PMMLRuntimeInternal secondPMMLRuntimeInternal = (PMMLRuntimeInternal) secondRetrieved;
        assertThat(secondPMMLRuntimeInternal.getKnowledgeBase()).isEqualTo(firstPMMLRuntimeInternal.getKnowledgeBase());
        kModulePackageName = "kModulePackageNameB";
        containerModelName = "containerModelNameB";
        PMMLRuntime thirdRetrieved = evaluator.getPMMLRuntime(kModulePackageName, kieBase, containerModelName);
        assertThat(thirdRetrieved).isNotNull();
        assertThat(thirdRetrieved).isInstanceOf(PMMLRuntimeInternal.class);
        PMMLRuntimeInternal thirdPMMLRuntimeInternal = (PMMLRuntimeInternal) thirdRetrieved;
        assertThat(firstPMMLRuntimeInternal.getKnowledgeBase()).isNotEqualTo(thirdPMMLRuntimeInternal.getKnowledgeBase());
    }

    @Test
    public void getKiePMMLNameRawObject() {
        final Object rawObject = "OBJ";
        final PMML4Result pmml4Result = getPMML4Result(rawObject);
        RAW_OBJECT_METHODS.forEach(multipleModelMethod -> {
            KiePMMLNameValue retrieved = evaluator.getKiePMMLNameValue(pmml4Result, multipleModelMethod, 34.2);
            assertThat(retrieved.getName()).isEqualTo(pmml4Result.getResultObjectName());
            assertThat(retrieved.getValue()).isNotNull();
            assertThat(retrieved.getValue()).isEqualTo(rawObject);
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
            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getName()).isEqualTo(pmml4Result.getResultObjectName());
            assertThat(retrieved.getValue()).isNotNull();
            assertThat(retrieved.getValue()).isInstanceOf(KiePMMLValueWeight.class);
            KiePMMLValueWeight kiePMMLValueWeight = (KiePMMLValueWeight) retrieved.getValue();
            assertThat(kiePMMLValueWeight.getValue()).isCloseTo(expected, Offset.offset(0.0));
            assertThat(kiePMMLValueWeight.getWeight()).isCloseTo(weight, Offset.offset(0.0));
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
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).isEqualTo(rawObject);
        });
    }

    @Test
    public void getEventuallyWeightedResultValueWeightNumber() {
        final Integer rawObject = 24;
        final double weight = 2.23;
        VALUE_WEIGHT_METHODS.forEach(multipleModelMethod -> {
            Object retrieved = evaluator.getEventuallyWeightedResult(rawObject, multipleModelMethod, weight);
            assertThat(retrieved).isNotNull();
            assertThat(retrieved).isInstanceOf(KiePMMLValueWeight.class);
            KiePMMLValueWeight kiePMMLValueWeight = (KiePMMLValueWeight) retrieved;
            assertThat(kiePMMLValueWeight.getValue()).isCloseTo(rawObject.doubleValue(), Offset.offset(0.0));
            assertThat(kiePMMLValueWeight.getWeight()).isCloseTo(weight, Offset.offset(0.0));
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

    @Test
    public void addStep() {
        PMMLStep step = mock(PMMLStep.class);
        Set<PMMLListener> pmmlListenersMock = IntStream.range(0, 3).mapToObj(i -> mock(PMMLListener.class)).collect(Collectors.toSet());
        PMMLContext pmmlContextMock = mock(PMMLContext.class);
        when(pmmlContextMock.getPMMLListeners()).thenReturn(pmmlListenersMock);
        evaluator.addStep(() -> step, pmmlContextMock);
        pmmlListenersMock.forEach(pmmlListenerMock -> verify(pmmlListenerMock).stepExecuted(step));
    }

    @Test
    public void getStep() {
        final String modelName = "MODEL_NAME";
        KiePMMLModel modelMock = mock(KiePMMLModel.class);
        when(modelMock.getName()).thenReturn(modelName);
        final String segmentName = "SEGMENT_NAME";
        KiePMMLSegment segmentMock = mock(KiePMMLSegment.class);
        when(segmentMock.getName()).thenReturn(segmentName);
        when(segmentMock.getModel()).thenReturn(modelMock);
        final String resultObjectName = "RESULT_OBJECT_NAME";
        final String resultObjectValue = "RESULT_OBJECT_VALUE";
        ResultCode resultCode = OK;
        PMML4Result pmml4Result = new PMML4Result();
        pmml4Result.setResultCode(resultCode.getName());
        pmml4Result.setResultObjectName(resultObjectName);
        pmml4Result.getResultVariables().put(resultObjectName, resultObjectValue);
        PMMLStep retrieved = evaluator.getStep(segmentMock, pmml4Result);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(PMMLMiningModelStep.class);
        Map<String, Object> retrievedInfo = retrieved.getInfo();
        assertThat(retrievedInfo).isNotNull();
        assertThat(retrievedInfo.get("SEGMENT")).isEqualTo(segmentName);
        assertThat(retrievedInfo.get("MODEL")).isEqualTo(modelName);
        assertThat(retrievedInfo.get("RESULT CODE")).isEqualTo(resultCode.getName());
        assertThat(retrievedInfo.get("RESULT")).isEqualTo(resultObjectValue);

        resultCode = FAIL;
        pmml4Result = new PMML4Result();
        pmml4Result.setResultCode(resultCode.getName());
        retrieved = evaluator.getStep(segmentMock, pmml4Result);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(PMMLMiningModelStep.class);
        retrievedInfo = retrieved.getInfo();
        assertThat(retrievedInfo).isNotNull();
        assertThat(retrievedInfo.get("SEGMENT")).isEqualTo(segmentName);
        assertThat(retrievedInfo.get("MODEL")).isEqualTo(modelName);
        assertThat(retrievedInfo.get("RESULT CODE")).isEqualTo(resultCode.getName());
        assertThat(retrievedInfo).doesNotContainKey("RESULT");
    }

    private PMML4Result getPMML4Result(Object rawObject) {
        final String resultObjectName = "RESULT";
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(resultObjectName, rawObject);
        toReturn.setResultObjectName(resultObjectName);
        return toReturn;
    }
}
