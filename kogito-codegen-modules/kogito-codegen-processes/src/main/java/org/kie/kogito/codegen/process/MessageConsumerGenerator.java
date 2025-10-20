/*
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
package org.kie.kogito.codegen.process;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.node.StartNode;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.kogito.codegen.core.CodegenUtils.interpolateTypes;
import static org.kie.kogito.codegen.core.CodegenUtils.isApplicationField;
import static org.kie.kogito.codegen.core.CodegenUtils.isObjectMapperField;
import static org.kie.kogito.codegen.core.CodegenUtils.isProcessField;
import static org.kie.kogito.internal.utils.ConversionUtils.sanitizeClassName;

public class MessageConsumerGenerator {

    private static final String OBJECT_MAPPER_CANONICAL_NAME = ObjectMapper.class.getCanonicalName();
    private final TemplatedGenerator generator;

    private KogitoBuildContext context;
    private final String processPackageName;
    private final String resourceClazzName;
    private final String processClazzName;
    private String processId;
    private String dataClazzName;
    private final String processName;
    private final String appCanonicalName;
    private final TriggerMetaData trigger;
    private CompilationUnit clazz;

    public MessageConsumerGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName,
            TriggerMetaData trigger) {
        this.context = context;
        this.trigger = trigger;
        this.processPackageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        this.resourceClazzName = sanitizeClassName(processName) + "MessageConsumer_" + trigger.getOwnerId();
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
        this.appCanonicalName = appCanonicalName;
        this.generator = TemplatedGenerator.builder()
                .withTargetTypeName(resourceClazzName)
                .withPackageName(processPackageName)
                .build(context, "MessageConsumer");
        this.clazz = generator.compilationUnitOrThrow("Cannot generate message consumer");
        clazz.addImport(modelfqcn);
    }

    public String className() {
        return resourceClazzName;
    }

    public CompilationUnit compilationUnit() {
        return clazz;
    }

    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public String generate() {
        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        generator,
                        "Cannot find class declaration"));

        template.setName(resourceClazzName);
        template.findAll(ConstructorDeclaration.class).forEach(cd -> cd.setName(resourceClazzName));
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, dataClazzName));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$ProcessName$", processName)));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger.getName())));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$ChannelName$", trigger.getChannelName())));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataType$", trigger.getDataType())));
        template.findAll(MethodCallExpr.class).forEach(this::interpolateStrings);

        generateModelMethods(template);

        // legacy: force initialize fields
        if (!context.hasDI()) {
            template.findAll(FieldDeclaration.class,
                    fd -> isProcessField(fd)).forEach(fd -> initializeProcessField(fd));
            template.findAll(FieldDeclaration.class,
                    fd -> isApplicationField(fd)).forEach(fd -> initializeApplicationField(fd));
            template.findAll(FieldDeclaration.class,
                    fd -> isObjectMapperField(fd)).forEach(fd -> initializeObjectMapperField(fd));
        }

        template.findAll(FieldDeclaration.class, f -> isCorrelationField(f)).stream().findFirst().ifPresent(f -> initializeCorrelationField(f));

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    public static boolean isCorrelationField(FieldDeclaration fd) {
        return fd.getVariable(0).getNameAsString().equals("correlation");//todo
    }

    private void generateModelMethods(ClassOrInterfaceDeclaration template) {
        //generate setter call on eventToModel method
        template.findAll(MethodCallExpr.class)
                .forEach(t -> {
                    String name = (String) trigger.getNode().getMetaData().get(Metadata.MAPPING_VARIABLE);
                    name = Optional.ofNullable(name).orElseGet(() -> trigger.getModelRef());
                    t.setName(t.getNameAsString().replace("$SetModelMethodName$", "set" + StringUtils.ucFirst(name)));
                });

        if (!(trigger.getNode() instanceof StartNode)) {
            template.findAll(MethodDeclaration.class, m -> m.getName().getIdentifier().equals("getModelConverter")).stream().findFirst().ifPresent(template::remove);
        }

        if (!trigger.dataOnly()) {
            ClassOrInterfaceType dataTypeClass = parseClassOrInterfaceType(trigger.getDataType());
            template.addMethod("getDataResolver", Keyword.PROTECTED).addAnnotation(Override.class)
                    .setType(parseClassOrInterfaceType(Function.class.getCanonicalName()).setTypeArguments(
                            NodeList.nodeList(parseClassOrInterfaceType(DataEvent.class.getCanonicalName()).setTypeArguments(NodeList.nodeList(dataTypeClass)), dataTypeClass)))
                    .setBody(new BlockStmt().addStatement(new ReturnStmt(
                            new MethodReferenceExpr(new NameExpr(CloudEventUtils.class.getCanonicalName()), NodeList.nodeList(), "fromValue"))));
        }
    }

    private void initializeCorrelationField(FieldDeclaration fd) {
        if (Objects.isNull(trigger.getCorrelation())) {
            return;
        }
        NodeList<Expression> arguments = new NodeList<>(trigger.getCorrelation().getValue().stream()
                .map(Correlation::getKey)
                .map(f -> new StringLiteralExpr(f))
                .collect(Collectors.toSet()));
        MethodCallExpr setOf =
                new MethodCallExpr(new NameExpr(Set.class.getCanonicalName()), "of").setArguments(arguments);
        fd.getVariable(0).setInitializer(setOf);
    }

    private void initializeProcessField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(processClazzName));
    }

    private void initializeApplicationField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }

    private void initializeObjectMapperField(FieldDeclaration fd) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(OBJECT_MAPPER_CANONICAL_NAME));
    }

    private void interpolateStrings(MethodCallExpr vv) {
        String s = vv.getNameAsString();
        String interpolated =
                s.replace("$DataType$", StringUtils.ucFirst(trigger.getModelRef()));
        vv.setName(interpolated);
    }
}
