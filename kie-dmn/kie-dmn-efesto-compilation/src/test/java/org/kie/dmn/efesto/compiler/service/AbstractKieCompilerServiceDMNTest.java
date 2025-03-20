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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContextImpl;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static org.kie.efesto.common.utils.PackageClassNameUtils.getSanitizedClassName;

public abstract class AbstractKieCompilerServiceDMNTest {

    protected static final String dmnModelName = "loan";
    protected static final String dmnFileName = "loan";
    protected static final String dmnFullFileName = String.format("%s.dmn", dmnFileName);
    protected static final String dmnFullPathFileName = String.format("valid_models/DMNv1_x/%s", dmnFullFileName);
    protected static MemoryFile dmnFile;

    protected static final String dmnPmmlModelName = "TestRegressionDMN";
    protected static final String dmnPmmlFileName = "KiePMMLRegression";
    protected static final String dmnPmmlFullFileName = String.format("%s.dmn", dmnPmmlFileName);
    protected static final String dmnPmmlFullPathFileName = String.format("valid_models/DMNv1_x/pmml/%s", dmnPmmlFullFileName);
    protected static MemoryFile dmnPmmlFile;

    protected static final String pmmlModelName =  "TestRegression";
    protected static final String pmmlFileName = "test_regression";
    protected static final String pmmlFullFileName = String.format("%s.pmml", pmmlFileName);
    protected static final String pmmlFullPathFileName = String.format("valid_models/DMNv1_x/pmml/%s", pmmlFullFileName);
    protected static MemoryFile pmmlFile;

    protected static KieCompilerService kieCompilationService;
    protected static DmnCompilationContext dmnCompilationContext;
    private static final CompilationManager compilationManager =
            org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new EfestoCompilationManagerException("Failed to find an instance of CompilationManager: please check classpath and dependencies"));



    protected static void commonSetUp() throws IOException {
        dmnFile = getMemoryFile(dmnFullPathFileName);
        pmmlFile = getMemoryFile(pmmlFullPathFileName);
        dmnPmmlFile = getMemoryFile(dmnPmmlFullPathFileName);

//        URL dmnFileResource = Thread.currentThread().getContextClassLoader().getResource(dmnFullPathFileName);
//        assertThat(dmnFileResource).isNotNull();
//        dmnFile = new File(dmnFileResource.getFile());
//
//        URL pmmlFileResource = Thread.currentThread().getContextClassLoader().getResource(pmmlFullPathFileName);
//        assertThat(pmmlFileResource).isNotNull();
//        pmmlFile = new File(pmmlFileResource.getFile());
//
//        URL dmnPmmlFileResource = Thread.currentThread().getContextClassLoader().getResource(dmnPmmlFullPathFileName);
//        assertThat(dmnPmmlFileResource).isNotNull();
//        dmnPmmlFile = new File(dmnPmmlFileResource.getFile());

        EfestoCompilationContext pmmlCompilationContext = EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());

//        InputStream is = new ByteArrayInputStream(((MemoryFile) pmmlFile).getContent());
//        EfestoInputStreamResource pmmlFileResource = new EfestoInputStreamResource(is, pmmlFullPathFileName);
//        compilationManager.processResource(pmmlCompilationContext, pmmlFileResource);
//        ContextStorage.putEfestoCompilationContext(getPmmlModelLocalUriId(), pmmlCompilationContext);
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
        String path = "/pmml/" + getFileNameNoSuffix(pmmlFullFileName) + SLASH + getSanitizedClassName(pmmlModelName);
        LocalUri parsed = LocalUri.parse(path);
        return new ModelLocalUriId(parsed);
    }

    private static String getFileNameNoSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
    }

}