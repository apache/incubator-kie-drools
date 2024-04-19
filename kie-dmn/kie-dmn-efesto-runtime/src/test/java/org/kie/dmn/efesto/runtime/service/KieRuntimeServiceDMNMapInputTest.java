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
package org.kie.dmn.efesto.runtime.service;


import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.efesto.api.identifiers.DmnIdFactory;
import org.kie.dmn.efesto.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.efesto.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.runtime.model.EfestoOutputDMN;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KieRuntimeServiceDMNMapInputTest {

    private static final String MODEL_NAME = "_0001-input-data-string";
    private static final String FILE_NAME = "0001-input-data-string";
    private static KieRuntimeServiceDMNMapInput kieRuntimeServiceDMNMapInput;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private ModelLocalUriId modelLocalUriId;

    private EfestoInput<Map<String, Object>> inputDMN;
    private EfestoRuntimeContext efestoRuntimeContext;

    @BeforeClass
    public static void setup() {
        kieRuntimeServiceDMNMapInput = new KieRuntimeServiceDMNMapInput();
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void canManageManageableInput() {
        modelLocalUriId = getModelLocalUriIdFromDmnIdFactory(FILE_NAME, MODEL_NAME);
        Map<String, Object> inputData = new HashMap<>();
        inputDMN = new BaseEfestoInput<>(modelLocalUriId, inputData);
        assertThat(kieRuntimeServiceDMNMapInput.canManageInput(inputDMN,
                getEfestoContext(memoryCompilerClassLoader))).isTrue();
    }

    @Test
    public void evaluateCorrectInput() {
        modelLocalUriId = getModelLocalUriIdFromDmnIdFactory(FILE_NAME, MODEL_NAME);
        Map<String, Object> inputData = Map.of("Full Name", "John Doe");
        inputDMN = new BaseEfestoInput<>(modelLocalUriId, inputData);
        efestoRuntimeContext = getEfestoContext(memoryCompilerClassLoader);
        Optional<EfestoOutputDMN> retrieved = kieRuntimeServiceDMNMapInput.evaluateInput(inputDMN,
                efestoRuntimeContext);
        assertThat(retrieved).isNotNull().isPresent();
        DMNResult dmnResult = retrieved.get().getOutputData();
        Assertions.assertThat( dmnResult.getDecisionResults()).hasSize(1);
        Assertions.assertThat( dmnResult.getDecisionResultByName( "Greeting Message" ).getResult()).isEqualTo("Hello John Doe" );
    }


    static LocalComponentIdDmn getModelLocalUriIdFromDmnIdFactory(String fileName, String modelName) {
        return new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName, modelName);
    }

    static EfestoRuntimeContext getEfestoContext(ClassLoader parenClassLoader) {
        return EfestoRuntimeContextUtils.buildWithParentClassLoader(parenClassLoader);
    }
}