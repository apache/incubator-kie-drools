/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.prediction;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionCodegenTest {

    private static final String SOURCE = "prediction/test_regression.pmml";
    private static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final Path FULL_SOURCE = BASE_PATH.resolve(SOURCE);

    @Test
    void generateAllFiles() {
        PredictionCodegen codeGenerator = PredictionCodegen.ofCollectedResources(
                JavaKogitoBuildContext.builder().build(),
                CollectedResourceProducer.fromFiles(BASE_PATH, FULL_SOURCE.toFile()));

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertEquals(5, generatedFiles.size());
        assertEquals(4, generatedFiles.stream()
                .filter(generatedFile ->
                                                               generatedFile.category().equals(GeneratedFileType.Category.SOURCE) &&
                                                                       generatedFile.type().name().equals("PMML") &&
                                                                       generatedFile.relativePath().endsWith(".java"))
                .count());
        assertEquals(1, generatedFiles.stream()
                .filter(generatedFile ->
                                                               generatedFile.category().equals(GeneratedFileType.Category.RESOURCE) &&
                                                                       generatedFile.type().name().equals(GeneratedFileType.RESOURCE.name()) &&
                                                                       generatedFile.relativePath().endsWith(".json"))
                .count());

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertTrue(optionalApplicationSection.isPresent());
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit);
    }

}
