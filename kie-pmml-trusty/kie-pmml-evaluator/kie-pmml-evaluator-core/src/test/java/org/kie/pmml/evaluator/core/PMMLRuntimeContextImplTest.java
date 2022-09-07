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
package org.kie.pmml.evaluator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PMMLRuntimeContextImplTest {

    private static final String fileName = "LinearRegressionSample.pmml";

    private KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;


    @BeforeEach
    public void init() {
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void getName() {
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileName, memoryCompilerClassLoader);
        assertThat(retrieved.getName()).startsWith("Context_");
    }

    @Test
    void getFileName() {
    }

    @Test
    void getFileNameNoSuffix() {
    }

    @Test
    void getRequestData() {
    }
}