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
package org.kie.efesto.compilationmanager.core.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.mocks.AbstractMockOutput;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoCallableOutput;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoCallableOutputE;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputA;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputB;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputC;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputD;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputE;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.efesto.common.core.utils.JSONUtils.getGeneratedResourcesObject;

class TestCompilationManagerImpl {

    private static CompilationManager compilationManager;

    private static final List<Class<? extends AbstractMockOutput>> MANAGED_Efesto_RESOURCES = Arrays.asList(MockEfestoRedirectOutputA.class, MockEfestoRedirectOutputB.class, MockEfestoRedirectOutputC.class);

    private static final Path TARGET_TEST_CLASSES_DIRECTORY = Paths.get("target/test-classes/");

    @BeforeAll
    static void setUp() {
        compilationManager = new CompilationManagerImpl();
    }

    @Test
    void processResource() {
        EfestoCompilationContext context = EfestoCompilationContextUtils.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
        MANAGED_Efesto_RESOURCES.forEach(managedResource -> {
            IndexFile indexFile = null;
            try {
                AbstractMockOutput toProcess = managedResource.getDeclaredConstructor().newInstance();
                compilationManager.processResource(context, toProcess);
                assertThat(context.getGeneratedResourcesMap()).hasSize(1);

                // This test repeatedly overwrites context.generatedResourcesMap
                Map<String, IndexFile> indexFiles = context.createIndexFiles(TARGET_TEST_CLASSES_DIRECTORY);
                assertThat(indexFiles).hasSize(1);
                indexFile = indexFiles.get("mock");
                assertThat(indexFile).exists();
            } catch (Exception e) {
                fail(e);
            } finally {
                if (indexFile != null) {
                    indexFile.delete();
                }
            }
        });

        EfestoCompilationContext newContext = EfestoCompilationContextUtils.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
        compilationManager.processResource(newContext, new MockEfestoRedirectOutputD());
        assertThat(newContext.getGeneratedResourcesMap()).isEmpty();
        Map<String, IndexFile> indexFiles = newContext.createIndexFiles(TARGET_TEST_CLASSES_DIRECTORY);
        assertThat(indexFiles).isEmpty();
    }

    @Test
    void processResources() {
        EfestoCompilationContext context = EfestoCompilationContextUtils.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
        List<AbstractMockOutput> toProcess = new ArrayList<>();
        MANAGED_Efesto_RESOURCES.forEach(managedResource -> {
            try {
                AbstractMockOutput toAdd = managedResource.getDeclaredConstructor().newInstance();
                toProcess.add(toAdd);
            } catch (Exception e) {
                fail(e);
            }
        });
        toProcess.add(new MockEfestoRedirectOutputD());
        compilationManager.processResource(context, toProcess.toArray(new EfestoResource[0]));
        assertThat(context.getGeneratedResourcesMap()).hasSize(1);

        IndexFile indexFile = null;
        try {
            Map<String, IndexFile> indexFiles = context.createIndexFiles(TARGET_TEST_CLASSES_DIRECTORY);
            assertThat(indexFiles).hasSize(1);
            indexFile = indexFiles.get("mock");
            assertThat(indexFile).exists();
        } finally {
            if (indexFile != null) {
                indexFile.delete();
            }
        }
    }

    @Test
    void overwritingIndexFile() {
        IndexFile indexFile = null;

        // 1st round
        try {
            EfestoCompilationContext context = EfestoCompilationContextUtils.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
            List<AbstractMockOutput> toProcess = new ArrayList<>();
            toProcess.add(new MockEfestoRedirectOutputA());
            compilationManager.processResource(context, toProcess.toArray(new EfestoResource[0]));
            assertThat(context.getGeneratedResourcesMap()).hasSize(1);

            Map<String, IndexFile> indexFiles = context.createIndexFiles(TARGET_TEST_CLASSES_DIRECTORY);
            assertThat(indexFiles).hasSize(1);
            indexFile = indexFiles.get("mock");
            assertThat(indexFile).exists();
        } catch (Exception e) {
            fail("failed during 1st round", e);
        }

        // 2nd round
        try {
            EfestoCompilationContext context = EfestoCompilationContextUtils.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
            List<AbstractMockOutput> toProcess = new ArrayList<>();
            toProcess.add(new MockEfestoRedirectOutputE());
            compilationManager.processResource(context, toProcess.toArray(new EfestoResource[0]));
            assertThat(context.getGeneratedResourcesMap()).hasSize(1);

            Map<String, IndexFile> indexFiles = context.createIndexFiles(TARGET_TEST_CLASSES_DIRECTORY);
            assertThat(indexFiles).hasSize(1);
            indexFile = indexFiles.get("mock");
            assertThat(indexFile).exists();

            GeneratedResources finalContent = getGeneratedResourcesObject(indexFile);
            assertThat(finalContent).hasSize(2);

            List<ModelLocalUriId> friList = Arrays.asList(new MockEfestoCallableOutput().getModelLocalUriId(), new MockEfestoCallableOutputE().getModelLocalUriId());

            List<GeneratedExecutableResource> resourceList = finalContent.stream().filter(GeneratedExecutableResource.class::isInstance)
                    .map(GeneratedExecutableResource.class::cast)
                    .filter(resource -> friList.contains(resource.getModelLocalUriId()))
                    .collect(Collectors.toList());

            // contains "/mock/mock/efesto/output/module" and "/mock/mock/efesto/output/moduleE"
            assertThat(resourceList).hasSize(2);
        } catch (Exception e) {
            fail("failed during 2nd round", e);
        } finally {
            if (indexFile != null) {
                indexFile.delete();
            }
        }
    }
}