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
package org.kie.efesto.compilationmanager.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.core.mocks.AbstractMockOutput;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputA;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputB;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputC;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputD;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class TestCompilationManagerImpl {

    private static CompilationManager compilationManager;
    private static EfestoCompilationContext context;

    private static final List<Class<? extends AbstractMockOutput>> MANAGED_Efesto_RESOURCES = Arrays.asList(MockEfestoRedirectOutputA.class, MockEfestoRedirectOutputB.class, MockEfestoRedirectOutputC.class);


    @BeforeAll
    static void setUp() {
        compilationManager = new CompilationManagerImpl();
        context = EfestoCompilationContext.buildWithParentClassLoader(CompilationManager.class.getClassLoader());
    }

    @Test
    void processResource() {
        MANAGED_Efesto_RESOURCES.forEach(managedResource -> {
            try {
                AbstractMockOutput toProcess = managedResource.getDeclaredConstructor().newInstance();
                Collection<IndexFile> retrieved = compilationManager.processResource(context,
                                                                                     toProcess);
                assertEquals(1, retrieved.size());
                retrieved.clear();
            } catch (Exception e) {
                fail(e);
            }
        });
        Collection<IndexFile> retrieved = compilationManager.processResource(context,
                                                                             new MockEfestoRedirectOutputD());
        assertThat(retrieved.isEmpty()).isTrue();
    }

    @Test
    void processResources() {
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
        Collection<IndexFile> retrieved = compilationManager.processResource(context,
                                                                             toProcess.toArray(new EfestoResource[0]));
        assertNotNull(retrieved);
    }
}