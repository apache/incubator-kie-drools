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
package org.kie.pmml.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.util.FileUtils.getFileContent;

class PMMLCompilationContextImplTest {

    private static final String fileName = "FileName.pmml";

    private static Map<String, String> allSourcesMap;

    private static Set<File> pmmlFiles;

    private static PMMLCompilationContextImpl pmmlCompilationContext;

    private static KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    private static Map<String, byte[]> compiledClasses;

    @BeforeAll
    public static void setup() throws IOException {
        pmmlFiles = PMMLTestUtils.collectFiles("src/test/resources/org/kie/model/project/codegen", "java");

        allSourcesMap = pmmlFiles.stream().collect(Collectors.toMap(file -> file.getName().replace(".java", ""),
                                                                    file -> {
                                                                        try {
                                                                            return getFileContent(file.getName());
                                                                        } catch (IOException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                    }));
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
        pmmlCompilationContext = new PMMLCompilationContextImpl(fileName, memoryCompilerClassLoader);
        compiledClasses = pmmlCompilationContext.compileClasses(allSourcesMap);
    }

    @Test
    void getModelLocalUriIdsForFile() {
        String path = "/pmml/" + fileName + "/testmod";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(parsed);
        pmmlCompilationContext.addGeneratedClasses(modelLocalUriId, compiledClasses);
        Set<ModelLocalUriId> retrieved = pmmlCompilationContext.getModelLocalUriIdsForFile();
        assertThat(retrieved.size()).isEqualTo(1);
        assertThat(retrieved.iterator().next()).isEqualTo(modelLocalUriId);
    }

    @Test
    void getName() {
        assertThat(pmmlCompilationContext.getName()).startsWith("Context_");
    }
}