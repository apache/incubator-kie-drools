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

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;
import org.kie.pmml.evaluator.core.model.EfestoOutputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.pmml.TestingHelper.commonEvaluateEfestoOutputPMML;
import static org.kie.pmml.TestingHelper.getPMMLContext;
import static org.kie.pmml.commons.Constants.PMML_STRING;

class KieRuntimeServicePMMLTest {

    private static final String basePath = "testmod";
    private static final String MODEL_NAME = "TestMod";
    private static final String FILE_NAME = "FileName";
    private static KieRuntimeServicePMML kieRuntimeServicePMML;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    @BeforeAll
    public static void setup() {
        kieRuntimeServicePMML = new KieRuntimeServicePMML();
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManageEfestoInputPMML() {
        FRI fri = new FRI(basePath, PMML_STRING);
        PMMLRuntimeContext context = getPMMLContext(FILE_NAME, MODEL_NAME, memoryCompilerClassLoader);
        AbstractEfestoInput darInputPMML = new EfestoInputPMML(fri, context);
        assertThat(kieRuntimeServicePMML.canManageInput(darInputPMML, context)).isTrue();
    }

    @Test
    void canManageEfestoInput() {
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoRuntimeContext runtimeContext =
                EfestoRuntimeContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        PMMLRequestData pmmlRequestData = new PMMLRequestData();
        EfestoInput<PMMLRequestData> inputPMML = new AbstractEfestoInput<>(fri, pmmlRequestData) {
        };
        assertThat(kieRuntimeServicePMML.canManageInput(inputPMML, runtimeContext)).isFalse();
    }

    @Test
    void evaluateCorrectInput() {
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMML.evaluateInput(darInputPMML,
                                                                                   getPMMLContext(FILE_NAME,
                                                                                                  MODEL_NAME,
                                                                                                  memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isPresent();
        commonEvaluateEfestoOutputPMML(retrieved.get(), darInputPMML);
    }

    @Test
    void evaluateWrongFRI() {
        FRI fri = new FRI("wrongbasepath", PMML_STRING);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        Optional<EfestoOutputPMML> retrieved = kieRuntimeServicePMML.evaluateInput(darInputPMML,
                                                                                   getPMMLContext(FILE_NAME,
                                                                                                  MODEL_NAME,
                                                                                                  memoryCompilerClassLoader));
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void evaluateWrongEfestoRuntimeContext() {
        FRI fri = new FRI(basePath, PMML_STRING);
        EfestoRuntimeContext runtimeContext =
                EfestoRuntimeContext.buildWithParentClassLoader(memoryCompilerClassLoader);
        EfestoInputPMML darInputPMML = new EfestoInputPMML(fri, getPMMLContext(FILE_NAME, MODEL_NAME,
                                                                               memoryCompilerClassLoader));
        KieRuntimeServiceException thrown = assertThrows(
                KieRuntimeServiceException.class,
                () -> kieRuntimeServicePMML.evaluateInput(darInputPMML,
                                                          runtimeContext),
                "Expected evaluateInput() to throw, but it didn't"
        );
        String expectedMessage = "Expecting PMMLRuntimeContext, received " + runtimeContext.getClass();
        assertThat(thrown.getMessage()).isEqualTo(expectedMessage);
    }
}