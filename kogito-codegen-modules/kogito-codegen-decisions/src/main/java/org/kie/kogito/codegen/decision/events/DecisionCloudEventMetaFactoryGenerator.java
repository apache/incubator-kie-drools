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

package org.kie.kogito.codegen.decision.events;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.core.CodegenUtils;
import org.kie.kogito.codegen.core.events.AbstractCloudEventMetaFactoryGenerator;
import org.kie.kogito.event.EventKind;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class DecisionCloudEventMetaFactoryGenerator extends AbstractCloudEventMetaFactoryGenerator {

    public static final String RESPONSE_EVENT_TYPE = "DecisionResponse";
    public static final String RESPONSE_FULL_EVENT_TYPE = "DecisionResponseFull";
    public static final String RESPONSE_ERROR_EVENT_TYPE = "DecisionResponseError";

    private static final String CLASS_NAME = "DecisionCloudEventMetaFactory";

    private final List<DMNModel> models;

    public DecisionCloudEventMetaFactoryGenerator(KogitoBuildContext context, List<DMNModel> models) {
        super(buildTemplatedGenerator(context, CLASS_NAME), context);
        this.models = models;
    }

    public String generate() {
        CompilationUnit compilationUnit = generator.compilationUnitOrThrow("Cannot generate CloudEventMetaFactory");

        ClassOrInterfaceDeclaration classDefinition = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(generator, "Compilation unit doesn't contain a class or interface declaration!"));

        MethodDeclaration templatedBuildMethod = classDefinition
                .findFirst(MethodDeclaration.class, x -> x.getName().toString().startsWith("buildCloudEventMeta_$methodName$"))
                .orElseThrow(() -> new InvalidTemplateException(generator, "Impossible to find expected buildCloudEventMeta_ method"));

        List<MethodData> methodDataList = models.stream()
                .flatMap(DecisionCloudEventMetaFactoryGenerator::buildMethodDataStreamFromModel)
                .distinct()
                .collect(Collectors.toList());

        methodDataList.forEach(methodData -> {
            MethodDeclaration builderMethod = templatedBuildMethod.clone();

            String methodNameValue = String.format("%s_%s", methodData.kind.name(), methodData.methodNameChunk);
            String builderMethodName = getBuilderMethodName(classDefinition, templatedBuildMethod.getNameAsString(), methodNameValue);
            builderMethod.setName(builderMethodName);

            Map<String, Expression> expressions = new HashMap<>();
            expressions.put("$type$", new StringLiteralExpr(methodData.type));
            expressions.put("$source$", new StringLiteralExpr(methodData.source));
            expressions.put("$kind$", new FieldAccessExpr(new NameExpr(new SimpleName(EventKind.class.getName())), methodData.kind.name()));

            builderMethod.findFirst(MethodCallExpr.class)
                    .ifPresent(callExpr -> CodegenUtils.interpolateArguments(callExpr, expressions));

            classDefinition.addMember(builderMethod);
        });

        templatedBuildMethod.remove();

        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withFactoryClass(classDefinition);
            classDefinition.findAll(FieldDeclaration.class, CodegenUtils::isConfigBeanField)
                    .forEach(fd -> context.getDependencyInjectionAnnotator().withInjection(fd));
            classDefinition.findAll(MethodDeclaration.class, x -> x.getName().toString().startsWith("buildCloudEventMeta_"))
                    .forEach(md -> context.getDependencyInjectionAnnotator().withFactoryMethod(md));
        }

        return compilationUnit.toString();
    }

    static Stream<MethodData> buildMethodDataStreamFromModel(DMNModel model) {
        String source = Optional.of(model.getName())
                .filter(s -> !s.isEmpty())
                .map(DecisionCloudEventMetaFactoryGenerator::urlEncodedStringFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse("");

        Stream<MethodData> modelStream = Stream.of(
                buildMethodDataFromModel(RESPONSE_EVENT_TYPE, source, model.getName()),
                buildMethodDataFromModel(RESPONSE_FULL_EVENT_TYPE, source, model.getName()),
                buildMethodDataFromModel(RESPONSE_ERROR_EVENT_TYPE, source, model.getName()));

        Stream<MethodData> decisionServiceStream = model.getDecisionServices().stream()
                .flatMap(ds -> buildMethodDataStreamFromDecisionService(model, ds.getName()));

        return Stream.concat(modelStream, decisionServiceStream);
    }

    static MethodData buildMethodDataFromModel(String type, String source, String modelName) {
        return new MethodData(type, source, EventKind.PRODUCED, buildMethodNameChunk(type, modelName, null));
    }

    static Stream<MethodData> buildMethodDataStreamFromDecisionService(DMNModel model, String decisionServiceName) {
        String source = Stream.of(model.getName(), decisionServiceName)
                .filter(s -> s != null && !s.isEmpty())
                .map(DecisionCloudEventMetaFactoryGenerator::urlEncodedStringFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("/"));

        return Stream.of(
                buildMethodDataFromDecisionService(RESPONSE_EVENT_TYPE, source, model.getName(), decisionServiceName),
                buildMethodDataFromDecisionService(RESPONSE_FULL_EVENT_TYPE, source, model.getName(), decisionServiceName),
                buildMethodDataFromDecisionService(RESPONSE_ERROR_EVENT_TYPE, source, model.getName(), decisionServiceName));
    }

    static MethodData buildMethodDataFromDecisionService(String type, String source, String modelName, String decisionServiceName) {
        return new MethodData(type, source, EventKind.PRODUCED, buildMethodNameChunk(type, modelName, decisionServiceName));
    }

    static String buildMethodNameChunk(String type, String modelName, String decisionServiceName) {
        return Stream.of(EventKind.PRODUCED.name(), type, modelName, decisionServiceName)
                .filter(s -> s != null && !s.isEmpty())
                .map(DecisionCloudEventMetaFactoryGenerator::toValidJavaIdentifier)
                .collect(Collectors.joining("_"));
    }

    static Optional<String> urlEncodedStringFrom(String input) {
        return Optional.ofNullable(input)
                .map(i -> {
                    try {
                        return URLEncoder.encode(i, StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static class MethodData {

        final String type;
        final String source;
        final EventKind kind;
        final String methodNameChunk;

        public MethodData(String type, String source, EventKind kind, String methodNameChunk) {
            this.type = type;
            this.source = source;
            this.kind = kind;
            this.methodNameChunk = methodNameChunk;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MethodData that = (MethodData) o;
            return type.equals(that.type) && source.equals(that.source) && kind == that.kind;
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, source, kind);
        }
    }
}
