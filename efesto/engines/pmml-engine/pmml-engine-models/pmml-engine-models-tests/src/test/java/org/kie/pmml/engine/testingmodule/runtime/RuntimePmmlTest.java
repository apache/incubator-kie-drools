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
package org.kie.pmml.engine.testingmodule.runtime;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

class RuntimePmmlTest {

    private static RuntimeManager runtimeManager;
    private static CompilationManager compilationManager;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static final String basePath = "TestingRule";

    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
        compilationManager = new CompilationManagerImpl();
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    @SuppressWarnings("raw")
    void evaluateWithKieSessionLocalCompilationOnTheFly() throws IOException {
//        String onTheFlyPath = "OnTheFlyPath";
//        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(new FRI(onTheFlyPath, "drl"), "");
//        Optional<EfestoOutput> darOutput = runtimeManager.evaluateInput(toEvaluate, memoryCompilerClassLoader);
//        assertThat(darOutput).isNotNull().isNotPresent();
//        Set<File> files = Files.walk(Paths.get("src/test/resources"))
//                .map(Path::toFile)
//                .filter(File::isFile)
//                .collect(Collectors.toSet());
//        EfestoResource<Set<File>> toProcess = new DrlFileSetResource(files, onTheFlyPath);
//        compilationManager.processResource(toProcess, memoryCompilerClassLoader);
//        darOutput = runtimeManager.evaluateInput(toEvaluate, memoryCompilerClassLoader);
//        assertThat(darOutput).isNotNull().isPresent();
//        assertThat(darOutput.get()).isInstanceOf(EfestoOutputDrlKieSessionLocal.class);
//        EfestoOutputDrlKieSessionLocal retrieved = (EfestoOutputDrlKieSessionLocal) darOutput.get();
//        assertThat(retrieved.getOutputData()).isNotNull().isInstanceOf(KieSession.class);
    }

}
