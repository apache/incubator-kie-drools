/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DecisionTableProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.io.CollectedResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IncrementalRuleCodegenTest {

    @BeforeEach
    public void setup() {
        DecisionTableFactory.setDecisionTableProvider(ServiceRegistry.getInstance().get(DecisionTableProvider.class));
    }

    @Test
    public void generateSingleFile() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(Paths.get("src/test/resources"),
                                                    new File("src/test/resources/org/kie/kogito/codegen/rules/pkg1/file1.drl")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(3, 1, generatedFiles.size());
    }

    @Test
    public void generateSinglePackage() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/rules/pkg1").listFiles()));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(5, 1, generatedFiles.size());
    }

    @Test
    public void generateSinglePackageSingleUnit() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/rules/multiunit").listFiles()));
        incrementalRuleCodegen.setPackageName("org.kie.kogito.codegen.rules.multiunit");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(2, 1, 1, generatedFiles.size());
    }

    @Test
    public void generateDirectoryRecursively() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(CollectedResource.fromPaths(
                        Paths.get("src/test/resources/org/kie/kogito/codegen/rules")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(14, 5, 3, generatedFiles.size());
    }

    @Test
    public void generateSingleDtable() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/candrink/CanDrink.xls")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 5;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @Test
    public void generateSingleUnit() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(CollectedResource.fromPaths(
                        Paths.get("src/test/resources/org/kie/kogito/codegen/rules/myunit")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        assertRules(1, 1, 1, generatedFiles.size());
    }

    @Test
    public void generateCepRule() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/cep/cep.drl")));
        incrementalRuleCodegen.setPackageName("com.acme");

        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();
        int externalizedLambda = 2;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda);
    }

    @Test
    public void raiseErrorOnSyntaxError() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/broken.drl")));
        incrementalRuleCodegen.setPackageName("com.acme");
        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @Test
    public void raiseErrorOnBadOOPath() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/brokenrules/brokenunit/ABrokenUnit.drl")));
        incrementalRuleCodegen.setPackageName("com.acme");
        assertThrows(RuleCodegenError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @Test
    public void throwWhenDtableDependencyMissing() {
        DecisionTableFactory.setDecisionTableProvider(null);
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/drools/simple/candrink/CanDrink.xls")));
        incrementalRuleCodegen.setPackageName("com.acme");
        assertThrows(MissingDecisionTableDependencyError.class, incrementalRuleCodegen.withHotReloadMode()::generate);
    }

    @Test
    public void generateGrafanaDashboards() {
        IncrementalRuleCodegen incrementalRuleCodegen =
                IncrementalRuleCodegen.ofCollectedResources(
                        CollectedResource.fromFiles(
                                Paths.get("src/test/resources"),
                                new File("src/test/resources/org/kie/kogito/codegen/unit/RuleUnitQuery.drl")))
                        .withAddons(new AddonsConfig().withPrometheusMonitoring(true));
        incrementalRuleCodegen.setPackageName("com.acme");
        List<GeneratedFile> generatedFiles = incrementalRuleCodegen.withHotReloadMode().generate();

        assertEquals(2, generatedFiles.stream().filter(x -> x.getType() == GeneratedFile.Type.RESOURCE).count());
        assertEquals(2, generatedFiles.stream().filter(x -> x.getType() == GeneratedFile.Type.GENERATED_CP_RESOURCE && x.relativePath().contains("dashboard")).count());
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
