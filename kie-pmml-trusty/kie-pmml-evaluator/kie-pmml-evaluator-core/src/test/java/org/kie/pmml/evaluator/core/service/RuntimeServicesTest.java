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
package org.kie.pmml.evaluator.core.service;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.TestingHelper.getEfestoContext;
import static org.kie.pmml.TestingHelper.getInputData;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.TestingHelper.getPMMLRequestDataWithInputData;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

public class RuntimeServicesTest {

    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static RuntimeManager runtimeManager;

    private ModelLocalUriId modelLocalUriId;

    @BeforeAll
    public static void setup() {
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                                                                                                          " runtime " +
                                                                                                          "manager"));
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManageInputPMMLRuntimeContext() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        PMMLRuntimeContext context = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        BaseEfestoInput inputPMML = new EfestoInputPMML(modelLocalUriId, context);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, inputPMML);
        commonEvaluateEfestoOutput(retrieved, modelLocalUriId);
    }

    @Test
    void canManageInputPMMLRequestData() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        PMMLRequestData pmmlRequestData = getPMMLRequestDataWithInputData(MODEL_NAME, FILE_NAME);
        EfestoInput<PMMLRequestData> inputPMML = new BaseEfestoInput<>(modelLocalUriId, pmmlRequestData);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(getEfestoContext(memoryCompilerClassLoader)
                , inputPMML);
        commonEvaluateEfestoOutput(retrieved, modelLocalUriId);
    }

    @Test
    void canManageInputPMMLMapInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        BaseEfestoInput<Map<String, Object>> inputPMML = new BaseEfestoInput<>(modelLocalUriId,
                                                                               getInputData(MODEL_NAME, FILE_NAME));
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(getEfestoContext(memoryCompilerClassLoader)
                , inputPMML);
        commonEvaluateEfestoOutput(retrieved, modelLocalUriId);
    }

    private static void commonEvaluateEfestoOutput(Collection<EfestoOutput> toEvaluate,
                                                   ModelLocalUriId modelLocalUriId) {
        assertThat(toEvaluate).isNotNull().hasSize(1);
        assertThat(toEvaluate.iterator().next().getModelLocalUriId()).isEqualTo(modelLocalUriId);
    }
}
