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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import com.github.javaparser.ast.CompilationUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.codegen.api.Generator.REST_TYPE;

class PredictionCodegenTest {

    private static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final String REGRESSION_SOURCE = "prediction/test_regression.pmml";
    private static final Path REGRESSION_FULL_SOURCE = BASE_PATH.resolve(REGRESSION_SOURCE);
    private static final String SCORECARD_SOURCE = "prediction/test_scorecard.pmml";
    private static final Path SCORECARD_FULL_SOURCE = BASE_PATH.resolve(SCORECARD_SOURCE);
    private static final String REFLECT_JSON = "reflect-config.json";

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateAllFilesRegression(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        PredictionCodegen codeGenerator = PredictionCodegen.ofCollectedResources(
                contextBuilder.build(),
                CollectedResourceProducer.fromFiles(BASE_PATH, REGRESSION_FULL_SOURCE.toFile()));

        int expectedRestResources = context.hasREST() ? 2 : 0;

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertEquals(3 + expectedRestResources, generatedFiles.size());
        assertEquals(3, generatedFiles.stream()
                .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.SOURCE) &&
                        generatedFile.type().name().equals("PMML") &&
                        generatedFile.relativePath().endsWith(".java"))
                .count());

        assertEndpoints(context, generatedFiles);

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertTrue(optionalApplicationSection.isPresent());
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateAllFilesScorecard(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        PredictionCodegen codeGenerator = PredictionCodegen.ofCollectedResources(
                context,
                CollectedResourceProducer.fromFiles(BASE_PATH, SCORECARD_FULL_SOURCE.toFile()));

        // for each REST endpoint it also generates OpenAPI json schema
        int expectedRestResources = context.hasREST() ? 2 : 0;

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertEquals(25 + expectedRestResources, generatedFiles.size());
        assertEquals(4, generatedFiles.stream()
                .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.SOURCE) &&
                        generatedFile.type().name().equals("PMML") &&
                        generatedFile.relativePath().endsWith(".java"))
                .count());

        assertEndpoints(context, generatedFiles);

        assertEquals(1, generatedFiles.stream()
                .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.RESOURCE) &&
                        generatedFile.type().name().equals(GeneratedFileType.RESOURCE.name()) &&
                        generatedFile.relativePath().endsWith(REFLECT_JSON))
                .count());
        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertTrue(optionalApplicationSection.isPresent());
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit);
    }

    private void assertEndpoints(KogitoBuildContext context, Collection<GeneratedFile> generatedFiles) {
        if (context.hasREST()) {
            // REST resource
            assertEquals(1, generatedFiles.stream()
                    .filter(generatedFile -> generatedFile.type().equals(REST_TYPE))
                    .count());
            // OpenAPI Json schema
            assertEquals(1, generatedFiles.stream()
                    .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.RESOURCE) &&
                            generatedFile.type().name().equals(GeneratedFileType.RESOURCE.name()) &&
                            !generatedFile.relativePath().endsWith(REFLECT_JSON))
                    .count());
        }
    }
}
