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

import org.junit.jupiter.api.BeforeAll;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoStringResource;

class KieCompilerServiceDMNStringTest extends AbstractKieCompilerServiceDMNTest {


    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNString();
        commonSetUp();
        ModelLocalUriId toProcessDmnId = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(DMN_FULL_PATH_FILE_NAME_NO_SUFFIX, DMN_MODEL_NAME);
        toProcessDmn = new EfestoStringResource(new String(dmnFile.getContent()), toProcessDmnId);

        ModelLocalUriId toProcessInvalidDmnId = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(DMN_INVALID_FULL_PATH_FILE_NAME, DMN_INVALID_MODEL_NAME);
        toProcessInvalidDmn = new EfestoStringResource(new String(dmnInvalidFile.getContent()), toProcessInvalidDmnId);

        ModelLocalUriId toProcessDmnPmmlId = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(DMN_PMML_FULL_PATH_FILE_NAME, DMN_PMML_MODEL_NAME);
        toProcessDmnPmml = new EfestoStringResource(new String(dmnPmmlFile.getContent()), toProcessDmnPmmlId);
    }
}