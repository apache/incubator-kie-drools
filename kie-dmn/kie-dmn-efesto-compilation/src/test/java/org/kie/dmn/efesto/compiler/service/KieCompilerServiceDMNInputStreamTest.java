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


import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

public class KieCompilerServiceDMNInputStreamTest {

    private static KieCompilationService kieCompilationService;

    @BeforeClass
    public static void setUp() {
        kieCompilationService = new KieCompilationServiceDMNInputStream();
    }

    @Test
    public void canManageResource() throws IOException {
        String fileName = "0001-input-data-string.dmn";
        File dmnFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get dmn file"));
        EfestoInputStreamResource toProcess = new EfestoInputStreamResource(Files.newInputStream(dmnFile.toPath()), fileName);
        assertThat(kieCompilationService.canManageResource(toProcess)).isTrue();
        EfestoFileResource notToProcess = new EfestoFileResource(dmnFile);
        assertThat(kieCompilationService.canManageResource(notToProcess)).isFalse();
    }

}