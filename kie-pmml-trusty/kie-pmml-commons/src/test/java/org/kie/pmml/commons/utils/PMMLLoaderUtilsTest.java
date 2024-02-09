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
package org.kie.pmml.commons.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.core.utils.JSONUtils;
import org.kie.efesto.runtimemanager.api.exceptions.EfestoRuntimeManagerException;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.PMMLContext;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.PMML_STRING;

class PMMLLoaderUtilsTest {

    private static final String basePath = "testmod";
    private static final String MODEL_NAME = "TestMod";

    private static final String FILE_NAME = "FileName";

    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;
    private static Map<String, GeneratedResources> generatedResourcesMap;


    @BeforeAll
    static void setUp() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        Map<String, IndexFile> indexFileMap = IndexFile.findIndexFilesFromClassLoader(memoryCompilerClassLoader, Collections.singleton(PMML_STRING));
        generatedResourcesMap = new HashMap<>();
        indexFileMap.forEach((model, indexFile) -> {
            try {
                GeneratedResources generatedResources = JSONUtils.getGeneratedResourcesObject(indexFile);
                generatedResourcesMap.put(model, generatedResources);
            } catch (Exception e) {
                throw new EfestoRuntimeManagerException("Failed to read IndexFile content : " + indexFile.getAbsolutePath(), e);
            }
        });
    }


    @Test
    void loadKiePMMLModelFactory() {
        KiePMMLModelFactory retrieved = PMMLLoaderUtils.loadKiePMMLModelFactory(new ModelLocalUriId(LocalUri.parse("/" + PMML_STRING +
                                                                                                       "/" + basePath)),
                                                                                getPMMLContext(FILE_NAME, MODEL_NAME));
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getKiePMMLModels()).hasSize(1);
        KiePMMLModel kiePmmlModel = retrieved.getKiePMMLModels().get(0);
        assertThat(kiePmmlModel.getName()).isEqualTo(MODEL_NAME);
    }

    @Test
    void loadNotExistingKiePMMLModelFactory() {
        try {
            PMMLLoaderUtils.loadKiePMMLModelFactory(new ModelLocalUriId(LocalUri.parse("/notpmml/" + basePath)), getPMMLContext(FILE_NAME,
                                                                                                           MODEL_NAME));
            fail("Expecting KieRuntimeServiceException");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(KieRuntimeServiceException.class);
        }
    }

    private PMMLContext getPMMLContext(String fileName, String modelName, Set<PMMLListener> listeners) {

        return new PMMLContext<PMMLListener>() {

            @Override
            public String getName() {
                return modelName;
            }

            @Override
            public Object get(String identifier) {
                return null;
            }

            @Override
            public void set(String identifier, Object value) {

            }

            @Override
            public void remove(String identifier) {

            }

            @Override
            public boolean has(String identifier) {
                return false;
            }

            @Override
            public Class<?> loadClass(String className) throws ClassNotFoundException {
                return memoryCompilerClassLoader.loadClass(className);
            }

            @Override
            public Map<String, GeneratedResources> getGeneratedResourcesMap() {
                return generatedResourcesMap;
            }
        };
    }

    private PMMLContext getPMMLContext(String fileName, String modelName) {
        return getPMMLContext(fileName, modelName, Collections.emptySet());
    }
}