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
package org.drools.model.codegen.project;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.context.JavaDroolsModelBuildContext;
import org.drools.codegen.common.context.QuarkusDroolsModelBuildContext;
import org.drools.codegen.common.context.SpringBootDroolsModelBuildContext;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.drl.extensions.DecisionTableProvider;
import org.drools.io.FileSystemResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleCodegenTest {

    public static Stream<Arguments> contextBuilders() {
        return Stream.of(
                Arguments.of(JavaDroolsModelBuildContext.builder()),
                Arguments.of(QuarkusDroolsModelBuildContext.builder()),
                Arguments.of(SpringBootDroolsModelBuildContext.builder()));
    }

    private static final String RESOURCE_PATH = "src/test/resources";

    @BeforeEach
    public void setup() {
        DecisionTableFactory.setDecisionTableProvider(KieService.load(DecisionTableProvider.class));
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.project.RuleCodegenTest#contextBuilders")
    public void isEmpty(DroolsModelBuildContext.Builder contextBuilder) {
        withLegacyApi(contextBuilder);

        DroolsModelBuildContext context = contextBuilder.build();
        RuleCodegen emptyCodeGenerator = RuleCodegen.ofResources(context, Collections.emptyList());

        assertThat(emptyCodeGenerator.isEmpty()).isTrue();
        assertThat(emptyCodeGenerator.isEnabled()).isFalse();

        Collection<GeneratedFile> emptyGeneratedFiles = emptyCodeGenerator.generate();
        assertThat(emptyGeneratedFiles.size()).isEqualTo(0);

        RuleCodegen codeGenerator = getRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/model/project/codegen/pkg1/file1.drl"));

        assertThat(codeGenerator.isEmpty()).isFalse();
        assertThat(codeGenerator.isEnabled()).isTrue();

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.project.RuleCodegenTest#contextBuilders")
    public void generateSingleFile(DroolsModelBuildContext.Builder contextBuilder) {
        withLegacyApi(contextBuilder);

        RuleCodegen ruleCodegen = getRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/model/project/codegen/pkg1/file1.drl"));

        Collection<GeneratedFile> generatedFiles = ruleCodegen.withHotReloadMode().generate();
        assertHasLegacyApiFiles(generatedFiles);
        assertRules(5, 1, generatedFiles.size());
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.project.RuleCodegenTest#contextBuilders")
    public void generateSinglePackage(DroolsModelBuildContext.Builder contextBuilder) {
        withLegacyApi(contextBuilder);

        RuleCodegen ruleCodegen = getRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/model/project/codegen/pkg1").listFiles());

        Collection<GeneratedFile> generatedFiles = ruleCodegen.withHotReloadMode().generate();
        assertHasLegacyApiFiles(generatedFiles);
        assertRules(7, 1, generatedFiles.size());
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.project.RuleCodegenTest#contextBuilders")
    public void generateSingleDtable(DroolsModelBuildContext.Builder contextBuilder) {
        withLegacyApi(contextBuilder);

        RuleCodegen ruleCodegen = getRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/candrink/CanDrink.drl.xls"));

        Collection<GeneratedFile> generatedFiles = ruleCodegen.withHotReloadMode().generate();
        assertHasLegacyApiFiles(generatedFiles);
        int externalizedLambda = 5;
        int legacyApiFiles = 2;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda - legacyApiFiles);
    }

    @ParameterizedTest
    @MethodSource("org.drools.model.codegen.project.RuleCodegenTest#contextBuilders")
    public void generateCepRule(DroolsModelBuildContext.Builder contextBuilder) {
        withLegacyApi(contextBuilder);

        RuleCodegen ruleCodegen = getRuleCodegenFromFiles(
                contextBuilder,
                new File(RESOURCE_PATH + "/org/drools/simple/cep/cep.drl"));

        Collection<GeneratedFile> generatedFiles = ruleCodegen.withHotReloadMode().generate();
        assertHasLegacyApiFiles(generatedFiles);
        int externalizedLambda = 2;
        int legacyApiFiles = 2;
        assertRules(2, 1, generatedFiles.size() - externalizedLambda - legacyApiFiles);
    }


    private void assertHasLegacyApiFiles(Collection<GeneratedFile> generatedFiles) {
        assertThat(generatedFiles.stream()).anyMatch(f -> f.relativePath().endsWith("/ProjectModel.java"));
        assertThat(generatedFiles.stream()).anyMatch(f -> f.relativePath().endsWith("/ProjectRuntime.java"));
    }

    private RuleCodegen getRuleCodegenFromFiles(DroolsModelBuildContext.Builder contextBuilder, File... resources) {
        return getRuleCodegenFromFiles(contextBuilder.build(), resources);
    }

    private RuleCodegen getRuleCodegenFromFiles(DroolsModelBuildContext context, File... resources) {
        return RuleCodegen.ofResources(context,
                fromFiles(Paths.get(RESOURCE_PATH), resources));
    }

    public static Collection<Resource> fromFiles(Path basePath, File... files) {
        Collection<Resource> resources = new ArrayList<>();
        try (Stream<File> paths = Arrays.stream(files)) {
            paths.filter(File::isFile)
                    .map(f -> {
                        Resource resource = new FileSystemResource(f);
                        resource.setResourceType(ResourceType.determineResourceType(f.getName()));
                        return resource;
                    })
                    .forEach(resources::add);
        }
        return resources;
    }

    static void withLegacyApi(DroolsModelBuildContext.Builder builder) {
        /* No-Op */
    }

    @Deprecated
    private static void assertRules(int expectedRules, int expectedPackages, int actualGeneratedFiles) {
        int expectedFiles = expectedRules +
                expectedPackages * 2; // package descriptor for rules + package metadata
        assertThat(actualGeneratedFiles).isEqualTo(expectedFiles);
    }
}
