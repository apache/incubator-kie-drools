package org.kie.efesto.compilationmanager.core.service;/*
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
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.compilationmanager.api.model.EfestoRedirectOutput;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputA;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputB;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputC;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputD;
import org.kie.memorycompiler.KieMemoryCompiler;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TestCompilationManagerImpl {

    private static CompilationManager compilationManager;
    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static final List<Class<? extends EfestoRedirectOutput>> MANAGED_Efesto_RESOURCES = Arrays.asList(MockEfestoRedirectOutputA.class, MockEfestoRedirectOutputB.class, MockEfestoRedirectOutputC.class);


    @BeforeAll
    static void setUp() {
        compilationManager = new CompilationManagerImpl();
        memoryCompilerClassLoader = new KieMemoryCompiler.MemoryCompilerClassLoader(CompilationManager.class.getClassLoader());
    }

    @Test
    void processResource() {
        MANAGED_Efesto_RESOURCES.forEach(managedResource -> {
            try {
                EfestoRedirectOutput toProcess = managedResource.getDeclaredConstructor().newInstance();
                List<IndexFile> retrieved = compilationManager.processResource(toProcess, memoryCompilerClassLoader);
                assertEquals(1, retrieved.size());
                retrieved.get(0).delete();
            } catch (Exception e) {
                fail(e);
            }
        });
        List<IndexFile> retrieved = compilationManager.processResource(new MockEfestoRedirectOutputD(), memoryCompilerClassLoader);
        assertThat(retrieved.isEmpty()).isTrue();
    }

    // TODO restore
//    @Test
//    void processResources() {
//        List<EfestoRedirectOutput> toProcess = new ArrayList<>();
//        MANAGED_Efesto_RESOURCES.forEach(managedResource -> {
//            try {
//                EfestoRedirectOutput toAdd = managedResource.getDeclaredConstructor().newInstance();
//                toProcess.add(toAdd);
//            } catch (Exception e) {
//                fail(e);
//            }
//        });
//        toProcess.add(new MockEfestoRedirectOutputD());
//        List<EfestoCompilationOutput> retrieved = compilationManager.processResources(toProcess, memoryCompilerClassLoader);
//        assertNotNull(retrieved);
//        assertEquals(MANAGED_Efesto_RESOURCES.size(), retrieved.size());
//    }
}