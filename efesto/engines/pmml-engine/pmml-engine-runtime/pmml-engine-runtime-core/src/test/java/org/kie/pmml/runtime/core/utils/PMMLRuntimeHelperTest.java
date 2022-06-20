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
package org.kie.pmml.runtime.core.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;
import org.kie.pmml.runtime.core.PMMLContextImpl;
import org.kie.pmml.runtime.core.model.EfestoInputPMML;
import org.kie.pmml.runtime.core.model.EfestoOutputPMML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.api.enums.ResultCode.OK;

class PMMLRuntimeHelperTest {

    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static final String basePath = "testmod";
    
    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManage() {
        FRI fri = new FRI(basePath, "pmml");
        AbstractEfestoInput darInputPMML = new EfestoInputPMML(fri, getPMMLContext("fileName", "TestMod"));
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML)).isTrue();
        darInputPMML = new AbstractEfestoInput<String>(fri, ""){};
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML)).isFalse();
        fri = new FRI("darfoo", "pmml");
        darInputPMML = new EfestoInputPMML(fri, getPMMLContext("fileName", "TestMod"));
        assertThat(PMMLRuntimeHelper.canManage(darInputPMML)).isFalse();
    }

    @Test
    void execute() {
        FRI fri = new FRI(basePath, "pmml");
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext("fileName", "TestMod"));
        Optional<EfestoOutputPMML> retrieved = PMMLRuntimeHelper.execute(darInputPMML, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), darInputPMML);
    }

    @Test
    void redirect() {
    }

    @Test
    void loadKiePMMLModelFactory() {
        KiePMMLModelFactory retrieved = PMMLRuntimeHelper.loadKiePMMLModelFactory(new FRI(basePath, "pmml"), memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getKiePMMLModels()).hasSize(1);
        KiePMMLModel kiePmmlModel = retrieved.getKiePMMLModels().get(0);
        assertThat(kiePmmlModel.getName()).isEqualTo("TestMod");
    }

    @Test
    void loadNotExistingKiePMMLModelFactory() {
        try {
            PMMLRuntimeHelper.loadKiePMMLModelFactory(new FRI(basePath, "notpmml"), memoryCompilerClassLoader);
            fail("Expecting KieRuntimeServiceException");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(KieRuntimeServiceException.class);
        }
    }

    @Test
    void getEfestoOutput() {
        FRI fri = new FRI(basePath, "pmml");
        KiePMMLModelFactory kiePmmlModelFactory = PMMLRuntimeHelper.loadKiePMMLModelFactory(fri, memoryCompilerClassLoader);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext("fileName", "TestMod"));
        EfestoOutputPMML retrieved = PMMLRuntimeHelper.getEfestoOutput(kiePmmlModelFactory, darInputPMML);
        commonEvaluateEfestoOutputPMML(retrieved, darInputPMML);
    }

    @Test
    void evaluate() {
        FRI fri = new FRI(basePath, "pmml");
        KiePMMLModelFactory kiePmmlModelFactory = PMMLRuntimeHelper.loadKiePMMLModelFactory(fri, memoryCompilerClassLoader);
        List<KiePMMLModel> kiePMMLModels = kiePmmlModelFactory.getKiePMMLModels();
        PMMLContext pmmlContext = getPMMLContext("fileName", "TestMod");
        PMML4Result retrieved = PMMLRuntimeHelper.evaluate(kiePMMLModels, pmmlContext);
        commonEvaluatePMML4Result(retrieved, pmmlContext.getRequestData());
    }

    @Test
    void testEvaluate() {
        FRI fri = new FRI(basePath, "pmml");
        KiePMMLModelFactory kiePmmlModelFactory = PMMLRuntimeHelper.loadKiePMMLModelFactory(fri, memoryCompilerClassLoader);
        KiePMMLModel kiePMMLModel = kiePmmlModelFactory.getKiePMMLModels().get(0);
        PMMLContext pmmlContext = getPMMLContext("fileName", "TestMod");
        PMML4Result retrieved = PMMLRuntimeHelper.evaluate(kiePMMLModel, pmmlContext);
        commonEvaluatePMML4Result(retrieved, pmmlContext.getRequestData());
    }

    @Test
    void getModel() {
        FRI fri = new FRI(basePath, "pmml");
        KiePMMLModelFactory kiePmmlModelFactory = PMMLRuntimeHelper.loadKiePMMLModelFactory(fri, memoryCompilerClassLoader);
        Optional<KiePMMLModel> retrieved = PMMLRuntimeHelper.getModel(kiePmmlModelFactory.getKiePMMLModels(), "TestMod");
        assertThat(retrieved).isNotNull().isPresent();
        retrieved = PMMLRuntimeHelper.getModel(kiePmmlModelFactory.getKiePMMLModels(), "NoTestMod");
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluateRedirectInput() {
        // TODO for drools models
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

    private PMMLContext getPMMLContext(String fileName, String modelName) {
        return new PMMLContextImpl(getPMMLRequestData(modelName), fileName, memoryCompilerClassLoader);
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
}