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
package org.kie.dmn.efesto.compiler.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KieCompilerServiceDMNFileTest extends AbstractKieCompilerServiceDMNTest {

    private static EfestoFileResource toProcessDmn;
    private static EfestoFileResource toProcessDmnPmml;

    @BeforeAll
    public static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNFile();
        commonSetUp();
        toProcessDmn = new EfestoFileResource(dmnFile);
        toProcessDmnPmml = new EfestoFileResource(dmnPmmlFile);
    }

    @Test
    public void canManageResourceDmn() {
        assertThat(kieCompilationService.canManageResource(toProcessDmn)).isTrue();
        InputStream is = new ByteArrayInputStream(((MemoryFile) dmnFile).getContent());
        EfestoInputStreamResource notToProcess = new EfestoInputStreamResource(is, dmnFullPathFileName);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    public void canManageResourceDmnPmml() {
        assertThat(kieCompilationService.canManageResource(toProcessDmnPmml)).isTrue();
        InputStream is = new ByteArrayInputStream(((MemoryFile) dmnPmmlFile).getContent());
        EfestoInputStreamResource notToProcess = new EfestoInputStreamResource(is, dmnPmmlFullPathFileName);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    public void processResourceDmn() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcessDmn,
                                                                                        dmnCompilationContext);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        EfestoCompilationOutput retrievedOutput = retrieved.get(0);
        assertNotNull(retrievedOutput);
        assertThat(retrievedOutput).isExactlyInstanceOf(EfestoCallableOutputDMN.class);
        EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
        ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
        assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
        LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
        assertThat(localComponentIdDmn.getFileName()).isEqualTo(dmnFileName);
        assertThat(localComponentIdDmn.getName()).isEqualTo(dmnModelName);
    }

    @Test
    public void processResourceDmnPmml() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcessDmnPmml,
                                                                                        dmnCompilationContext);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        EfestoCompilationOutput retrievedOutput = retrieved.get(0);
        assertNotNull(retrievedOutput);
        assertThat(retrievedOutput).isExactlyInstanceOf(EfestoCallableOutputDMN.class);
        EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
        ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
        assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
        LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
        assertThat(localComponentIdDmn.getFileName()).isEqualTo(dmnPmmlFileName);
        assertThat(localComponentIdDmn.getName()).isEqualTo(dmnPmmlModelName);
    }

    @Test
    public void hasCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        assertTrue(kieCompilationService.hasCompilationSource(dmnFullPathFileName));
    }

    @Test
    public void hasCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        assertTrue(kieCompilationService.hasCompilationSource(dmnPmmlFullPathFileName));
    }

    @Test
    public void getCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(dmnFullPathFileName);
        assertNotNull(retrieved);
        String expected = new String(((MemoryFile) dmnFile).getContent(), StandardCharsets.UTF_8);
        assertEquals(expected, retrieved);
    }

    @Test
    public void getCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(dmnPmmlFullPathFileName);
        assertNotNull(retrieved);
        String expected = new String(((MemoryFile) dmnPmmlFile).getContent(), StandardCharsets.UTF_8);
        assertEquals(expected, retrieved);
    }

}