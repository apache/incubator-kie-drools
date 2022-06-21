package org.kie.drl.engine.runtime.kiesession.local.service;/*
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KieRuntimeServiceDrlKieSessionLocalTest {

    private static final String basePath = "TestingRule";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private static KieRuntimeServiceDrlKieSessionLocal kieRuntimeServiceDrlKieSessionLocal;

    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        kieRuntimeServiceDrlKieSessionLocal = new KieRuntimeServiceDrlKieSessionLocal();
    }

    @Test
    void canManageInput() {
        FRI fri = new FRI(basePath, "drl");
        EfestoInputDrlKieSessionLocal darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(fri, "");
        assertThat(kieRuntimeServiceDrlKieSessionLocal.canManageInput(darInputDrlKieSessionLocal, memoryCompilerClassLoader)).isTrue();
        fri = new FRI("notexisting", "drl");
        darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(fri, "");
        assertThat(kieRuntimeServiceDrlKieSessionLocal.canManageInput(darInputDrlKieSessionLocal, memoryCompilerClassLoader)).isFalse();
    }

    @Test
    void evaluateInput() {
        EfestoInputDrlKieSessionLocal darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(new FRI(basePath, "drl"), "");
        Optional<EfestoOutputDrlKieSessionLocal> retrieved = kieRuntimeServiceDrlKieSessionLocal.evaluateInput(darInputDrlKieSessionLocal, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get().getOutputData()).isNotNull().isInstanceOf(KieSession.class);
        darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(new FRI("notexisting", "drl"), "");
        retrieved = kieRuntimeServiceDrlKieSessionLocal.evaluateInput(darInputDrlKieSessionLocal, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull().isNotPresent();
    }
}