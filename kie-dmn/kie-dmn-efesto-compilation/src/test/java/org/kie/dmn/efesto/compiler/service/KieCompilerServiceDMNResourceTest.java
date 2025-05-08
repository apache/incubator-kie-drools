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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.drools.io.ByteArrayResource;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.efesto.compiler.model.DMNResourceSetResource;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
class KieCompilerServiceDMNResourceTest extends AbstractKieCompilerServiceDMNTest {

    private static Resource toProcessDmn;
    private static Resource toProcessDmnPmml;
    private static DMNResourceSetResource toProcess;
    private static ModelLocalUriId dmnModelLocalUriId;

    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNResourceSet();
        commonSetUp();
        toProcessDmn = new FileSystemResource(dmnFile);
        toProcessDmnPmml = new ByteArrayResource(dmnPmmlFile.getContent());
        dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append(DMN_MODEL_NAME));
        toProcess = new DMNResourceSetResource(Set.of(toProcessDmn, toProcessDmnPmml), dmnModelLocalUriId);
    }

    @Test
    void canManageResourceSet() {
        assertThat(kieCompilationService.canManageResource(toProcess)).isTrue();
        EfestoFileSetResource notToProcess = new EfestoFileSetResource(Set.of(dmnFile, dmnPmmlFile), dmnModelLocalUriId);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }


    @Test
    void processResourceSet() {
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcess,
                                                                                        dmnCompilationContext);
        assertThat(retrieved).isNotNull().hasSize(2);
        retrieved.forEach(retrievedOutput -> {
            assertThat(retrievedOutput).isNotNull().isExactlyInstanceOf(EfestoCallableOutputDMN.class);
            EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
            ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
            assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
            LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
            String nameSpace = localComponentIdDmn.getNameSpace();
            String name = localComponentIdDmn.getName();
            assertThat((nameSpace.equals(DMN_NAMESPACE) && name.equals(DMN_MODEL_NAME)) ||
                               (nameSpace.equals(DMN_PMML_NAMESPACE) && name.equals(DMN_PMML_MODEL_NAME)))
                    .isTrue();
        });
    }

    @Test
    void hasCompilationSource() {
        kieCompilationService.processResource(toProcess,
                                              dmnCompilationContext);
        assertThat(kieCompilationService.hasCompilationSource(DMN_FULL_PATH_FILE_NAME)).isTrue();
        assertThat(kieCompilationService.hasCompilationSource(DMN_PMML_MODEL_NAME)).isTrue();
    }

    @Test
    void getCompilationSource() {
        kieCompilationService.processResource(toProcess,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_FULL_PATH_FILE_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
        retrieved = kieCompilationService.getCompilationSource(DMN_PMML_MODEL_NAME);
        assertThat(retrieved).isNotNull();
        expected = new String(dmnPmmlFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

}