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

import java.io.File;
import java.util.Optional;

import org.drools.util.FileUtils;
import org.drools.util.IoUtils;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

public abstract class AbstractKieCompilerServiceDMNTest {

    protected static final String modelName = "_0001-input-data-string";
    protected static final String dmnFileName = "0001-input-data-string";
    protected static final String fileName = String.format("%s.dmn", dmnFileName);
    protected static KieCompilerService kieCompilationService;
    protected static File dmnFile;

    protected static void commonSetUp() {
        dmnFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get dmn file"));
        try {
            Optional<File> newFIle = org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName("valid_models/DMNv1_x/loan.dmn");
            assertTrue(newFIle.isPresent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}