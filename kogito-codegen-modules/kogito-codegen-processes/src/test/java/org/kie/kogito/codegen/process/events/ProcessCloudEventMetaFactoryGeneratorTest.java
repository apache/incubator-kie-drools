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
package org.kie.kogito.codegen.process.events;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.kogito.codegen.core.events.AbstractCloudEventMetaFactoryGenerator.buildTemplatedGenerator;
import static org.kie.kogito.codegen.core.events.AbstractCloudEventMetaFactoryGenerator.getBuilderMethodName;
import static org.kie.kogito.codegen.core.events.AbstractCloudEventMetaFactoryGenerator.toValidJavaIdentifier;

class ProcessCloudEventMetaFactoryGeneratorTest {

    @Test
    void testTemplateIsValid() {
        TemplatedGenerator generator = buildTemplatedGenerator(getContext(true), ProcessCloudEventMetaFactoryGenerator.CLASS_NAME);

        CompilationUnit compilationUnit = generator.compilationUnit()
                .orElseGet(() -> fail("Cannot generate ProcessCloudEventMetaFactory"));

        ClassOrInterfaceDeclaration classDefinition = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseGet(() -> fail("Compilation unit doesn't contain a class or interface declaration!"));

        MethodDeclaration methodDeclaration = classDefinition
                .findFirst(MethodDeclaration.class, x -> x.getName().toString().startsWith("buildCloudEventMeta_"))
                .orElseGet(() -> fail("Impossible to find expected buildCloudEventMeta_ method"));

        if (!methodDeclaration.getName().toString().contains("$methodName$")) {
            fail("Missing $methodName$ placeholder in templated build method declaration");
        }

        List<ReturnStmt> returnStmtList = methodDeclaration.findAll(ReturnStmt.class);
        if (returnStmtList.size() != 1) {
            fail("Templated build method declaration must contain exactly one return statement");
        }

        Optional<ObjectCreationExpr> optObjectCreationExprExpr = returnStmtList.get(0).getExpression()
                .filter(Expression::isObjectCreationExpr)
                .map(Expression::asObjectCreationExpr)
                .filter(ocExpr -> {
                    String typeName = ocExpr.getType().getNameAsString();
                    return typeName.equals(CloudEventMeta.class.getSimpleName()) || typeName.equals(CloudEventMeta.class.getName());
                })
                .filter(ocExpr -> ocExpr.getArguments().size() == 3)
                .filter(ocExpr -> ocExpr.getArguments().get(0).toString().equals("$type$")
                        && ocExpr.getArguments().get(1).toString().equals("$source$")
                        && ocExpr.getArguments().get(2).toString().equals("$kind$"));

        if (optObjectCreationExprExpr.isEmpty()) {
            org.assertj.core.api.Assertions.fail("", "Templated build method declaration return statement must be an ObjectCreationExpr of type CloudEventMeta" +
                    " with three placeholder arguments ($type$, $source$, $kind$)");
        }
    }

    @Test
    void testGetBuilderMethodName() {
        String testSource = "" +
                "class ProcessCloudEventMetaFactory {\n" +
                "    public CloudEventMeta buildCloudEventMeta_CONSUMED_first() {\n" +
                "        return new CloudEventMeta(\"first\", \"\", org.kie.kogito.event.EventKind.CONSUMED);\n" +
                "    }\n" +
                "    public CloudEventMeta buildCloudEventMeta_CONSUMED_second() {\n" +
                "        return new CloudEventMeta(\"second\", \"\", org.kie.kogito.event.EventKind.CONSUMED);\n" +
                "    }" +
                "}";

        ClassOrInterfaceDeclaration testClassDefinition = StaticJavaParser.parse(testSource).findFirst(ClassOrInterfaceDeclaration.class)
                .orElseGet(() -> fail("Test source doesn't contain a class or interface declaration!"));

        String templatedBuildMethodName = "buildCloudEventMeta_$methodName$";

        assertThat(getBuilderMethodName(testClassDefinition, templatedBuildMethodName, "PRODUCED_first")).isEqualTo("buildCloudEventMeta_PRODUCED_first");
        assertThat(getBuilderMethodName(testClassDefinition, templatedBuildMethodName, "CONSUMED_first")).isEqualTo("buildCloudEventMeta_CONSUMED_first_1");
        assertThat(getBuilderMethodName(testClassDefinition, templatedBuildMethodName, "CONSUMED_third")).isEqualTo("buildCloudEventMeta_CONSUMED_third");
    }

    @Test
    void testToValidJavaIdentifier() {
        assertThat(toValidJavaIdentifier("simpleName")).isEqualTo("simpleName");
        assertThat(toValidJavaIdentifier("more%Com_plex/Name!")).isEqualTo("more_37Com__plex_47Name_33");
    }

    @Test
    void verifyProcessWithMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateCatchEventMessage.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getMethods()).hasSize(1);
        assertReturnExpressionContains(clazz.getMethods().get(0), "customers", EventKind.CONSUMED);
    }

    @Test
    void verifyProcessWithStartAndEndMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messagestartevent/MessageStartAndEndEvent.bpmn2", 2, true);
        assertThat(clazz).isNotNull();
        assertThat(clazz.getMethods()).hasSize(2);
        List<MethodDeclaration> methods = clazz.getMethods().stream().sorted(Comparator.comparing(MethodDeclaration::getNameAsString)).collect(Collectors.toList());
        assertReturnExpressionContains(methods.get(0), "customers", EventKind.CONSUMED);
        assertReturnExpressionContains(methods.get(1), "process.messagestartevent.processedcustomers", EventKind.PRODUCED);
    }

    @Test
    void verifyProcessWithIntermediateThrowEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateThrowEventMessage.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getMethods()).hasSize(1);
        assertReturnExpressionContains(clazz.getMethods().get(0), "process.messageintermediateevent.customers", EventKind.PRODUCED);
    }

    @Test
    void verifyProcessWithBoundaryEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/BoundaryMessageEventOnTask.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getMethods()).hasSize(1);
        assertReturnExpressionContains(clazz.getMethods().get(0), "customers", EventKind.CONSUMED);
    }

    @Test
    void verifyProcessWithoutMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/usertask/approval.bpmn2", 0, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getMethods()).isEmpty();
    }

    private void assertReturnExpressionContains(MethodDeclaration method, String expectedType, EventKind expectedKind) {
        Optional<String> optExpr = method.getBody()
                .map(BlockStmt::getStatements)
                .filter(stmtList -> stmtList.size() == 1)
                .map(stmtList -> stmtList.get(0))
                .filter(Statement::isReturnStmt)
                .map(Statement::asReturnStmt)
                .flatMap(ReturnStmt::getExpression)
                .map(Expression::toString);

        assertThat(optExpr.filter(str -> str.contains(String.format("\"%s\"", expectedType))))
                .withFailMessage(() -> String.format("Method %s doesn't contain \"%s\" as event type", method.getName(), expectedType)).isPresent();
        assertThat(optExpr.filter(str -> str.contains(String.format("%s.%s", EventKind.class.getName(), expectedKind.name()))))
                .withFailMessage(() -> String.format("Method %s doesn't contain %s as event kind", method.getName(), expectedKind.name())).isPresent();
    }

    private ClassOrInterfaceDeclaration generateAndParseClass(String bpmnFile, int expectedTriggers, boolean withInjection) {
        KogitoBuildContext context = getContext(withInjection);
        List<ProcessExecutableModelGenerator> execModelGenerators = ProcessGenerationUtils.execModelFromProcessFile(bpmnFile);

        final ProcessCloudEventMetaFactoryGenerator generator =
                new ProcessCloudEventMetaFactoryGenerator(
                        context,
                        execModelGenerators);

        Collection<ProcessCloudEventMeta> ces = generator.getCloudEventMetaBuilder().build(execModelGenerators);
        if (expectedTriggers > 0) {
            assertThat(ces).hasSize(expectedTriggers);
        } else {
            assertThat(ces).isEmpty();
        }
        final String source = generator.generate();
        assertThat(source).isNotNull();
        final ClassOrInterfaceDeclaration clazz = StaticJavaParser
                .parse(source)
                .getClassByName(generator.getClassName())
                .orElseThrow(() -> new IllegalArgumentException("Class does not exists"));
        return clazz;
    }

    private KogitoBuildContext getContext(boolean withInjection) {
        return (withInjection ? QuarkusKogitoBuildContext.builder() : JavaKogitoBuildContext.builder())
                .withAddonsConfig(AddonsConfig.builder().withCloudEvents(true).build())
                .build();
    }
}
