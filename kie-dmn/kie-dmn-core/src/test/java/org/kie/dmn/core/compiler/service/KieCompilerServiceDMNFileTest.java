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
package org.kie.dmn.core.compiler.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.core.compiler.model.DmnCompilationContext;
import org.kie.dmn.core.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.efesto.compilationmanager.core.utils.CompilationManagerUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

public class KieCompilerServiceDMNFileTest {

    private static KieCompilerService kieCompilationService;

    @BeforeAll
    public static void setUp() {
        kieCompilationService = new KieCompilationServiceDMNFile();
    }

    @Test
    void canManageResource() throws IOException {
        String fileName = "0001-input-data-string.dmn";
        File dmnFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get dmn file"));
        EfestoFileResource toProcess = new EfestoFileResource(dmnFile);
        assertThat(kieCompilationService.canManageResource(toProcess)).isTrue();
        EfestoInputStreamResource notToProcess = new EfestoInputStreamResource(Files.newInputStream(dmnFile.toPath()), fileName);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

    @Test
    void processResource() {
        String modelName = "_0001-input-data-string";
        String fileName = "0001-input-data-string";
        String fullFileName = String.format("%s.dmn", fileName);
        File dmnFile = getFileFromFileName(fullFileName).orElseThrow(() -> new RuntimeException("Failed to get dmn file"));
        EfestoFileResource toProcess = new EfestoFileResource(dmnFile);
        List<EfestoCompilationOutput> retrieved = kieCompilationService.processResource(toProcess, DmnCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader()));
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        EfestoCompilationOutput retrievedOutput = retrieved.get(0);
        assertNotNull(retrievedOutput);
        assertThat(retrievedOutput).isExactlyInstanceOf(EfestoCallableOutputDMN.class);
        EfestoCallableOutputDMN callableOutput = (EfestoCallableOutputDMN) retrievedOutput;
        ModelLocalUriId modelLocalUriId = callableOutput.getModelLocalUriId();
        assertThat(modelLocalUriId).isExactlyInstanceOf(LocalComponentIdDmn.class);
        LocalComponentIdDmn localComponentIdDmn = (LocalComponentIdDmn) modelLocalUriId;
        assertThat(localComponentIdDmn.getFileName()).isEqualTo(fileName);
        assertThat(localComponentIdDmn.getName()).isEqualTo(modelName);
    }

    @Test
    void processResourcesWithoutRedirect() {
        String modelName = "0001-input-data-string";
        String fileName = String.format("%s.dmn", modelName);
        File dmnFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get dmn file"));
        EfestoFileResource toProcess = new EfestoFileResource(dmnFile);
        EfestoCompilationContext context =
                EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        CompilationManagerUtils.processResourceWithContext(toProcess, context);
        context.createIndexFiles(Path.of("/Users/gcardosi/tmp"));
        // TODO @gcardosi

    }

}