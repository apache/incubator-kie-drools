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

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.models.PMMLModel;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.PMMLRuntimeContextImpl;
import org.kie.pmml.evaluator.core.model.EfestoInputPMML;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;

class PMMLRuntimeInternalImplTest {

    private static final String fileName = "FileName.pmml";

    private static final String modelName = "TestMod";
    private static PMMLRuntimeContext pmmlRuntimeContext;

    private static PMMLRuntimeInternalImpl pmmlRuntimeInternal;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    @BeforeAll
    public static void setup() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        pmmlRuntimeContext = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileName, memoryCompilerClassLoader);
        pmmlRuntimeInternal = new PMMLRuntimeInternalImpl();
    }

    @Test
    void getPMMLModels() {
        List<PMMLModel> retrieved = pmmlRuntimeInternal.getPMMLModels(pmmlRuntimeContext);
        assertThat(retrieved).isNotNull().hasSize(1);
        PMMLModel pmmlModel = retrieved.get(0);
        assertThat(pmmlModel.getFileName()).isEqualTo(fileName);
        assertThat(pmmlModel.getName()).isEqualTo(modelName);
    }

    @Test
    void getPMMLModelPresent() {
        Optional<PMMLModel> retrieved = pmmlRuntimeInternal.getPMMLModel(fileName, modelName, pmmlRuntimeContext);
        assertThat(retrieved).isNotNull().isPresent();
        PMMLModel pmmlModel = retrieved.get();
        assertThat(pmmlModel.getFileName()).isEqualTo(fileName);
        assertThat(pmmlModel.getName()).isEqualTo(modelName);
    }

    @Test
    void getPMMLModelNotPresent() {
        Optional<PMMLModel> retrieved = pmmlRuntimeInternal.getPMMLModel(fileName, "notPresentModel",
                                                                         pmmlRuntimeContext);
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    @Test
    void getEfestoInputPMML() {
        String modelName = "modelName";
        EfestoInputPMML retrieved = PMMLRuntimeInternalImpl.getEfestoInputPMML(modelName, pmmlRuntimeContext);
        assertThat(retrieved).isNotNull();
        LocalComponentIdPmml expected = new LocalComponentIdPmml(pmmlRuntimeContext.getFileNameNoSuffix(),
                                                                 getSanitizedClassName(modelName));
        assertThat(retrieved.getModelLocalUriId()).isEqualTo(expected);
        assertThat(retrieved.getInputData()).isEqualTo(pmmlRuntimeContext);
    }
}