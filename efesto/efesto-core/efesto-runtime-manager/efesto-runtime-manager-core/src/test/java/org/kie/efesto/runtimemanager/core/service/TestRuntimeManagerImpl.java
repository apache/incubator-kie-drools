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
package org.kie.efesto.runtimemanager.core.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputA;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputB;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputC;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputD;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class TestRuntimeManagerImpl {

    private static RuntimeManager runtimeManager;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static final List<Class<? extends EfestoInput>> MANAGED_Efesto_INPUTS = Arrays.asList(MockEfestoInputA.class,
            MockEfestoInputB.class,
            MockEfestoInputC.class);


    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void evaluateInput() {
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput toProcess = managedInput.getDeclaredConstructor().newInstance();
                Optional<EfestoOutput> retrieved = runtimeManager.evaluateInput(toProcess, memoryCompilerClassLoader);
                assertTrue(retrieved.isPresent());
            } catch (Exception e) {
                fail(e);
            }
        });
        Optional<EfestoOutput> retrieved = runtimeManager.evaluateInput(new MockEfestoInputD(), memoryCompilerClassLoader);
        assertThat(retrieved.isPresent()).isFalse();
    }

    @Test
    void evaluateInputs() {
        List<EfestoInput> toProcess = new ArrayList<>();
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput toAdd = managedInput.getDeclaredConstructor().newInstance();
                toProcess.add(toAdd);
            } catch (Exception e) {
                fail(e);
            }
        });
        toProcess.add(new MockEfestoInputD());
        List<EfestoOutput> retrieved = runtimeManager.evaluateInputs(toProcess, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.size()).isEqualTo(MANAGED_Efesto_INPUTS.size());
    }
}