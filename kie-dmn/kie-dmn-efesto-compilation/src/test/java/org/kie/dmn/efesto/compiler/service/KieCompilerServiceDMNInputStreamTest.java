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
import java.io.InputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;

class KieCompilerServiceDMNInputStreamTest extends AbstractKieCompilerServiceDMNTest {


    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNInputStream();
        commonSetUp();
        notToProcessDmn = new EfestoFileResource(dmnFile);
        notToProcessDmnPmml = new EfestoFileResource(dmnPmmlFile);
    }

    @BeforeEach
    void init() {
        InputStream toProcessDmnIs = new ByteArrayInputStream(dmnFile.getContent());
        toProcessDmn = new EfestoInputStreamResource(toProcessDmnIs,  DMN_FULL_PATH_FILE_NAME);

        InputStream toProcessDmnPmmlIs = new ByteArrayInputStream(dmnPmmlFile.getContent());
        toProcessDmnPmml = new EfestoInputStreamResource(toProcessDmnPmmlIs,  DMN_PMML_FULL_PATH_FILE_NAME);

        InputStream toProcessInvalidDmnIs = new ByteArrayInputStream(dmnInvalidFile.getContent());
        toProcessInvalidDmn = new EfestoInputStreamResource(toProcessInvalidDmnIs,  DMN_INVALID_FULL_PATH_FILE_NAME);
    }

}