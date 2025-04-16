/*
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
package org.kie.dmn.efesto.compiler.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
class KieCompilerServiceDMNInputStreamTest extends AbstractKieCompilerServiceDMNTest {

    private static EfestoInputStreamResource toProcess;

    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNInputStream();
        commonSetUp();
    }

    @BeforeEach
    void init() {
        InputStream is = new ByteArrayInputStream(dmnFile.getContent());
        toProcess = new EfestoInputStreamResource(is, DMN_FULL_FILE_NAME);
    }

    @Test
    void canManageResource() {
        assertThat(kieCompilationService.canManageResource(toProcess)).isTrue();
        EfestoFileResource notToProcess = new EfestoFileResource(dmnFile);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    void processResource() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcess,
                                                                                        DmnCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader()));
        assertThat(retrieved).isNotNull().hasSize(1);
        EfestoCompilationOutput retrievedOutput = retrieved.get(0);
        assertThat(retrievedOutput).isNotNull().isExactlyInstanceOf(EfestoCallableOutputDMN.class);
        EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
        ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
        assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
        LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
        assertThat(localComponentIdDmn.getNameSpace()).isEqualTo(DMN_NAMESPACE);
        assertThat(localComponentIdDmn.getName()).isEqualTo(DMN_MODEL_NAME);
    }

    @Test
    void hasCompilationSource() {
        kieCompilationService.processResource(toProcess,
                                              DmnCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader()));
        assertThat(kieCompilationService.hasCompilationSource(DMN_FULL_FILE_NAME)).isTrue();
    }

    @Test
    void getCompilationSource() {
        kieCompilationService.processResource(toProcess,
                                              DmnCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader()));
        String retrieved = kieCompilationService.getCompilationSource(DMN_FULL_FILE_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }


}