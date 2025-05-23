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

import org.kie.dmn.core.pmml.EfestoPMMLUtils;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContextImpl;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;


@SuppressWarnings("rawtypes")
public abstract class AbstractKieCompilerServiceDMNTest {

    private static final String VALID_MODELS = "valid_models";
    private static final String DMN_1 = "/DMNv1_x";
    private static final String VALID_MODELS_DMN1 = String.format("%s%s", VALID_MODELS, DMN_1);
    private static final String VALID_MODELS_DMN1_PMML = String.format("%s%s/pmml", VALID_MODELS, DMN_1);

    protected static final String DMN_MODEL_NAME = "loan";
    protected static final String DMN_FILE_NAME = "loan";
    protected static final String DMN_NAMESPACE = "https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED";
    protected static final String DMN_FULL_FILE_NAME = String.format("%s.dmn", DMN_FILE_NAME);
    protected static final String DMN_FULL_PATH_FILE_NAME = String.format("%s/%s", VALID_MODELS_DMN1,  DMN_FULL_FILE_NAME);
    protected static final String DMN_FULL_PATH_FILE_NAME_NO_SUFFIX = String.format("%s/%s", VALID_MODELS_DMN1, DMN_FILE_NAME);
    protected static MemoryFile dmnFile;

    protected static final String DMN_PMML_MODEL_NAME = "TestRegressionDMN";
    protected static final String DMN_PMML_FILE_NAME = "KiePMMLRegression";

    protected static final String DMN_PMML_NAMESPACE =  "https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456";
    protected static final String DMN_PMML_FULL_FILE_NAME = String.format("%s.dmn", DMN_PMML_FILE_NAME);
    protected static final String DMN_PMML_FULL_PATH_FILE_NAME = String.format("%s/%s", VALID_MODELS_DMN1_PMML, DMN_PMML_FULL_FILE_NAME);
    protected static final String DMN_PMML_FULL_PATH_FILE_NAME_NO_SUFFIX = String.format("%s/%s",VALID_MODELS_DMN1_PMML, DMN_PMML_FILE_NAME);
    protected static MemoryFile dmnPmmlFile;

    protected static final String PMML_MODEL_NAME =  "TestRegression";
    protected static final String PMML_FILE_NAME = "test_regression";
    protected static final String PMML_FULL_FILE_NAME = String.format("%s.pmml", PMML_FILE_NAME);
    protected static final String PMML_FULL_PATH_FILE_NAME = String.format("%s/%s", VALID_MODELS_DMN1_PMML, PMML_FULL_FILE_NAME);
    protected static MemoryFile pmmlFile;

    protected static KieCompilerService kieCompilationService;
    protected static DmnCompilationContext dmnCompilationContext;
    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new EfestoCompilationManagerException("Failed to find an instance of CompilationManager: please check classpath and dependencies"));



    protected static void commonSetUp() {
        dmnFile = getMemoryFile(DMN_FULL_PATH_FILE_NAME);
        pmmlFile = getMemoryFile(PMML_FULL_PATH_FILE_NAME);
        dmnPmmlFile = getMemoryFile(DMN_PMML_FULL_PATH_FILE_NAME);
        EfestoCompilationContext pmmlCompilationContext = EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());

        InputStream is = new ByteArrayInputStream(pmmlFile.getContent());
        EfestoInputStreamResource pmmlFileResource = new EfestoInputStreamResource(is, PMML_FULL_PATH_FILE_NAME);
        compilationManager.processResource(pmmlCompilationContext, pmmlFileResource);
        ContextStorage.putEfestoCompilationContext(getPmmlModelLocalUriId(), pmmlCompilationContext);
        ContextStorage.putEfestoCompilationSource(getPmmlModelLocalUriId(), new String( pmmlFile.getContent(), StandardCharsets.UTF_8));
        dmnCompilationContext = (DmnCompilationContext) EfestoCompilationContextUtils.buildFromContext((EfestoCompilationContextImpl) pmmlCompilationContext, DmnCompilationContextImpl.class);
    }

    private static MemoryFile getMemoryFile(String fullFilePath) {
        return org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName(fullFilePath)
                .map(file -> {
                    try {
                        return new MemoryFile(file.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException(String.format("Failed to get %s file", fullFilePath)));
    }

    private static ModelLocalUriId getPmmlModelLocalUriId() {
        return EfestoPMMLUtils.getPmmlModelLocalUriId(PMML_FULL_FILE_NAME, PMML_MODEL_NAME);
    }

}