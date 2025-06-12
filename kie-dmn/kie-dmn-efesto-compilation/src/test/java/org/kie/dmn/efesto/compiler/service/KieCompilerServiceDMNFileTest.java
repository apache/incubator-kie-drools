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
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;

class KieCompilerServiceDMNFileTest extends AbstractKieCompilerServiceDMNTest {


    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNFile();
        commonSetUp();
        toProcessDmn = new EfestoFileResource(dmnFile);
        toProcessInvalidDmn = new EfestoFileResource(dmnInvalidFile);
        toProcessDmnPmml = new EfestoFileResource(dmnPmmlFile);
        InputStream notToProcessDmnIs = new ByteArrayInputStream(dmnFile.getContent());
        notToProcessDmn = new EfestoInputStreamResource(notToProcessDmnIs, DMN_FULL_PATH_FILE_NAME);
        InputStream notToProcessDmnPmmlIs = new ByteArrayInputStream( dmnPmmlFile.getContent());
        notToProcessDmnPmml = new EfestoInputStreamResource(notToProcessDmnPmmlIs, DMN_PMML_FULL_PATH_FILE_NAME);
    }
}