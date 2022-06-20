package org.kie.efesto.compilationmanager.core.utils;/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.*;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.efesto.common.api.utils.JSONUtils.getGeneratedResourcesObject;
import static org.kie.efesto.common.api.utils.JSONUtils.writeGeneratedResourcesObject;

class CompilationManagerUtilsTest {

    private final static String modelType = "test";
    private final static FRI fri = new FRI("this/is/fri", modelType);
    private final static FRI notExistingfri = new FRI("this/is/fri", "notexisting");
    private final static Map<String, byte[]> compiledClassMap = IntStream.range(0, 3).boxed().collect(Collectors.toMap(integer -> "class_" + integer, integer -> new byte[0]));
    private final static EfestoCallableOutputClassesContainer finalOutput = getEfestoFinalOutputClassesContainer(fri);

//    @BeforeEach
//    public void init() {
//        try {
//            CompilationManagerUtils.getIndexFile(finalOutput).delete();
//        } catch (KieEfestoCommonException e) {
//            // Ignore
//        }
//    }

    @Test
    void populateIndexFilesWithProcessedResource() {
    }

    @Test
    void getIndexFileFromFinalOutput() {
    }

    @Test
    void getIndexFileExisting() {
        IndexFile retrieved = CompilationManagerUtils.getIndexFile(finalOutput);
        assertThat(retrieved).isNotNull();
        String expectedName = "IndexFile.test_json";
        assertThat(retrieved.getName()).isEqualTo(expectedName);
    }

    @Test
    void getIndexFileNotExisting() {
        EfestoCallableOutputClassesContainer notExistingOutput = getEfestoFinalOutputClassesContainer(notExistingfri);
        IndexFile retrieved = CompilationManagerUtils.getIndexFile(notExistingOutput);
        assertThat(retrieved).isNotNull();
        String expectedName = "IndexFile.notexisting_json";
        assertThat(retrieved.getName()).isEqualTo(expectedName);
    }

    @Test
    void populateIndexFile() throws IOException {
        IndexFile toPopulate = CompilationManagerUtils.getIndexFile(finalOutput);
        GeneratedResources originalGeneratedResources = getGeneratedResourcesObject(toPopulate);
        int expectedResources = 2; // 1 final resource + 1 intermediate resources
        assertThat(originalGeneratedResources).hasSize(expectedResources);
        CompilationManagerUtils.populateIndexFile(toPopulate, finalOutput);
        GeneratedResources generatedResources = getGeneratedResourcesObject(toPopulate);
        expectedResources = 6; // 2 final resource + 4 class resources
        assertThat(generatedResources).hasSize(expectedResources);
        List<GeneratedExecutableResource> executableResources = generatedResources.stream().filter(generatedResource -> generatedResource instanceof GeneratedExecutableResource).map(GeneratedExecutableResource.class::cast).collect(Collectors.toList());
        expectedResources = 2; // 2 final resource
        assertThat(executableResources).hasSize(expectedResources);

        GeneratedExecutableResource finalResource = executableResources.stream().filter(generatedExecutableResource -> finalOutput.getFri().equals(generatedExecutableResource.getFri())).findFirst().orElse(null);

        commonEvaluateGeneratedExecutableResource(finalResource);
        List<GeneratedClassResource> classResources = generatedResources.stream().filter(generatedResource -> generatedResource instanceof GeneratedClassResource).map(GeneratedClassResource.class::cast).collect(Collectors.toList());
        expectedResources = 4; // 4 class resources
        assertThat(classResources).hasSize(expectedResources);

        List<GeneratedResource> classResourcesGenerated = classResources.stream().filter(generatedResource -> !generatedResource.getFullClassName().equals("type")).collect(Collectors.toList());
        commonEvaluateGeneratedIntermediateResources(classResourcesGenerated);

        // restore clean situation
        writeGeneratedResourcesObject(originalGeneratedResources, toPopulate);
    }

    @Test
    void populateGeneratedResources() {
        GeneratedResources toPopulate = new GeneratedResources();
        assertThat(toPopulate).isEmpty();
        CompilationManagerUtils.populateGeneratedResources(toPopulate, finalOutput);
        int expectedResources = 4; // 1 final resource + 3 intermediate resources
        assertThat(toPopulate).hasSize(expectedResources);
        GeneratedResource finalResource = toPopulate.stream().filter(generatedResource -> generatedResource instanceof GeneratedExecutableResource).findFirst().orElse(null);
        commonEvaluateGeneratedExecutableResource(finalResource);
        List<GeneratedResource> classResources = toPopulate.stream().filter(generatedResource -> generatedResource instanceof GeneratedClassResource).map(GeneratedClassResource.class::cast).collect(Collectors.toList());
        commonEvaluateGeneratedIntermediateResources(classResources);
    }

    @Test
    void getGeneratedResource() {
        GeneratedResource retrieved = CompilationManagerUtils.getGeneratedResource(finalOutput);
        commonEvaluateGeneratedExecutableResource(retrieved);
    }

    @Test
    void getGeneratedResources() {
        List<GeneratedResource> retrieved = CompilationManagerUtils.getGeneratedResources(finalOutput);
        commonEvaluateGeneratedIntermediateResources(retrieved);
    }

    @Test
    void getGeneratedIntermediateResource() {
        String className = "className";
        GeneratedClassResource retrieved = CompilationManagerUtils.getGeneratedClassResource(className);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getFullClassName()).isEqualTo(className);
    }

    private void commonEvaluateGeneratedExecutableResource(GeneratedResource generatedResource) {
        assertThat(generatedResource).isNotNull();
        assertThat(generatedResource instanceof GeneratedExecutableResource).isTrue();
        assertThat(((GeneratedExecutableResource) generatedResource).getFri()).isEqualTo(finalOutput.getFri());
    }

    private void commonEvaluateGeneratedIntermediateResources(List<GeneratedResource> retrieved) {
        assertThat(retrieved).isNotNull();
//        assertThat(retrieved).hasSize(compiledClassMap.size());
        compiledClassMap.keySet().forEach(fullClassName -> {
            assertTrue(retrieved.stream().filter(GeneratedClassResource.class::isInstance).map(GeneratedClassResource.class::cast).anyMatch(generatedResource -> generatedResource.getFullClassName().equals(fullClassName)));
        });
    }

    private static EfestoCallableOutputClassesContainer getEfestoFinalOutputClassesContainer(FRI usedFri) {
        return new EfestoCallableOutputClassesContainer(usedFri, usedFri.getModel() + "Resources", compiledClassMap) {
        };
    }
}