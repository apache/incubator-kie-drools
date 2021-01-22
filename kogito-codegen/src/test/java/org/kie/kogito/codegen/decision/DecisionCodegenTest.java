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

package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationSection;
import org.kie.kogito.codegen.DashboardGeneratedFileUtils;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.grafana.JGrafana;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.codegen.KogitoBuildContextTestUtils.mockClassAvailabilityResolver;

public class DecisionCodegenTest {

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void generateAllFiles(KogitoBuildContext.Builder contextBuilder) throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/models/vacationDays", contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(6);
        assertThat(fileNames(generatedFiles)).containsAll(Arrays.asList("decision/InputSet.java",
                                                                        "decision/OutputSet.java",
                                                                        "decision/TEmployee.java",
                                                                        "decision/TAddress.java",
                                                                        "decision/TPayroll.java",
                                                                        "decision/VacationsResource.java",
                                                                        "org/kie/kogito/app/DecisionModelResourcesProvider.java"));

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertThat(optionalApplicationSection).isNotEmpty();
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit );
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void doNotGenerateTypesafeInfo(KogitoBuildContext.Builder contextBuilder) throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/alltypes/", contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(3);
        assertThat(fileNames(generatedFiles)).containsAll(Arrays.asList("http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/InputSet.java",
                                                                        "http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/OutputSet.java",
                                                                        "http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/OneOfEachTypeResource.java",
                                                                        "org/kie/kogito/app/DecisionModelResourcesProvider.java"));

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertThat(optionalApplicationSection).isNotEmpty();
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit );
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void givenADMNModelWhenMonitoringIsActiveThenGrafanaDashboardsAreGenerated(KogitoBuildContext.Builder contextBuilder) throws Exception {
        List<GeneratedFile> dashboards = generateTestDashboards(AddonsConfig.builder().withMonitoring(true).withPrometheusMonitoring(true).build(), contextBuilder);

        JGrafana vacationOperationalDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("operational-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(6, vacationOperationalDashboard.getDashboard().panels.size());
        assertEquals(0, vacationOperationalDashboard.getDashboard().links.size());

        JGrafana vacationDomainDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("domain-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationDomainDashboard.getDashboard().panels.size());
        assertEquals(0, vacationDomainDashboard.getDashboard().links.size());
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void givenADMNModelWhenMonitoringAndTracingAreActiveThenTheGrafanaDashboardsContainsTheAuditUILink(KogitoBuildContext.Builder contextBuilder) throws Exception {
        List<GeneratedFile> dashboards = generateTestDashboards(AddonsConfig.builder().withMonitoring(true).withPrometheusMonitoring(true).withTracing(true).build(), contextBuilder);

        JGrafana vacationOperationalDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("operational-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationOperationalDashboard.getDashboard().links.size());

        JGrafana vacationDomainDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("domain-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationDomainDashboard.getDashboard().links.size());
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void resilientToDuplicateDMNIDs(KogitoBuildContext.Builder contextBuilder) throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-test20200507", contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(3);

        Optional<ApplicationSection> optionalApplicationSection = codeGenerator.section();
        assertThat(optionalApplicationSection).isNotEmpty();
        CompilationUnit compilationUnit = optionalApplicationSection.get().compilationUnit();
        assertNotNull(compilationUnit );
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void emptyName(KogitoBuildContext.Builder contextBuilder) throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-empty-name", contextBuilder);
        RuntimeException re = Assertions.assertThrows(RuntimeException.class, codeGenerator::generate);
        assertEquals("Model name should not be empty", re.getMessage());
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void testNSEW_positive(KogitoBuildContext.Builder contextBuilder) throws Exception {
        contextBuilder
                .withClassAvailabilityResolver(mockClassAvailabilityResolver(Collections.singleton("org.eclipse.microprofile.openapi.models.OpenAPI"), Collections.emptyList()));
        // This test is meant to check that IFF Eclipse MP OpenAPI annotations are available on Build/CP of Kogito application, annotation is used with codegen
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-NSEW", contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).anyMatch(x -> x.relativePath().endsWith("InputSet.java"));
        GeneratedFile inputSetFile = generatedFiles.stream().filter(x -> x.relativePath().endsWith("InputSet.java")).findFirst().get();
        assertThat(new String(inputSetFile.contents())).containsPattern("@org\\.eclipse\\.microprofile\\.openapi\\.annotations\\.media\\.Schema\\(.*enumeration");
    }

    @ParameterizedTest
    @MethodSource("contextBuilders")
    public void testNSEW_negative(KogitoBuildContext.Builder contextBuilder) throws Exception {
        contextBuilder
                .withClassAvailabilityResolver(mockClassAvailabilityResolver(Collections.emptyList(), Collections.singleton("org.eclipse.microprofile.openapi.models.OpenAPI")));
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-NSEW", contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).anyMatch(x -> x.relativePath().endsWith("InputSet.java"));
        GeneratedFile inputSetFile = generatedFiles.stream().filter(x -> x.relativePath().endsWith("InputSet.java")).findFirst().get();
        assertThat(new String(inputSetFile.contents())).doesNotContain("@org.eclipse.microprofile.openapi.annotations.media.Schema");
    }

    static Stream<Arguments> contextBuilders() {
        return Stream.of(
                Arguments.of(JavaKogitoBuildContext.builder()),
                Arguments.of(QuarkusKogitoBuildContext.builder()),
                Arguments.of(SpringBootKogitoBuildContext.builder())
        );
    }

    private KogitoBuildContext.Builder stronglyTypedContext(KogitoBuildContext.Builder builder) {
        Properties properties = new Properties();
        properties.put(DecisionCodegen.STRONGLY_TYPED_CONFIGURATION_KEY, Boolean.TRUE.toString());
        builder.withApplicationProperties(properties);
        return builder;
    }

    protected DecisionCodegen getDecisionCodegen(String sourcePath, KogitoBuildContext.Builder contextBuilder) {
        return getDecisionCodegen(sourcePath, AddonsConfig.DEFAULT, contextBuilder);
    }

    protected DecisionCodegen getDecisionCodegen(String sourcePath, AddonsConfig addonsConfig, KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = stronglyTypedContext(contextBuilder)
                .withAddonsConfig(addonsConfig)
                .build();

        return DecisionCodegen.ofCollectedResources(context, CollectedResource.fromPaths(Paths.get(sourcePath).toAbsolutePath()));
    }

    private List<String> fileNames(List<GeneratedFile> generatedFiles) {
        return generatedFiles.stream().map(GeneratedFile::relativePath).collect(Collectors.toList());
    }

    private List<GeneratedFile> generateTestDashboards(AddonsConfig addonsConfig, KogitoBuildContext.Builder contextBuilder) {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/models/vacationDays", addonsConfig, contextBuilder);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();

        List<GeneratedFile> dashboards = generatedFiles.stream()
                .filter(x -> x.type().equals(DashboardGeneratedFileUtils.DASHBOARD_TYPE))
                .collect(Collectors.toList());

        assertEquals(2, dashboards.size());

        return dashboards;
    }
}
