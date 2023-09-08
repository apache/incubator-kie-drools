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

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.TestingHelper.commonEvaluateEfestoOutputPMML;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.commons.CommonTestingUtility.getModelLocalUriIdFromPmmlIdFactory;

class KieRuntimeServicePMMLTest {

    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieRuntimeServicePMML kieRuntimeServicePMML;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private ModelLocalUriId modelLocalUriId;

    @BeforeAll
    public static void setup() {
        kieRuntimeServicePMML = new KieRuntimeServicePMML();
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManageInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        PMMLRuntimeContext context = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        BaseEfestoInput inputPMML = new EfestoInputPMML(modelLocalUriId, context);
        assertThat(kieRuntimeServicePMML.canManageInput(inputPMML, context)).isTrue();
    }

    @Test
    void evaluateCorrectInput() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        EfestoInputPMML inputPMML = new EfestoInputPMML(modelLocalUriId, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMML.evaluateInput(inputPMML,
                                                                                   getPMMLContext(FILE_NAME,
                                                                                                  MODEL_NAME,
                                                                                                  memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), inputPMML);
    }

    @Test
    void evaluateWrongIdentifier() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, "wrongmodel");
        EfestoInputPMML inputPMML = new EfestoInputPMML(modelLocalUriId, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                                           memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMML.evaluateInput(inputPMML,
                                                                                   getPMMLContext(FILE_NAME,
                                                                                                  MODEL_NAME,
                                                                                                  memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluateEfestoRuntimeContext() {
        modelLocalUriId = getModelLocalUriIdFromPmmlIdFactory(FILE_NAME, MODEL_NAME);
        EfestoRuntimeContext runtimeContext =
                EfestoRuntimeContextUtils.buildWithParentClassLoader(memoryCompilerClassLoader);
        EfestoInputPMML inputPMML = new EfestoInputPMML(modelLocalUriId, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMML.evaluateInput(inputPMML,
                                                                                   runtimeContext);
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), inputPMML);
    }
}