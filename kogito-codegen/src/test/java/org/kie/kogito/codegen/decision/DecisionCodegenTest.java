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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratorContext;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.io.CollectedResource;
import org.kie.kogito.grafana.JGrafana;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DecisionCodegenTest {

    @Test
    public void generateAllFiles() throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/models/vacationDays");

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(6);
        assertThat(fileNames(generatedFiles)).containsAll(Arrays.asList("decision/InputSet.java",
                                                                        "decision/OutputSet.java",
                                                                        "decision/TEmployee.java",
                                                                        "decision/TAddress.java",
                                                                        "decision/TPayroll.java",
                                                                        "decision/VacationsResource.java",
                                                                        "org/kie/kogito/app/DecisionModelResourcesProvider.java"));

        ClassOrInterfaceDeclaration classDeclaration = codeGenerator.section().classDeclaration();
        assertNotNull(classDeclaration);
    }

    public DecisionCodegen getDecisionCodegen(String s) {
        GeneratorContext context = stronglyTypedContext();
        DecisionCodegen codeGenerator = DecisionCodegen.ofCollectedResources(CollectedResource.fromPaths(Paths.get(s).toAbsolutePath()));
        codeGenerator.setContext(context);
        return codeGenerator;
    }

    private GeneratorContext stronglyTypedContext() {
        Properties properties = new Properties();
        properties.put(DecisionCodegen.STRONGLY_TYPED_CONFIGURATION_KEY, Boolean.TRUE.toString());
        return GeneratorContext.ofProperties(properties);
    }

    private List<String> fileNames(List<GeneratedFile> generatedFiles) {
        return generatedFiles.stream().map(GeneratedFile::relativePath).collect(Collectors.toList());
    }

    @Test
    public void doNotGenerateTypesafeInfo() throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/alltypes/");

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(3);
        assertThat(fileNames(generatedFiles)).containsAll(Arrays.asList("http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/InputSet.java",
                                                                        "http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/OutputSet.java",
                                                                        "http_58_47_47www_46trisotech_46com_47definitions_47__4f5608e9_454d74_454c22_45a47e_45ab657257fc9c/OneOfEachTypeResource.java",
                                                                        "org/kie/kogito/app/DecisionModelResourcesProvider.java"));

        ClassOrInterfaceDeclaration classDeclaration = codeGenerator.section().classDeclaration();
        assertNotNull(classDeclaration);
    }

    @Test
    public void givenADMNModelWhenMonitoringIsActiveThenGrafanaDashboardsAreGenerated() throws Exception {
        List<GeneratedFile> dashboards = generateTestDashboards(new AddonsConfig().withPrometheusMonitoring(true));

        JGrafana vacationOperationalDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("operational-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(6, vacationOperationalDashboard.getDashboard().panels.size());
        assertEquals(0, vacationOperationalDashboard.getDashboard().links.size());

        JGrafana vacationDomainDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("domain-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationDomainDashboard.getDashboard().panels.size());
        assertEquals(0, vacationDomainDashboard.getDashboard().links.size());
    }

    @Test
    public void givenADMNModelWhenMonitoringAndTracingAreActiveThenTheGrafanaDashboardsContainsTheAuditUILink() throws Exception {
        List<GeneratedFile> dashboards = generateTestDashboards(new AddonsConfig().withPrometheusMonitoring(true).withTracing(true));

        JGrafana vacationOperationalDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("operational-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationOperationalDashboard.getDashboard().links.size());

        JGrafana vacationDomainDashboard = JGrafana.parse(new String(dashboards.stream().filter(x -> x.relativePath().contains("domain-dashboard-Vacations.json")).findFirst().get().contents()));

        assertEquals(1, vacationDomainDashboard.getDashboard().links.size());
    }

    @Test
    public void resilientToDuplicateDMNIDs() throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-test20200507");

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles.size()).isGreaterThanOrEqualTo(3);

        ClassOrInterfaceDeclaration classDeclaration = codeGenerator.section().classDeclaration();
        assertNotNull(classDeclaration);
    }

    @Test
    public void emptyName() throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-empty-name");
        RuntimeException re = Assertions.assertThrows(RuntimeException.class, () -> {
            codeGenerator.generate();
        });
        assertEquals("Model name should not be empty", re.getMessage());
    }

    private List<GeneratedFile> generateTestDashboards(AddonsConfig addonsConfig) throws IOException {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision/models/vacationDays")
                .withAddons(addonsConfig);

        List<GeneratedFile> generatedFiles = codeGenerator.generate();

        List<GeneratedFile> dashboards = generatedFiles.stream().filter(x -> x.getType() == GeneratedFile.Type.RESOURCE).collect(Collectors.toList());

        assertEquals(2, dashboards.size());

        List<GeneratedFile> staticDashboards = generatedFiles.stream().filter(x -> x.getType() == GeneratedFile.Type.GENERATED_CP_RESOURCE && x.relativePath().contains("dashboard")).collect(Collectors.toList());

        assertEquals(2, staticDashboards.size());

        return dashboards;
    }

    @Test
    public void testNSEW_positive() throws Exception {
        // This test is meant to check that IFF Eclipse MP OpenAPI annotations are available on Build/CP of Kogito application, annotation is used with codegen
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-NSEW");
        codeGenerator.withClassLoader(new ClassLoader() {
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return Object.class;
            }
        });

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).anyMatch(x -> x.relativePath().endsWith("InputSet.java"));
        GeneratedFile inputSetFile = generatedFiles.stream().filter(x -> x.relativePath().endsWith("InputSet.java")).findFirst().get();
        assertThat(new String(inputSetFile.contents())).containsPattern("@org\\.eclipse\\.microprofile\\.openapi\\.annotations\\.media\\.Schema\\(.*enumeration");
    }

    @Test
    public void testNSEW_negative() throws Exception {
        DecisionCodegen codeGenerator = getDecisionCodegen("src/test/resources/decision-NSEW");

        List<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).anyMatch(x -> x.relativePath().endsWith("InputSet.java"));
        GeneratedFile inputSetFile = generatedFiles.stream().filter(x -> x.relativePath().endsWith("InputSet.java")).findFirst().get();
        assertThat(new String(inputSetFile.contents())).doesNotContain("@org.eclipse.microprofile.openapi.annotations.media.Schema");
    }
}
