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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.identifiers.LocalComponentIdDmn;
import org.kie.dmn.core.pmml.EfestoPMMLUtils;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContextImpl;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.dmn.validation.DMNValidator;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractKieCompilerServiceDMNTest {

    private static final String VALID_MODELS = "valid_models";
    private static final String DMN_1 = "/DMNv1_x";
    private static final String VALID_MODELS_DMN1 = String.format("%s%s", VALID_MODELS, DMN_1);
    private static final String VALID_MODELS_DMN1_PMML = String.format("%s%s/pmml", VALID_MODELS, DMN_1);

    private static final String INVALID_MODELS = "invalid_models";
    private static final String DMN_5 = "/DMNv1_5";
    private static final String INVALID_MODELS_DMN5 = String.format("%s%s", INVALID_MODELS, DMN_5);

    protected static final String DMN_MODEL_NAME = "loan";
    protected static final String DMN_FILE_NAME = "loan";
    protected static final String DMN_NAMESPACE = "https://kiegroup.org/dmn/_79B69A7F-5A25-4B53-BD6A-3216EDC246ED";
    protected static final String DMN_FULL_FILE_NAME = String.format("%s.dmn", DMN_FILE_NAME);
    protected static final String DMN_FULL_PATH_FILE_NAME = String.format("%s/%s", VALID_MODELS_DMN1,  DMN_FULL_FILE_NAME);
    protected static final String DMN_FULL_PATH_FILE_NAME_NO_SUFFIX = String.format("%s/%s", VALID_MODELS_DMN1, DMN_FILE_NAME);
    protected static MemoryFile dmnFile;

    protected static final String DMN_INVALID_MODEL_NAME = "DMN_9A35369C-E843-446F-A720-2A41B827FB8D";
    protected static final String DMN_INVALID_FILE_NAME = "DMN-Invalid";
    protected static final String DMN_INVALID_NAMESPACE = "https://kie.org/dmn/_C41C5BB7-C6D3-44AC-AA11-8C6669A1067C";
    protected static final String DMN_INVALID_FULL_FILE_NAME = String.format("%s.dmn", DMN_INVALID_FILE_NAME);
    protected static final String DMN_INVALID_FULL_PATH_FILE_NAME = String.format("%s/%s", INVALID_MODELS_DMN5,  DMN_INVALID_FULL_FILE_NAME);
    protected static final String DMN_INVALID_FULL_PATH_FILE_NAME_NO_SUFFIX = String.format("%s/%s", INVALID_MODELS_DMN5, DMN_INVALID_FILE_NAME);
    protected static MemoryFile dmnInvalidFile;

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
    protected static DmnCompilationContext dmnValidatingCompilationContext;
    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new EfestoCompilationManagerException("Failed to find an instance of CompilationManager: please check classpath and dependencies"));


    protected static EfestoResource toProcessDmn;
    protected static EfestoResource toProcessInvalidDmn;
    protected static EfestoResource toProcessDmnPmml;
    protected static EfestoResource notToProcessDmn;
    protected static EfestoResource notToProcessDmnPmml;


    @Test
    void  canManageResourceDmn() {
        assertThat(kieCompilationService.canManageResource(toProcessDmn)).isTrue();
        assertThat(kieCompilationService.canManageResource(notToProcessDmn)).isFalse();
    }

    @Test
    void  canManageResourceDmnPmml() {
        assertThat(kieCompilationService.canManageResource(toProcessDmnPmml)).isTrue();
        assertThat(kieCompilationService.canManageResource(notToProcessDmnPmml)).isFalse();
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
        assertThat(localComponentIdDmn.getFileName()).isEqualTo(DMN_FULL_PATH_FILE_NAME_NO_SUFFIX);
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
        assertThat(localComponentIdDmn.getFileName()).isEqualTo(DMN_PMML_FULL_PATH_FILE_NAME_NO_SUFFIX);
        assertThat(localComponentIdDmn.getName()).isEqualTo(DMN_PMML_MODEL_NAME);
    }

    @Test
    void  hasCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        assertThat(kieCompilationService.hasCompilationSource(DMN_MODEL_NAME)).isTrue();
    }

    @Test
    void hasCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        assertThat(kieCompilationService.hasCompilationSource(DMN_PMML_MODEL_NAME)).isTrue();
    }

    @Test
    void getCompilationSourceDmn() {
        kieCompilationService.processResource(toProcessDmn,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_MODEL_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getCompilationSourceDmnPmml() {
        kieCompilationService.processResource(toProcessDmnPmml,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_PMML_MODEL_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnPmmlFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void withoutValidationWrongModel() {
        kieCompilationService.processResource(toProcessInvalidDmn,
                                              dmnCompilationContext);
        String retrieved = kieCompilationService.getCompilationSource(DMN_INVALID_MODEL_NAME);
        assertThat(retrieved).isNotNull();
        String expected = new String(dmnInvalidFile.getContent(), StandardCharsets.UTF_8);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void withValidationWrongModel() {
        assertThatThrownBy(() -> kieCompilationService.processResource(toProcessInvalidDmn,
                                                                       dmnValidatingCompilationContext)).isInstanceOf(KieCompilerServiceException.class);
    }

    protected static void commonSetUp() {
        dmnFile = getMemoryFile(DMN_FULL_PATH_FILE_NAME);
        dmnInvalidFile = getMemoryFile(DMN_INVALID_FULL_PATH_FILE_NAME);
        pmmlFile = getMemoryFile(PMML_FULL_PATH_FILE_NAME);
        dmnPmmlFile = getMemoryFile(DMN_PMML_FULL_PATH_FILE_NAME);
        EfestoCompilationContext pmmlCompilationContext = EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());

        InputStream is = new ByteArrayInputStream(pmmlFile.getContent());
        EfestoInputStreamResource pmmlFileResource = new EfestoInputStreamResource(is, PMML_FULL_PATH_FILE_NAME);
        compilationManager.processResource(pmmlCompilationContext, pmmlFileResource);
        ContextStorage.putEfestoCompilationContext(getPmmlModelLocalUriId(), pmmlCompilationContext);
        ContextStorage.putEfestoCompilationSource(getPmmlModelLocalUriId(), new String( pmmlFile.getContent(), StandardCharsets.UTF_8));
        dmnCompilationContext = (DmnCompilationContext) EfestoCompilationContextUtils.buildFromContext((EfestoCompilationContextImpl) pmmlCompilationContext, DmnCompilationContextImpl.class);
        dmnValidatingCompilationContext = DmnCompilationContext.buildWithEfestoCompilationContext((EfestoCompilationContextImpl) pmmlCompilationContext,
                                                                                                                          Collections.EMPTY_SET,
                                                                                                                          Set.of(DMNValidator.Validation.values()),
                                                                                                                          null);
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