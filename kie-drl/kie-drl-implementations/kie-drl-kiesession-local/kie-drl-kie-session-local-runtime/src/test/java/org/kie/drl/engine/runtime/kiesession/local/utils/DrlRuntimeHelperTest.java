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
package org.kie.drl.engine.runtime.kiesession.local.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.model.EfestoInputDrl;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DrlRuntimeHelperTest {

    private static final String basePath = "TestingRule";
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManage() {
        FRI fri = new FRI(basePath, "drl");
        AbstractEfestoInput darInput = new EfestoInputDrlKieSessionLocal(fri, "");
        assertThat(DrlRuntimeHelper.canManage(darInput)).isTrue();
        fri = new FRI(basePath, "drl");
        darInput = new EfestoInputDrl(fri, "") {
        };
        assertThat(DrlRuntimeHelper.canManage(darInput)).isFalse();
        fri = new FRI("notexisting", "drl");
        darInput = new EfestoInputDrlKieSessionLocal(fri, "");
        assertThat(DrlRuntimeHelper.canManage(darInput)).isFalse();
    }

    @Test
    void execute() {
        EfestoInputDrlKieSessionLocal darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(new FRI(basePath, "drl"), "");
        Optional<EfestoOutputDrlKieSessionLocal> retrieved = DrlRuntimeHelper.execute(darInputDrlKieSessionLocal, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull().isPresent();
        assertThat(retrieved.get().getOutputData()).isNotNull().isInstanceOf(KieSession.class);
        darInputDrlKieSessionLocal = new EfestoInputDrlKieSessionLocal(new FRI("notexisting", "drl"), "");
        retrieved = DrlRuntimeHelper.execute(darInputDrlKieSessionLocal, memoryCompilerClassLoader);
        assertThat(retrieved).isNotNull().isNotPresent();
    }
}