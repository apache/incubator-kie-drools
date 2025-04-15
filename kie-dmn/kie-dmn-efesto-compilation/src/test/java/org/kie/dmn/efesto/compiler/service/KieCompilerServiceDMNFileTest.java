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
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("unchecked")
class KieCompilerServiceDMNFileTest extends AbstractKieCompilerServiceDMNTest {

    private static EfestoFileResource toProcessDmn;
    private static EfestoFileResource toProcessDmnPmml;

    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNFile();
        commonSetUp();
        toProcessDmn = new EfestoFileResource(dmnFile);
        toProcessDmnPmml = new EfestoFileResource(dmnPmmlFile);
    }

    @Test
    void  canManageResourceDmn() {
        assertThat(kieCompilationService.canManageResource(toProcessDmn)).isTrue();
        InputStream is = new ByteArrayInputStream(dmnFile.getContent());
        EfestoInputStreamResource notToProcess = new EfestoInputStreamResource(is, DMN_FULL_PATH_FILE_NAME);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    void  canManageResourceDmnPmml() {
        assertThat(kieCompilationService.canManageResource(toProcessDmnPmml)).isTrue();
        InputStream is = new ByteArrayInputStream( dmnPmmlFile.getContent());
        EfestoInputStreamResource notToProcess = new EfestoInputStreamResource(is, DMN_PMML_FULL_PATH_FILE_NAME);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    void  processResourceDmn() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcessDmn,
                                                                                        dmnCompilationContext);
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
    void  processResourceDmnPmml() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcessDmnPmml,
                                                                                        dmnCompilationContext);
        assertThat(retrieved).isNotNull().hasSize(1);
        EfestoCompilationOutput retrievedOutput = retrieved.get(0);
        assertThat(retrievedOutput).isNotNull().isExactlyInstanceOf(EfestoCallableOutputDMN.class);
        EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
        ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
        assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
        LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
        assertThat(localComponentIdDmn.getNameSpace()).isEqualTo(DMN_PMML_NAMESPACE);
        assertThat(localComponentIdDmn.getName()).isEqualTo(DMN_PMML_MODEL_NAME);
    }

    @Test
    void  hasCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        assertThat(kieCompilationService.hasCompilationSource(DMN_FULL_PATH_FILE_NAME)).isTrue();
    }

    @Test
    void hasCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        assertThat(kieCompilationService.hasCompilationSource(DMN_PMML_FULL_PATH_FILE_NAME)).isTrue();
    }

    @Test
    void getCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_FULL_PATH_FILE_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_PMML_FULL_PATH_FILE_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnPmmlFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

}