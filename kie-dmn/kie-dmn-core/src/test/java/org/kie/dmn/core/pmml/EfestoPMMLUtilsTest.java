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
package org.kie.dmn.core.pmml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_5.TImport;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.storage.ContextStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.pmml.EfestoPMMLUtils.resolveRelativeResource;

@SuppressWarnings({"unchecked", "rawtypes"})
class EfestoPMMLUtilsTest {

    private static final String PMML_FILE_PATH = "../pmml/test_tree_new.pmml";
    private static File PMML_FILE;
    private static String PMML_SOURCE;

    @BeforeAll
    static void setUp() throws IOException {
        URL resource = EfestoPMMLUtilsTest.class.getResource(PMML_FILE_PATH);
        assertThat(resource).isNotNull();
        PMML_FILE = new File(resource.getFile());
        assertThat(PMML_FILE).isNotNull().exists();
        PMML_SOURCE = Files.readString(PMML_FILE.toPath());
        assertThat(PMML_SOURCE).isNotNull().isNotEmpty();
    }

    @BeforeEach
    public  void init() {
        ContextStorage.reset();
    }

    @Test
    void compilePMMLFromFile() {
        ModelLocalUriId retrieved = EfestoPMMLUtils.compilePMML(PMML_FILE, Thread.currentThread().getContextClassLoader());
        commonCompilePMML(retrieved);
    }

    @Test
    void compilePMMLFromSource() {
        ModelLocalUriId retrieved = EfestoPMMLUtils.compilePMML(PMML_SOURCE, PMML_FILE.getAbsolutePath(), Thread.currentThread().getContextClassLoader());
        commonCompilePMML(retrieved);
    }

    @Test
    void resolveRelativeResourceWithResolver() {
        commonCompileFromResolver(EfestoPMMLUtils::resolveRelativeResource);
    }

    @ParameterizedTest
    @MethodSource("pmmlPaths")
    void resolveRelativeResourceWithoutResolver(String fullFileNamePath, ModelLocalUriId expected) {
        Import anImport = new TImport();
        anImport.setLocationURI(fullFileNamePath);
        ModelLocalUriId retrieved = resolveRelativeResource(anImport, null);
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

    @Test
    void compilePmmlFromRelativeResolver() {
        commonCompileFromResolver(EfestoPMMLUtils::compilePmmlFromRelativeResolver);
    }

    @ParameterizedTest
    @MethodSource("pmmlPaths")
    void getPmmlModelLocalUriIdFromImport(String fullFileNamePath, ModelLocalUriId expected) {
        Import anImport = new TImport();
        anImport.setLocationURI(fullFileNamePath);
        ModelLocalUriId retrieved = EfestoPMMLUtils.getPmmlModelLocalUriIdFromImport(anImport);
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("pmmlPaths")
    void getPmmlModelLocalUriIdFromFullPath(String fullFileNamePath, ModelLocalUriId expected) {
        ModelLocalUriId retrieved = EfestoPMMLUtils.getPmmlModelLocalUriIdFromFullPath(fullFileNamePath);
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("pmmlPaths")
    void getStringFromRelativeResolver(String fullFileNamePath, ModelLocalUriId modelLocalUriId) {
        String content = UUID.randomUUID().toString();
        Function<String, Reader> relativeResolver = getRelativeResolver(fullFileNamePath, content);
        assertThat(EfestoPMMLUtils.getStringFromRelativeResolver(fullFileNamePath, relativeResolver)).isNotNull().isEqualTo(content);
    }

    private void commonCompilePMML(ModelLocalUriId retrieved) {
        assertThat(retrieved).isNotNull();
        assertThat(EfestoPMMLUtils.getPmmlSourceFromContextStorage(retrieved)).isEqualTo(PMML_SOURCE);
        EfestoCompilationContext efestoCompilationContext = ContextStorage.getEfestoCompilationContext(retrieved);
        assertThat(efestoCompilationContext).isNotNull();
        Map<String, GeneratedResources> generatedResourcesMap = efestoCompilationContext.getGeneratedResourcesMap();
        assertThat(generatedResourcesMap).isNotNull().hasSize(1).containsOnlyKeys("pmml");
        GeneratedResources generatedResources = generatedResourcesMap.get("pmml");
        System.out.println(generatedResources);
    }

    private void commonCompileFromResolver(BiFunction<Import, Function<String, Reader>, ModelLocalUriId> methodToCall) {
        Import anImport = new TImport();
        anImport.setLocationURI(PMML_FILE.getAbsolutePath());
        Function<String, Reader> relativeResolver = getRelativeResolver(anImport.getLocationURI(), PMML_SOURCE);
        ModelLocalUriId retrieved = methodToCall.apply(anImport, relativeResolver);
        assertThat(retrieved).isNotNull();
        assertThat(EfestoPMMLUtils.getPmmlSourceFromContextStorage(retrieved)).isNotNull().isEqualTo(PMML_SOURCE);
    }

    private Function<String, Reader> getRelativeResolver(String key, String content) {
        return s -> s.equals(key) ? new StringReader(content) : null;
    }

    private static Object[][] pmmlPaths() {
        return new Object[][]{
                {"this/is/a/path/to/model.pmml", new ModelLocalUriId(LocalUri.parse("/pmml/this/is/a/path/to/Model"))},
                {"this/is/a/path/to/model", new ModelLocalUriId(LocalUri.parse("/pmml/this/is/a/path/to/Model"))},
                {"model.pmml", new ModelLocalUriId(LocalUri.parse("/pmml/model/Model"))}
        };
    }

}