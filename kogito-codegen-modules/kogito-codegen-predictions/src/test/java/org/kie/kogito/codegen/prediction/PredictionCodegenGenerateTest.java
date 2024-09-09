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
package org.kie.kogito.codegen.prediction;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.ResourceType;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import com.github.javaparser.ast.CompilationUnit;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.kogito.codegen.api.Generator.REST_TYPE;
import static org.kie.kogito.codegen.api.utils.KogitoContextTestUtils.contextBuilders;
import static org.kie.kogito.codegen.prediction.PredictionCodegenFactory.parsePredictions;
import static org.kie.kogito.codegen.prediction.PredictionCodegenFactoryTest.REFLECT_JSON;

class PredictionCodegenGenerateTest {

    static final Path BASE_PATH = Paths.get("src/test/resources/").toAbsolutePath();
    private static final String REGRESSION_SOURCE = "prediction/test_regression.pmml";
    static final Path REGRESSION_FULL_SOURCE = BASE_PATH.resolve(REGRESSION_SOURCE);
    private static final String SCORECARD_SOURCE = "prediction/test_scorecard.pmml";
    static final Path SCORECARD_FULL_SOURCE = BASE_PATH.resolve(SCORECARD_SOURCE);
    private static final String MINING_SOURCE = "prediction/test_miningmodel.pmml";
    static final Path MINING_FULL_SOURCE = BASE_PATH.resolve(MINING_SOURCE);
    static final String MULTIPLE_SOURCE = "prediction/test_multiplemodels.pmml";
    static final Path MULTIPLE_FULL_SOURCE = BASE_PATH.resolve(MULTIPLE_SOURCE);

    @BeforeAll
    public static void setup() {
        System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, String.format("%s/test-classes", AppPaths.TARGET_DIR));
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
    }

    private static Stream<Arguments> data() {
        // The difference with PredictionCodegenTest#data is that the current one uses PredictionCodegen.generate()
        return contextBuilders().flatMap((Function<Arguments, Stream<Arguments>>) arguments -> {
            KogitoBuildContext.Builder contextBuilder =
                    (KogitoBuildContext.Builder) Arrays.stream(arguments.get()).findFirst().orElseThrow(() -> new IllegalStateException("Failed to retrieve KogitoBuildContext.Builder"));
            KogitoBuildContext context = contextBuilder.build();

            final List<Arguments> testArguments = new ArrayList<>();

            PredictionCodegen codeGenerator = getPredictionCodegen(context, REGRESSION_FULL_SOURCE);
            Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
            Arguments toAdd = arguments(codeGenerator, generatedFiles, 4, 3, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, SCORECARD_FULL_SOURCE);
            generatedFiles = codeGenerator.generate();
            toAdd = arguments(codeGenerator, generatedFiles, 4, 3, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, MINING_FULL_SOURCE);
            generatedFiles = codeGenerator.generate();
            toAdd = arguments(codeGenerator, generatedFiles, 13, 12, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, MULTIPLE_FULL_SOURCE);
            generatedFiles = codeGenerator.generate();
            toAdd = arguments(codeGenerator, generatedFiles, 13, 12, 2, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);
            return testArguments.stream();
        });
    }

    static PredictionCodegen getPredictionCodegen(KogitoBuildContext context, Path modelPath) {
        Collection<CollectedResource> resources = CollectedResourceProducer.fromFiles(BASE_PATH,
                modelPath.toFile());
        Collection<PMMLResource> pmmlResources = resources.stream()
                .filter(r -> r.resource().getResourceType() == ResourceType.PMML)
                .flatMap(r -> parsePredictions(Thread.currentThread().getContextClassLoader(), r.basePath(),
                        Collections.singletonList(r.resource())).stream())
                .collect(toList());
        return new PredictionCodegen(context, pmmlResources);
    }

    @MethodSource({ "data" })
    @ParameterizedTest
    void verifyTotalFiles(PredictionCodegen codeGenerator,
            Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedCompiledClasses,
            int expectedRestEndpoints,
            boolean assertReflect,
            boolean hasRest) {
        commonVerifyTotalFiles(generatedFiles, expectedTotalFiles, expectedRestEndpoints, hasRest);
    }

    @MethodSource({ "data" })
    @ParameterizedTest
    void verifyCompiledClasses(PredictionCodegen codeGenerator,
            Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedCompiledClasses,
            int expectedRestEndpoints,
            boolean assertReflect,
            boolean hasRest) {
        commonVerifyCompiledClasses(generatedFiles, expectedCompiledClasses);
    }

    @MethodSource({ "data" })
    @ParameterizedTest
    void verifyReflectResource(PredictionCodegen codeGenerator,
            Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedCompiledClasses,
            int expectedRestEndpoints,
            boolean assertReflect,
            boolean hasRest) {
        commonVerifyReflectResource(generatedFiles, assertReflect);
    }

    @MethodSource({ "data" })
    @ParameterizedTest
    void verifyRestEndpoints(PredictionCodegen codeGenerator,
            Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedCompiledClasses,
            int expectedRestEndpoints,
            boolean assertReflect,
            boolean hasRest) {
        commonVerifyRestEndpoints(generatedFiles, expectedRestEndpoints, hasRest);
    }

    @MethodSource({ "data" })
    @ParameterizedTest
    void verifySectionAndCompilationUnit(PredictionCodegen codeGenerator,
            Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedCompiledClasses,
            int expectedRestEndpoints,
            boolean assertReflect,
            boolean hasRest) {
        commonVerifySectionAndCompilationUnit(codeGenerator);
    }

    static void commonVerifyTotalFiles(Collection<GeneratedFile> generatedFiles,
            int expectedTotalFiles,
            int expectedRestEndpoints,
            boolean hasREST) {
        int expectedGeneratedFilesSize = expectedTotalFiles + (hasREST ? expectedRestEndpoints * 2 : 0);
        assertThat(generatedFiles).hasSize(expectedGeneratedFilesSize);
    }

    static void commonVerifyCompiledClasses(Collection<GeneratedFile> generatedFiles,
            int expectedCompiledClasses) {
        int actuallyCompiledClasses = (int) generatedFiles.stream()
                .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.COMPILED_CLASS) &&
                        generatedFile.type().equals(COMPILED_CLASS))
                .count();
        assertThat(actuallyCompiledClasses).isEqualTo(expectedCompiledClasses);
    }

    static void commonVerifyReflectResource(Collection<GeneratedFile> generatedFiles,
            boolean assertReflect) {
        int expectedReflectResource = assertReflect ? 1 : 0;
        assertThat(expectedReflectResource).isEqualTo(generatedFiles.stream()
                .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.INTERNAL_RESOURCE) &&
                        generatedFile.type().name().equals(GeneratedFileType.INTERNAL_RESOURCE.name()) &&
                        generatedFile.relativePath().endsWith(REFLECT_JSON))
                .count());
    }

    static void commonVerifyRestEndpoints(Collection<GeneratedFile> generatedFiles,
            int expectedRestEndpoints, boolean hasREST) {
        if (hasREST) {
            // REST resource
            assertThat(expectedRestEndpoints).isEqualTo(generatedFiles.stream()
                    .filter(generatedFile -> generatedFile.type().equals(REST_TYPE))
                    .count());
            // OpenAPI Json schema
            assertThat(expectedRestEndpoints).isEqualTo(generatedFiles.stream()
                    .filter(generatedFile -> generatedFile.category().equals(GeneratedFileType.Category.STATIC_HTTP_RESOURCE) &&
                            generatedFile.type().name().equals(GeneratedFileType.STATIC_HTTP_RESOURCE.name()) &&
                            !generatedFile.relativePath().endsWith(REFLECT_JSON))
                    .count());
        }
    }

    static void commonVerifySectionAndCompilationUnit(PredictionCodegen codeGenerator) {
        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertThat(optionalApplicationSection).isPresent();
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertThat(compilationUnit).isNotNull();
    }
}
