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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.PMML_STEP;
import org.kie.pmml.api.identifiers.KiePmmlComponentRoot;
import org.kie.pmml.api.identifiers.PmmlIdFactory;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.commons.utils.PMMLLoaderUtils;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.TestingHelper.commonEvaluateEfestoOutputPMML;
import static org.kie.pmml.TestingHelper.commonEvaluatePMML4Result;
import static org.kie.pmml.TestingHelper.commonValuateStep;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.TestingHelper.getPMMLRequestData;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PMMLRuntimeHelperTest {

    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private static KiePMMLModel modelMock;

    private static ModelLocalUriId modelLocalUriId;

    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        modelMock = getKiePMMLModelMock();
        modelLocalUriId = new EfestoAppRoot()
                .get(KiePmmlComponentRoot.class)
                .get(PmmlIdFactory.class)
                .get(FILE_NAME, getSanitizedClassName(MODEL_NAME));
    }

    @Test
    void canManageEfestoInput() {
        EfestoRuntimeContext runtimeContext =
                EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        EfestoInput<PMMLRequestData> inputPMML = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        assertThat(PMMLRuntimeHelper.canManageEfestoInput(inputPMML, runtimeContext)).isTrue();
    }

    @Test
    void execute() {
        EfestoInputPMML darInputPMML = new EfestoInputPMML(modelLocalUriId, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = PMMLRuntimeHelper.executeEfestoInputPMML(darInputPMML,
                                                                                        getPMMLContext(FILE_NAME,
                                                                                                       MODEL_NAME,
                                                                                                       memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), darInputPMML);
    }

    @Test
    void getPMMLModels() {
        List<PMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModels(getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                                   memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().hasSize(1); // defined in IndexFile.pmml_json
        assertThat(retrieved.get(0)).isInstanceOf(KiePMMLTestingModel.class);
    }

    @Test
    void getPMMLModelFromClassLoader() {
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(modelLocalUriId,
                                                                                          getPMMLContext(FILE_NAME,
                                                                                                         MODEL_NAME,
                                                                                                         memoryCompilerClassLoader));
        Optional<KiePMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModel(kiePmmlModelFactory.getKiePMMLModels(),
                                                                          FILE_NAME,
                                                                          MODEL_NAME);
        assertThat(retrieved).isNotNull().isPresent();
        retrieved = PMMLRuntimeHelper.getPMMLModel(kiePmmlModelFactory.getKiePMMLModels(), "FileName", "NoTestMod");
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluate() {
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(modelLocalUriId, pmmlContext);
        List<KiePMMLModel> kiePMMLModels = kiePmmlModelFactory.getKiePMMLModels();
        PMML4Result retrieved = PMMLRuntimeHelper.evaluate(kiePMMLModels, pmmlContext);
        assertThat(retrieved).isNotNull();
        commonEvaluatePMML4Result(retrieved, pmmlContext.getRequestData());
    }

    @Test
    public void evaluateWithPMMLContextListeners() {
        final List<PMMLStep> pmmlSteps = new ArrayList<>();
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME,
                                                        Collections.singleton(getPMMLListener(pmmlSteps)),
                                                        memoryCompilerClassLoader);
        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(modelLocalUriId, pmmlContext);
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
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);

        KiePMMLModelFactory kiePmmlModelFactory = PMMLLoaderUtils.loadKiePMMLModelFactory(modelLocalUriId, pmmlContext);
        EfestoInputPMML efestoInputPMML = new EfestoInputPMML(modelLocalUriId, pmmlContext);
        EfestoOutputPMML retrieved = PMMLRuntimeHelper.getEfestoOutput(kiePmmlModelFactory, efestoInputPMML);
        assertThat(retrieved).isNotNull();
        commonEvaluateEfestoOutputPMML(retrieved, efestoInputPMML);
    }

    @Test
    void getModelFromMemoryCLassloader() {
        PMMLRuntimeContext pmmlContext = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        Optional<PMMLModel> retrieved = PMMLRuntimeHelper.getPMMLModel(FILE_NAME,
                                                                       MODEL_NAME,
                                                                       pmmlContext);
        assertThat(retrieved).isNotNull().isPresent();
        retrieved = PMMLRuntimeHelper.getPMMLModel("FileName", "NoTestMod", pmmlContext);
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    public void getStep() {
        final PMMLRequestData requestData = getPMMLRequestData(MODEL_NAME, FILE_NAME);
        Arrays.stream(PMML_STEP.values()).forEach(pmml_step -> {
            PMMLStep retrieved = PMMLRuntimeHelper.getStep(pmml_step, modelMock, requestData);
            assertThat(retrieved).isNotNull();
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

    private PMMLListener getPMMLListener(final List<PMMLStep> pmmlSteps) {
        return pmmlSteps::add;
    }
}