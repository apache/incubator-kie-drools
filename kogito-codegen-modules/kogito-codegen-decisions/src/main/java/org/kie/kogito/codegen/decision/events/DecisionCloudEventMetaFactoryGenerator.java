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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String CLASS_NAME = "DecisionCloudEventMetaFactory";

    private final List<DMNModel> models;

    public DecisionCloudEventMetaFactoryGenerator(KogitoBuildContext context, List<DMNModel> models) {
        super(buildTemplatedGenerator(context, CLASS_NAME), context);
        this.models = models;
    }

    @Override
    protected DecisionCloudEventMetaBuilder getCloudEventMetaBuilder() {
        return new DecisionCloudEventMetaBuilder();
    }

    public String generate() {
        CompilationUnit compilationUnit = generator.compilationUnitOrThrow("Cannot generate CloudEventMetaFactory");

        ClassOrInterfaceDeclaration classDefinition = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(generator, "Compilation unit doesn't contain a class or interface declaration!"));

        MethodDeclaration templatedBuildMethod = classDefinition
                .findFirst(MethodDeclaration.class, x -> x.getName().toString().startsWith("buildCloudEventMeta_$methodName$"))
                .orElseThrow(() -> new InvalidTemplateException(generator, "Impossible to find expected buildCloudEventMeta_ method"));

        Set<DecisionCloudEventMeta> methodDataList = this.getCloudEventMetaBuilder().build(models);

        methodDataList.forEach(methodData -> {
            MethodDeclaration builderMethod = templatedBuildMethod.clone();

            String methodNameValue = String.format("%s_%s", methodData.getKind().name(), methodData.methodNameChunk);
            String builderMethodName = getBuilderMethodName(classDefinition, templatedBuildMethod.getNameAsString(), methodNameValue);
            builderMethod.setName(builderMethodName);

            Map<String, Expression> expressions = new HashMap<>();
            expressions.put("$type$", new StringLiteralExpr(methodData.getType()));
            expressions.put("$source$", new StringLiteralExpr(methodData.getSource()));
            expressions.put("$kind$", new FieldAccessExpr(new NameExpr(new SimpleName(EventKind.class.getName())), methodData.getKind().name()));

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
}
