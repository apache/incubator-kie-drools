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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.kogito.codegen.api.utils.KogitoContextTestUtils.contextBuilders;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.MINING_FULL_SOURCE;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.MULTIPLE_FULL_SOURCE;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.REGRESSION_FULL_SOURCE;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.SCORECARD_FULL_SOURCE;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.commonVerifyCompiledClasses;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.commonVerifyReflectResource;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.commonVerifyRestEndpoints;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.commonVerifySectionAndCompilationUnit;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.commonVerifyTotalFiles;
import static org.kie.kogito.codegen.prediction.PredictionCodegenGenerateTest.getPredictionCodegen;

@Disabled("Temporarily disabled due to https://github.com/apache/incubator-kie-kogito-runtimes/issues/3640")
class PredictionCodegenInternalGenerateTest {

    @BeforeAll
    public static void setup() {
        System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, String.format("%s/test-classes", AppPaths.TARGET_DIR));
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
    }

    private static Stream<Arguments> data() {
        // The difference with PredictionCodegenFactoryModelsTest#data is that the current one uses PredictionCodegen
        // .internalGenerate()
        return contextBuilders().flatMap((Function<Arguments, Stream<Arguments>>) arguments -> {
            KogitoBuildContext.Builder contextBuilder =
                    (KogitoBuildContext.Builder) Arrays.stream(arguments.get()).findFirst().orElseThrow(() -> new IllegalStateException("Failed to retrieve KogitoBuildContext.Builder"));
            KogitoBuildContext context = contextBuilder.build();

            final List<Arguments> testArguments = new ArrayList<>();

            PredictionCodegen codeGenerator = getPredictionCodegen(context, REGRESSION_FULL_SOURCE);
            Collection<GeneratedFile> generatedFiles = codeGenerator.internalGenerate();
            Arguments toAdd = arguments(codeGenerator, generatedFiles, 4, 3, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, SCORECARD_FULL_SOURCE);
            generatedFiles = codeGenerator.internalGenerate();
            toAdd = arguments(codeGenerator, generatedFiles, 36, 34, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, MINING_FULL_SOURCE);
            generatedFiles = codeGenerator.internalGenerate();
            toAdd = arguments(codeGenerator, generatedFiles, 79, 77, 1, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);

            codeGenerator = getPredictionCodegen(context, MULTIPLE_FULL_SOURCE);
            generatedFiles = codeGenerator.internalGenerate();
            toAdd = arguments(codeGenerator, generatedFiles, 86, 84, 2, false,
                    context.hasRESTForGenerator(codeGenerator));
            testArguments.add(toAdd);
            return testArguments.stream();
        });
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
}
