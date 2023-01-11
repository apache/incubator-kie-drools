/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.drools.io.FileSystemResource;
import org.jbpm.compiler.canonical.ProcessToExecModelGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.process.Process;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessResourceGeneratorTest {

    private static final List<String> JAVA_AND_QUARKUS_REST_ANNOTATIONS = List.of("DELETE", "GET", "POST");
    private static final List<String> SPRING_BOOT_REST_ANNOTATIONS = List.of("DeleteMapping", "GetMapping", "PostMapping");

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void testGenerateProcessWithDocumentation(KogitoBuildContext.Builder contextBuilder) {
        String fileName = "src/test/resources/ProcessWithDocumentation.bpmn";
        String expectedSummary = "This is the documentation";
        String expectedDescription = "This is the process instance description";

        testOpenApiDocumentation(contextBuilder, fileName, expectedSummary, expectedDescription);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void testGenerateProcessWithoutDocumentation(KogitoBuildContext.Builder contextBuilder) {
        String fileName = "src/test/resources/ProcessWithoutDocumentation.bpmn";
        String expectedSummary = "ProcessWithoutDocumentation";
        String expectedDescription = "";

        testOpenApiDocumentation(contextBuilder, fileName, expectedSummary, expectedDescription);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void testGenerateBoundarySignalEventOnTask(KogitoBuildContext.Builder contextBuilder) {
        String fileName = "src/test/resources/signalevent/BoundarySignalEventOnTask.bpmn2";

        ClassOrInterfaceDeclaration classDeclaration = getResourceClassDeclaration(contextBuilder, fileName);

        KogitoWorkflowProcess process = parseProcess(fileName);

        String outputType = new ModelClassGenerator(contextBuilder.build(), process).simpleName() + "Output";

        classDeclaration.getMethods().stream()
                .filter(method -> isRestMethod(method) && method.getNameAsString().startsWith("signal_"))
                .forEach(method -> assertMethodOutputModelType(method, outputType));
    }

    void testOpenApiDocumentation(KogitoBuildContext.Builder contextBuilder, String fileName, String expectedSummary, String expectedDescription) {
        ClassOrInterfaceDeclaration classDeclaration = getResourceClassDeclaration(contextBuilder, fileName);

        classDeclaration.getMethods().stream()
                .filter(this::isRestMethod)
                .forEach(method -> assertThatMethodHasOpenApiDocumentation(method, expectedSummary, expectedDescription));
    }

    private ClassOrInterfaceDeclaration getResourceClassDeclaration(KogitoBuildContext.Builder contextBuilder, String fileName) {
        KogitoWorkflowProcess process = parseProcess(fileName);
        CompilationUnit compilationUnit = getCompilationUnit(contextBuilder, process);
        Optional<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit.getClassByName(process.getId() + "Resource");
        assertThat(classDeclaration).isNotEmpty();
        return classDeclaration.orElseThrow();
    }

    private CompilationUnit getCompilationUnit(KogitoBuildContext.Builder contextBuilder, KogitoWorkflowProcess process) {
        KogitoBuildContext context = createContext(contextBuilder);

        ProcessExecutableModelGenerator execModelGen =
                new ProcessExecutableModelGenerator(process, new ProcessToExecModelGenerator(context.getClassLoader()));

        KogitoWorkflowProcess workFlowProcess = execModelGen.process();

        ProcessResourceGenerator processResourceGenerator = new ProcessResourceGenerator(
                context,
                workFlowProcess,
                new ModelClassGenerator(context, workFlowProcess).className(),
                execModelGen.className(),
                context.getPackageName() + ".Application");

        processResourceGenerator
                .withSignals(execModelGen.generate().getSignals());

        return StaticJavaParser.parse(processResourceGenerator.generate());
    }

    private void assertThatMethodHasOpenApiDocumentation(MethodDeclaration method, String summary, String description) {
        Optional<AnnotationExpr> annotation = method.getAnnotationByName("Operation");
        assertThat(annotation).isNotEmpty();
        assertThatAnnotationHasSummaryAndDescription(annotation.orElseThrow(), summary, description);
    }

    private void assertMethodOutputModelType(MethodDeclaration method, String outputType) {
        assertThat(method.getType()).isNotNull();
        assertThat(method.getType().asString()).isEqualTo(outputType);
    }

    private void assertThatAnnotationHasSummaryAndDescription(AnnotationExpr annotation, String summary, String description) {
        NodeList<MemberValuePair> pairs = ((NormalAnnotationExpr) annotation).getPairs();

        assertThat(pairs).containsExactlyInAnyOrder(
                new MemberValuePair("summary", new StringLiteralExpr(summary)),
                new MemberValuePair("description", new StringLiteralExpr(description)));
    }

    private boolean isRestMethod(MethodDeclaration method) {
        return method.getAnnotations().stream()
                .anyMatch(isOpenApiAnnotation().or(isSwaggerAnnotation()));
    }

    private Predicate<AnnotationExpr> isOpenApiAnnotation() {
        return annotation -> JAVA_AND_QUARKUS_REST_ANNOTATIONS.contains(annotation.getNameAsString());
    }

    private Predicate<AnnotationExpr> isSwaggerAnnotation() {
        return annotation -> SPRING_BOOT_REST_ANNOTATIONS.contains(annotation.getNameAsString());
    }

    private KogitoWorkflowProcess parseProcess(String fileName) {
        Collection<Process> processes = ProcessCodegen.parseProcessFile(new FileSystemResource(new File(fileName)));
        assertThat(processes).hasSize(1);
        Process process = processes.stream().findAny().orElseThrow();
        assertThat(process).isInstanceOf(KogitoWorkflowProcess.class);
        return (KogitoWorkflowProcess) process;
    }

    private KogitoBuildContext createContext(KogitoBuildContext.Builder contextBuilder) {
        AddonsConfig addonsConfig = AddonsConfig.builder()
                .withMonitoring(false)
                .withPrometheusMonitoring(false)
                .build();

        return contextBuilder.withAddonsConfig(addonsConfig).build();
    }
}
