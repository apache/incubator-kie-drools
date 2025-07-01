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

import java.util.Set;
import org.drools.io.ByteArrayResource;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeAll;
import org.kie.api.io.Resource;
import org.kie.dmn.efesto.compiler.model.DMNResourceSetResource;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

class KieCompilerServiceDMNResourceTest extends AbstractKieCompilerServiceDMNTest {

    @BeforeAll
    static void setUp() {
        kieCompilationService = new KieCompilerServiceDMNResourceSet();
        commonSetUp();
        Resource dmnResource = new FileSystemResource(dmnFile);
        Resource dmnPmmlResources = new ByteArrayResource(dmnPmmlFile.getContent());
        Resource dmnInvalidResource = new FileSystemResource(dmnInvalidFile);
        dmnPmmlResources.setSourcePath(DMN_PMML_FULL_PATH_FILE_NAME_NO_SUFFIX);
        ModelLocalUriId dmnModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append(DMN_MODEL_NAME));
        toProcessDmn = new DMNResourceSetResource(Set.of(dmnResource), dmnModelLocalUriId);
        ModelLocalUriId dmnPmmlModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append(DMN_PMML_MODEL_NAME));
        toProcessDmnPmml = new DMNResourceSetResource(Set.of(dmnPmmlResources), dmnPmmlModelLocalUriId);
        ModelLocalUriId dmnInvalidModelLocalUriId = new ModelLocalUriId(LocalUri.Root.append("dmn").append(DMN_INVALID_MODEL_NAME));
        toProcessInvalidDmn = new DMNResourceSetResource(Set.of(dmnInvalidResource), dmnInvalidModelLocalUriId);
    }
}