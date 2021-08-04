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
package org.kie.kogito.codegen.rules;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DecisionTableProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IncrementalRuleCodegenTest {

    private static final String RESOURCE_PATH = "src/test/resources";

    @BeforeEach
    public void setup() {
        DecisionTableFactory.setDecisionTableProvider(ServiceRegistry.getInstance().get(DecisionTableProvider.class));
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void isEmpty(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        IncrementalRuleCodegen emptyCodeGenerator = IncrementalRuleCodegen.ofCollectedResources(context, Collections.emptyList());

        assertThat(emptyCodeGenerator.isEmpty()).isTrue();
        assertThat(emptyCodeGenerator.isEnabled()).isFalse();

        Collection<GeneratedFile> emptyGeneratedFiles = emptyCodeGenerator.generate();
        assertThat(emptyGeneratedFiles.size()).isEqualTo(0);

        IncrementalRuleCodegen codeGenerator = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/rules/pkg1/file1.drl"));

        assertThat(codeGenerator.isEmpty()).isFalse();
        assertThat(codeGenerator.isEnabled()).isTrue();

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateSingleFile(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/rules/pkg1/file1.drl"));

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(3, 1, generatedFiles.size());
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateSinglePackage(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/rules/pkg1").listFiles());

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(5, 1, generatedFiles.size());
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateSinglePackageSingleUnit(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/rules/multiunit").listFiles());

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnitInstance.java")));
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateDirectoryRecursively(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromPaths(
                contextBuilder,
                Paths.get(RESOURCE_PATH + "/org/kie/kogito/codegen/rules"));

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnitInstance.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnitInstance.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/singleton/SingletonRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/singleton/SingletonRuleUnitInstance.java")));
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateSingleDtable(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/candrink/CanDrink.xls"));

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 5;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateSingleUnit(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/rules/myunit").listFiles());

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnitInstance.java")));
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateCepRule(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/cep/cep.drl"));

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 2;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void raiseErrorOnSyntaxError(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/broken.drl"));
        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void raiseErrorOnBadOOPath(KogitoBuildContext.Builder contextBuilder) {
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/brokenrules/brokenunit/ABrokenUnit.drl"));

        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void throwWhenDtableDependencyMissing(KogitoBuildContext.Builder contextBuilder) {
        DecisionTableFactory.setDecisionTableProvider(null);
        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/candrink/CanDrink.xls"));
        assertThrows(MissingDecisionTableDependencyError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateGrafanaDashboards(KogitoBuildContext.Builder contextBuilder) {
        contextBuilder.withAddonsConfig(AddonsConfig.builder()
                .withPrometheusMonitoring(true)
                .withMonitoring(true)
                .build());

        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/unit/RuleUnitQuery.drl"));

        int expectedDashboards = contextBuilder.build().hasRESTForGenerator(incrementalRuleCodegen) ? 3 : 1; // The domain dashboard to monitor the RuleUnit is always generated.

        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertEquals(expectedDashboards, generatedFiles.stream().filter(x -> x.type().equals(DashboardGeneratedFileUtils.DASHBOARD_TYPE)).count());
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void elapsedTimeMonitoringIsWrappingEveryMethod(KogitoBuildContext.Builder contextBuilder) {
        contextBuilder.withAddonsConfig(AddonsConfig.builder()
                .withPrometheusMonitoring(true)
                .withMonitoring(true)
                .build())
                .build();

        IncrementalRuleCodegen incrementalRuleCodegen = getIncrementalRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/kie/kogito/codegen/unit/RuleUnitQuery.drl"));
        Collection<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();

        List<String> endpointClasses = generatedFiles
                .stream()
                .filter(x -> x.relativePath().contains("Endpoint"))
                .map(x -> new String(x.contents()))
                .collect(Collectors.toList());

        for (String endpointClass : endpointClasses) {
            assertMonitoringEndpoints(endpointClass);
        }
    }

    private IncrementalRuleCodegen getIncrementalRuleCodegenFromFiles(KogitoBuildContext.Builder contextBuilder, File... resources) {
        return IncrementalRuleCodegen.ofCollectedResources(
                contextBuilder.build(),
                CollectedResourceProducer.fromFiles(Paths.get(RESOURCE_PATH), resources));
    }

    private IncrementalRuleCodegen getIncrementalRuleCodegenFromPaths(KogitoBuildContext.Builder contextBuilder, Path... paths) {
        return IncrementalRuleCodegen.ofCollectedResources(
                contextBuilder.build(),
                CollectedResourceProducer.fromPaths(paths));
    }

    private static void assertMonitoringEndpoints(String endpointClass) {
        CompilationUnit cu = StaticJavaParser.parse(endpointClass);

        ClassOrInterfaceDeclaration clazz = cu.findFirst(ClassOrInterfaceDeclaration.class).get();
        ReturnStmt executeQueryReturnStmt = clazz.getMethodsByName("executeQuery")
                .get(0)
                .getBody()
                .orElseThrow(() -> new RuntimeException("No body found for executeQuery method"))
                .findFirst(ReturnStmt.class)
                .orElseThrow(() -> new RuntimeException("No return statement for executeQuery method. Template has changed."));
        ReturnStmt executeQueryFirstReturnStmt = clazz.getMethodsByName("executeQueryFirst")
                .get(0)
                .getBody()
                .orElseThrow(() -> new RuntimeException("No body found for executeQueryFirst method"))
                .findFirst(ReturnStmt.class)
                .orElseThrow(() -> new RuntimeException("No return statement for executeQueryFirst method. Template has changed."));

        // SystemMetricsCollectorProvider field has been created
        Assertions.assertTrue(clazz.getFieldByName("systemMetricsCollectorProvider").isPresent());

        // Return expression should not be a call, otherwise the elapsed time would not be calculated properly
        Assertions.assertFalse(executeQueryReturnStmt.getExpression().get().isMethodCallExpr());
        Assertions.assertFalse(executeQueryFirstReturnStmt.getExpression().get().isMethodCallExpr());

        // The monitoring code is generated
        String statementsExecuteQuery = clazz.getMethodsByName("executeQuery").get(0).getBody().get().getStatements().toString();
        Assertions.assertTrue(statementsExecuteQuery.contains("startTime"));
        Assertions.assertTrue(statementsExecuteQuery.contains("endTime"));
        Assertions.assertTrue(statementsExecuteQuery.contains("registerElapsedTimeSampleMetrics"));

        String statementsExecuteQueryFirst = clazz.getMethodsByName("executeQueryFirst").get(0).getBody().get().getStatements().toString();
        Assertions.assertTrue(statementsExecuteQueryFirst.contains("startTime"));
        Assertions.assertTrue(statementsExecuteQueryFirst.contains("endTime"));
        Assertions.assertTrue(statementsExecuteQueryFirst.contains("registerElapsedTimeSampleMetrics"));
    }

    private static void assertRules(int expectedRules, int expectedPackages, int actualGeneratedFiles) {
        assertEquals(expectedRules +
                expectedPackages * 2, // package descriptor for rules + package metadata
                actualGeneratedFiles);
    }
}
