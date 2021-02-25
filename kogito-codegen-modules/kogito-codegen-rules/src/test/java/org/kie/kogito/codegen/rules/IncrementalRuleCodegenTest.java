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
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DecisionTableProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.core.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.ReturnStmt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IncrementalRuleCodegenTest {

    private static final KogitoBuildContext ACME_CONTEXT = JavaKogitoBuildContext.builder()
            .withPackageName("com.acme")
            .build();

    @BeforeEach
    public void setup() {
        DecisionTableFactory.setDecisionTableProvider(ServiceRegistry.getInstance().get(DecisionTableProvider.class));
    }

    @Test
    public void generateSingleFile() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/rules/pkg1/file1.drl")));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(3, 1, generatedFiles.size());
    }

    @Test
    public void generateSinglePackage() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/rules/pkg1").listFiles()));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(5, 1, generatedFiles.size());
    }

    @Test
    public void generateSinglePackageSingleUnit() {
        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withPackageName("org.kie.kogito.codegen.rules.multiunit")
                .build();

        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        context,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/rules/multiunit").listFiles()));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnitInstance.java")));
    }

    @Test
    public void generateDirectoryRecursively() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromPaths(Paths.get("src/test/resources/org/kie/kogito/codegen/rules")));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/multiunit/MultiUnitRuleUnitInstance.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnitInstance.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/singleton/SingletonRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/singleton/SingletonRuleUnitInstance.java")));
    }

    @Test
    public void generateSingleDtable() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/candrink/CanDrink.xls")));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 5;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @Test
    public void generateSingleUnit() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromPaths(Paths.get("src/test/resources/org/kie/kogito/codegen/rules/myunit")));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnit.java")));
        assertTrue(generatedFiles.stream().anyMatch(f -> f.relativePath().equals("org/kie/kogito/codegen/rules/myunit/MyUnitRuleUnitInstance.java")));
    }

    @Test
    public void generateCepRule() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/cep/cep.drl")));

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 2;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @Test
    public void raiseErrorOnSyntaxError() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/broken.drl")));
        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @Test
    public void raiseErrorOnBadOOPath() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/brokenrules/brokenunit/ABrokenUnit.drl")));

        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @Test
    public void throwWhenDtableDependencyMissing() {
        DecisionTableFactory.setDecisionTableProvider(null);
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        ACME_CONTEXT,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/candrink/CanDrink.xls")));
        assertThrows(MissingDecisionTableDependencyError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generateGrafanaDashboards(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder
                .withAddonsConfig(AddonsConfig.builder()
                        .withPrometheusMonitoring(true)
                        .withMonitoring(true)
                        .build())
                .build();

        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        context,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/unit/RuleUnitQuery.drl")));
        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertEquals(2, generatedFiles.stream().filter(x -> x.type().equals(DashboardGeneratedFileUtils.DASHBOARD_TYPE)).count());
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void elapsedTimeMonitoringIsWrappingEveryMethod(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder
                .withAddonsConfig(AddonsConfig.builder()
                        .withPrometheusMonitoring(true)
                        .withMonitoring(true)
                        .build())
                .build();

        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        context,
                        CollectedResourceProducer.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/unit/RuleUnitQuery.drl")));
        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();

        List<String> endpointClasses = generatedFiles
                .stream()
                .filter(x -> x.relativePath().contains("Endpoint"))
                .map(x -> new String(x.contents()))
                .collect(Collectors.toList());

        for (String endpointClass : endpointClasses) {
            assertMonitoringEndpoints(endpointClass);
        }
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

    private static void assertRules(int expectedRules, int expectedPackages, int expectedUnits, int actualGeneratedFiles) {
        assertEquals(expectedRules +
                expectedPackages * 2 + // package descriptor for rules + package metadata 
                expectedUnits * 3, // ruleUnit + ruleUnit instance + unit model
                actualGeneratedFiles);
    }

    private static void assertRules(int expectedRules, int expectedPackages, int actualGeneratedFiles) {
        assertEquals(expectedRules +
                expectedPackages * 2, // package descriptor for rules + package metadata
                actualGeneratedFiles - 2); // ignore ProjectModel and ProjectRuntime classes
    }
}
