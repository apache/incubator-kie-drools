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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.compilation.model.DrlFileSetResource;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.testingmodule.utils.DrlTestUtils;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.JarUtils;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;

class StoredClassesTest {

    @BeforeAll
    static void setUp() {
        DrlTestUtils.refreshDrlIndexFile();
    }

    @Test
    void simulateReboot() throws IOException {

        String basePath = "ApplicationA";

        // Phase 1
        RuntimeManager runtimeManager1 = new RuntimeManagerImpl();
        CompilationManager compilationManager1 = new CompilationManagerImpl();
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader1 = new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());

        Set<File> files = DrlTestUtils.collectDrlFiles("src/test/resources/org/drools/model/project/codegen");
        EfestoResource<Set<File>> toProcess1 = new DrlFileSetResource(files, basePath);
        Collection<IndexFile> indexFiles = compilationManager1.processResource(memoryCompilerClassLoader1, toProcess1);
        
        // Persist generated classes as a jar per index file (= model)
        JarUtils.createJarFiles(indexFiles, memoryCompilerClassLoader1);

        EfestoInputDrlKieSessionLocal toEvaluate1 = new EfestoInputDrlKieSessionLocal(new FRI(basePath, "drl"), "");
        Collection<EfestoOutput> output1 = runtimeManager1.evaluateInput(memoryCompilerClassLoader1, toEvaluate1);
        assertThat(output1).isNotNull().hasSize(1);

        KieSession session1 = (KieSession) output1.iterator().next().getOutputData();
        session1.insert("test");
        assertThat(session1.fireAllRules()).isEqualTo(3);
        session1.dispose();

        // --- Simulating reboot ---

        // Phase 2
        RuntimeManager runtimeManager2 = new RuntimeManagerImpl();
        ClassLoader parentClassLoader = JarUtils.createStoredJarClassLoader("drl");
        KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader2 = new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader);

        EfestoInputDrlKieSessionLocal toEvaluate2 = new EfestoInputDrlKieSessionLocal(new FRI(basePath, "drl"), "");
        Collection<EfestoOutput> output2 = runtimeManager2.evaluateInput(memoryCompilerClassLoader2, toEvaluate2);
        assertThat(output2).isNotNull().hasSize(1);

        KieSession session2 = (KieSession) output2.iterator().next().getOutputData();
        session2.insert("test");
        assertThat(session2.fireAllRules()).isEqualTo(3);
        session2.dispose();
    }
}
