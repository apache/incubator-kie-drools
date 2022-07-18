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
package org.kie.drl.engine.testingmodule.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeDrlTest {

    private static RuntimeManager runtimeManager;
    private static CompilationManager compilationManager;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static final String basePath = "TestingRule";

    @BeforeAll
    static void setUp() {
        DrlTestUtils.refreshDrlIndexFile();
        runtimeManager = new RuntimeManagerImpl();
        compilationManager = new CompilationManagerImpl();
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void evaluateWithKieSessionLocalStaticCompilation() {
        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(new FRI(basePath, "drl"), "");
        Collection<EfestoOutput> output = runtimeManager.evaluateInput(memoryCompilerClassLoader, toEvaluate);
        assertThat(output).isNotNull().hasSize(1);
        EfestoOutput<?> retrievedRaw = output.iterator().next();
        assertThat(retrievedRaw).isInstanceOf(EfestoOutputDrlKieSessionLocal.class);
        EfestoOutputDrlKieSessionLocal retrieved = (EfestoOutputDrlKieSessionLocal) retrievedRaw;
        assertThat(retrieved.getOutputData()).isNotNull().isInstanceOf(KieSession.class);
    }

    @Test
    void evaluateWithKieSessionLocalCompilationOnTheFly() throws IOException {
        String onTheFlyPath = "OnTheFlyPath";
        Set<File> files = DrlTestUtils.collectDrlFiles("src/test/resources/org/drools/model/project/codegen");
        EfestoResource<Set<File>> toProcess = new DrlFileSetResource(files, onTheFlyPath);
        Collection<IndexFile> indexFiles = compilationManager.processResource(memoryCompilerClassLoader, toProcess);

        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(new FRI(onTheFlyPath, "drl"), "");
        Collection<EfestoOutput> output = runtimeManager.evaluateInput(memoryCompilerClassLoader, toEvaluate);
        assertThat(output).isNotNull().hasSize(1);
        EfestoOutput retrievedRaw = output.iterator().next();
        assertThat(retrievedRaw).isInstanceOf(EfestoOutputDrlKieSessionLocal.class);
        EfestoOutputDrlKieSessionLocal retrieved = (EfestoOutputDrlKieSessionLocal) retrievedRaw;
        assertThat(retrieved.getOutputData()).isNotNull().isInstanceOf(KieSession.class);

        KieSession session = retrieved.getOutputData();
        session.insert("test");
        assertThat(session.fireAllRules()).isEqualTo(3);
    }
}
