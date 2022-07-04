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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;

import static org.assertj.core.api.Assertions.assertThat;

public class PMMLRuntimeInternalImplTest {

    private static PMMLRuntime pmmlRuntime;

    @BeforeAll
    public static void init() {
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        pmmlRuntime = new PMMLRuntimeInternalImpl(memoryCompilerClassLoader);
    }

    @Test
    void getPMMLModels() {
        List<PMMLModel> retrieved = pmmlRuntime.getPMMLModels();
        assertThat(retrieved).isNotNull().hasSize(1); // defined in IndexFile.pmml_json
        assertThat(retrieved.get(0)).isInstanceOf(KiePMMLTestingModel.class);
    }

    @Test
    void getPMMLModel() {
        Optional<PMMLModel> retrieved = pmmlRuntime.getPMMLModel("FileName.pmml", "TestMod"); // defined in IndexFile.pmml_json
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(KiePMMLTestingModel.class);
        retrieved = pmmlRuntime.getPMMLModel("FileName", "TestMod"); // not defined in IndexFile.pmml_json
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get()).isInstanceOf(KiePMMLTestingModel.class);
        retrieved = pmmlRuntime.getPMMLModel("FileNameDiff", "TestMod"); // not defined in IndexFile.pmml_json
        assertThat(retrieved).isNotNull().isNotPresent();
        retrieved = pmmlRuntime.getPMMLModel("FileName", "NotTestMod"); // not defined in IndexFile.pmml_json
        assertThat(retrieved).isNotNull().isNotPresent();
    }

}