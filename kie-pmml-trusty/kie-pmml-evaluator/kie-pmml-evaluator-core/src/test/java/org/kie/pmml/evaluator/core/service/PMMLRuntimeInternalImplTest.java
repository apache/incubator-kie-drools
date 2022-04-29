/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.evaluator.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.PMML_STEP;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PMMLRuntimeInternalImplTest {

    private PMMLContext pmmlContextMock;
    private KieBase kieBaseMock = mock(KieBase.class);
    private PMML4Result resultMock = mock(PMML4Result.class);
    private PMMLModelEvaluator evaluatorMock = mock(PMMLModelEvaluator.class);
    private PMMLModelEvaluatorFinderImpl pmmlModelExecutorFinderMock = mock(PMMLModelEvaluatorFinderImpl.class);
    private KiePMMLModel modelMock;
    private PMMLRuntimeInternalImpl pmmlRuntime;
    private static final String MODEL_NAME = "MODEL_NAME";

    @Before
    public void setup() {
        modelMock = getKiePMMLModelMock();
        when(resultMock.getResultCode()).thenReturn(OK.getName());
        when(resultMock.getResultVariables()).thenReturn(Collections.emptyMap());

        when(evaluatorMock.getPMMLModelType()).thenReturn(PMML_MODEL.TEST_MODEL);
        when(evaluatorMock.evaluate(any(), any(), any())).thenReturn(resultMock);
        List<PMMLModelEvaluator> modelEvaluators = Collections.singletonList(evaluatorMock);
        when(pmmlModelExecutorFinderMock.getImplementations(false)).thenReturn(modelEvaluators);

        pmmlRuntime = new PMMLRuntimeInternalImpl(kieBaseMock, pmmlModelExecutorFinderMock);
        pmmlContextMock = mock(PMMLContextImpl.class);
    }

    @Test
    public void addPMMLListener() {
        try {
            pmmlRuntime.evaluate(MODEL_NAME, pmmlContextMock);
            verify(pmmlContextMock, never()).addPMMLListener(any());
        } catch (KiePMMLException e) {
            commonManageException(e);
        }
        try {
            reset(pmmlContextMock);
            PMMLListener listener = getPMMLListener(new ArrayList<>());
            pmmlRuntime.addPMMLListener(listener);
            pmmlRuntime.evaluate(MODEL_NAME, pmmlContextMock);
            verify(pmmlContextMock).addPMMLListener(listener);
        } catch (KiePMMLException e) {
            commonManageException(e);
        }
    }

    @Test
    public void removePMMLListener() {
        try {
            PMMLListener listener = getPMMLListener(new ArrayList<>());
            pmmlRuntime.addPMMLListener(listener);
            pmmlRuntime.removePMMLListener(listener);
            pmmlRuntime.evaluate("MODEL_NAME", pmmlContextMock);
            verify(pmmlContextMock, never()).addPMMLListener(listener);
        } catch (KiePMMLException e) {
            commonManageException(e);
        }
    }

    @Test
    public void evaluateWithPmmlRuntimeListeners() {
        final PMMLRequestData requestData = getPMMLRequestData();
        final List<PMMLStep> pmmlSteps = new ArrayList<>();
        final PMMLContext pmmlContext = new PMMLContextImpl(requestData);
        pmmlRuntime.addPMMLListener(getPMMLListener(pmmlSteps));
        pmmlRuntime.evaluate(modelMock, pmmlContext);
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            Optional<PMMLStep> retrieved =
                    pmmlSteps.stream().filter(pmmlStep -> pmml_step.equals(((PMMLRuntimeStep) pmmlStep).getPmmlStep()))
                    .findFirst();
            assertTrue(retrieved.isPresent());
            commonValuateStep(retrieved.get(), pmml_step, modelMock, requestData);
        });
    }

    @Test
    public void evaluateWithPMMLContextListeners() {
        final PMMLRequestData requestData = getPMMLRequestData();
        final List<PMMLStep> pmmlSteps = new ArrayList<>();
        final PMMLContext pmmlContext = new PMMLContextImpl(requestData,
                                                            Collections.singleton(getPMMLListener(pmmlSteps)));
        pmmlRuntime.evaluate(modelMock, pmmlContext);
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            Optional<PMMLStep> retrieved =
                    pmmlSteps.stream().filter(pmmlStep -> pmml_step.equals(((PMMLRuntimeStep) pmmlStep).getPmmlStep()))
                    .findFirst();
            assertTrue(retrieved.isPresent());
            commonValuateStep(retrieved.get(), pmml_step, modelMock, requestData);
        });
    }

    @Test
    public void getStep() {
        final PMMLRequestData requestData = getPMMLRequestData();
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            PMMLStep retrieved = pmmlRuntime.getStep(pmml_step, modelMock, requestData);
            commonValuateStep(retrieved, pmml_step, modelMock, requestData);
        });
    }

    private void commonValuateStep(final PMMLStep toVerify, final PMML_STEP pmmlStep, final KiePMMLModel kiePMMLModel,
                                   final PMMLRequestData requestData) {
        assertThat(toVerify).isNotNull();
        assertTrue(toVerify instanceof PMMLRuntimeStep);
        assertEquals(pmmlStep, ((PMMLRuntimeStep) toVerify).getPmmlStep());
        Map<String, Object> info = toVerify.getInfo();
        assertEquals(info.get("MODEL"), kiePMMLModel.getName());
        assertEquals(info.get("CORRELATION ID"), requestData.getCorrelationId());
        assertEquals(info.get("REQUEST MODEL"), requestData.getModelName());
        requestData.getRequestParams()
                .forEach(requestParam ->
                                 assertEquals(requestParam.getValue(), info.get(requestParam.getName())));
    }

    private void commonManageException(KiePMMLException toManage) {
        String expectedMessage = String.format("Failed to retrieve model with name %s", MODEL_NAME);
        assertEquals(expectedMessage, toManage.getMessage());
    }

    private KiePMMLModel getKiePMMLModelMock() {
        KiePMMLModel toReturn = mock(KiePMMLModel.class);
        String targetFieldName = "targetFieldName";
        MiningField miningFieldMock = mock(MiningField.class);
        when(miningFieldMock.getName()).thenReturn(targetFieldName);
        when(miningFieldMock.getDataType()).thenReturn(DATA_TYPE.FLOAT);
        when(toReturn.getName()).thenReturn(MODEL_NAME);
        when(toReturn.getMiningFields()).thenReturn(Collections.singletonList(miningFieldMock));
        when(toReturn.getTargetField()).thenReturn(targetFieldName);
        when(toReturn.getPmmlMODEL()).thenReturn(PMML_MODEL.TEST_MODEL);
        return toReturn;
    }

    private PMMLRequestData getPMMLRequestData() {
        final PMMLRequestData toReturn = new PMMLRequestData();
        toReturn.setModelName(MODEL_NAME);
        toReturn.setCorrelationId("CORRELATION_ID");
        IntStream.range(0, 3).forEach(i -> toReturn.addRequestParam("PARAM_" + i, i));
        return toReturn;
    }

    private PMMLListener getPMMLListener(final List<PMMLStep> pmmlSteps) {
        return pmmlSteps::add;
    }
}