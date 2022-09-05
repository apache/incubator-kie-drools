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
package org.kie.pmml.evaluator.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.PMML_STEP;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.utils.PMMLLoaderUtils;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.commons.Constants.PMML_STRING;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PMMLRuntimeHelperTest {

    private static final String basePath = "testmod";
    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private static KiePMMLModel modelMock;

    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        modelMock = getKiePMMLModelMock();
    }

    @Test
    void canManage() {
        FRI fri = new FRI(basePath, PMML_STRING);
        PMMLRuntimeContext context = getPMMLContext(FILE_NAME, MODEL_NAME);
        AbstractEfestoInput darInputPMML = new EfestoInputPMML(fri, context);
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML, context)).isTrue();
        darInputPMML = new AbstractEfestoInput<String>(fri, "") {
        };
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML, context)).isFalse();
        fri = new FRI("darfoo", PMML_STRING);
        context = getPMMLContext(FILE_NAME, MODEL_NAME);
        darInputPMML = new EfestoInputPMML(fri, context);
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML, context)).isFalse();
    }

    @Test
    void execute() {
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext(FILE_NAME, MODEL_NAME));
        Optional<EfestoOutputPMML> retrieved = PMMLRuntimeHelper.execute(darInputPMML, getPMMLContext(FILE_NAME,
                                                                                                      MODEL_NAME));
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), darInputPMML);
    }

    @Test
    void getPMMLModels() {
        List<PMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModels(getPMMLContext(FILE_NAME, MODEL_NAME));
        assertThat(retrieved).isNotNull().hasSize(1); // defined in IndexFile.pmml_json
        assertThat(retrieved.get(0)).isInstanceOf(KiePMMLTestingModel.class);
    }

    @Test
    void getPMMLModelFromClassLoader() {
        FRI fri = new FRI(basePath, PMML_STRING);
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(fri,
                                                                                          getPMMLContext(FILE_NAME,
                                                                                                         MODEL_NAME));
        Optional<KiePMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModel(kiePmmlModelFactory.getKiePMMLModels(),
                                                                          FILE_NAME,
                                                                          MODEL_NAME);
        assertThat(retrieved).isNotNull().isPresent();
        retrieved = PMMLRuntimeHelper.getPMMLModel(kiePmmlModelFactory.getKiePMMLModels(), "FileName", "NoTestMod");
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluate() {
        FRI fri = new FRI(basePath, PMML_STRING);
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME);
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(fri, pmmlContext);
        List<KiePMMLModel> kiePMMLModels = kiePmmlModelFactory.getKiePMMLModels();
        PMML4Result retrieved = PMMLRuntimeHelper.evaluate(kiePMMLModels, pmmlContext);
        commonEvaluatePMML4Result(retrieved, pmmlContext.getRequestData());
    }

    @Test
    public void evaluateWithPMMLContextListeners() {

        FRI fri = new FRI(basePath, PMML_STRING);
        final List<PMMLStep> pmmlSteps = new ArrayList<>();
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME,
                                                        Collections.singleton(getPMMLListener(pmmlSteps)));
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(fri, pmmlContext);
        KiePMMLModel kiePMMLModel = kiePmmlModelFactory.getKiePMMLModels().get(0);
        PMMLRuntimeHelper.evaluate(kiePMMLModel, pmmlContext);
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            Optional<PMMLStep> retrieved =
                    pmmlSteps.stream().filter(pmmlStep -> pmml_step.equals(((PMMLRuntimeStep) pmmlStep).getPmmlStep
                                    ()))
                            .findFirst();
            assertThat(retrieved).isPresent();
            commonValuateStep(retrieved.get(), pmml_step, kiePMMLModel, pmmlContext.getRequestData());
        });
    }

    @Test
    void getEfestoOutput() {
        FRI fri = new FRI(basePath, PMML_STRING);
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME);

        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(fri, pmmlContext);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, pmmlContext);
        EfestoOutputPMML retrieved = PMMLRuntimeHelper.getEfestoOutput(kiePmmlModelFactory, darInputPMML);
        commonEvaluateEfestoOutputPMML(retrieved, darInputPMML);
    }

    @Test
    void getModelFromMemoryCLassloader() {
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME);
        Optional<PMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModel(FILE_NAME,
                                                                       MODEL_NAME,
                                                                       pmmlContext);
        assertThat(retrieved).isNotNull().isPresent();
        retrieved = PMMLRuntimeHelper.getPMMLModel("FileName", "NoTestMod", pmmlContext);
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    public void getStep() {
        final PMMLRequestData requestData = getPMMLRequestData();
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            PMMLStep retrieved = PMMLRuntimeHelper.getStep(pmml_step, modelMock, requestData);
            commonValuateStep(retrieved, pmml_step, modelMock, requestData);
        });
    }

    private static KiePMMLModel getKiePMMLModelMock() {
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

    private void commonEvaluateEfestoOutputPMML(EfestoOutputPMML toEvaluate, EfestoInputPMML darInputPMML) {
        assertThat(toEvaluate).isNotNull();
        assertThat(toEvaluate.getFRI()).isEqualTo(darInputPMML.getFRI());
        commonEvaluatePMML4Result(toEvaluate.getOutputData(), darInputPMML.getInputData().getRequestData());
    }

    private void commonEvaluatePMML4Result(PMML4Result toEvaluate, PMMLRequestData pmmlRequestData) {
        assertThat(toEvaluate).isNotNull();
        assertThat(toEvaluate.getResultCode()).isEqualTo(OK.getName());
        assertThat(toEvaluate.getCorrelationId()).isEqualTo(pmmlRequestData.getCorrelationId());
    }

    private void commonValuateStep(final PMMLStep toVerify, final PMML_STEP pmmlStep, final KiePMMLModel kiePMMLModel,
                                   final PMMLRequestData requestData) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify).isInstanceOf(PMMLRuntimeStep.class);
        assertThat(((PMMLRuntimeStep) toVerify).getPmmlStep()).isEqualTo(pmmlStep);
        Map<String, Object> info = toVerify.getInfo();
        assertThat(kiePMMLModel.getName()).isEqualTo(info.get("MODEL"));
        assertThat(requestData.getCorrelationId()).isEqualTo(info.get("CORRELATION ID"));
        assertThat(requestData.getModelName()).isEqualTo(info.get("REQUEST MODEL"));
        requestData.getRequestParams()
                .forEach(requestParam ->
                                 assertThat(info.get(requestParam.getName())).isEqualTo(requestParam.getValue()));
    }

    private PMMLRuntimeContext getPMMLContext(String fileName, String modelName, Set<PMMLListener> listeners) {
        return new PMMLRuntimeContextImpl(getPMMLRequestData(modelName), fileName, listeners, memoryCompilerClassLoader);
    }

    private PMMLRuntimeContext getPMMLContext(String fileName, String modelName) {
        return getPMMLContext(fileName, modelName, Collections.emptySet());
    }

    private PMMLRequestData getPMMLRequestData(String modelName) {
        Map<String, Object> inputData = getInputData();
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }

    private Map<String, Object> getInputData() {
        final Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("fld1", 23.2);
        toReturn.put("fld2", 11.34);
        toReturn.put("fld3", "x");
        toReturn.put("fld4", 34.1);
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