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

package org.kie.kogito.codegen.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;
import static org.kie.kogito.codegen.CodegenUtils.isApplicationField;
import static org.kie.kogito.codegen.CodegenUtils.isObjectMapperField;
import static org.kie.kogito.codegen.CodegenUtils.isProcessField;

public class MessageConsumerGenerator {

    private static final String OBJECT_MAPPER_CANONICAL_NAME = ObjectMapper.class.getCanonicalName();
    private final TemplatedGenerator generator;

    private KogitoBuildContext context;
    private WorkflowProcess process;
    private final String processPackageName;
    private final String resourceClazzName;
    private final String processClazzName;
    private String processId;
    private String dataClazzName;
    private final String processName;
    private final String appCanonicalName;
    private final String messageDataEventClassName;

    private TriggerMetaData trigger;

    public MessageConsumerGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName,
            String messageDataEventClassName,
            TriggerMetaData trigger) {
        this.context = context;
        this.process = process;
        this.trigger = trigger;
        this.processPackageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String capitalizedProcessName = StringUtils.ucFirst(processName);
        this.resourceClazzName = capitalizedProcessName + "MessageConsumer_" + trigger.getOwnerId();
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
        this.appCanonicalName = appCanonicalName;
        this.messageDataEventClassName = messageDataEventClassName;

        this.generator = TemplatedGenerator.builder()
                .withTargetTypeName(resourceClazzName)
                .withPackageName(processPackageName)
                .build(context, "MessageConsumer");
    }

    public String className() {
        return resourceClazzName;
    }

    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnitOrThrow("Cannot generate message consumer");

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        generator,
                        "Cannot find class declaration"
                ));
        template.setName(resourceClazzName);
        template.findAll(ConstructorDeclaration.class).forEach(cd -> cd.setName(resourceClazzName));

        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, dataClazzName));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$ProcessName$", processName)));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger.getName())));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataEventType$", messageDataEventClassName)));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataType$", trigger.getDataType())));
        template.findAll(MethodCallExpr.class).forEach(this::interpolateStrings);

        // legacy: force initialize fields
        if (!context.hasDI()) {
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> initializeProcessField(fd));
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> initializeApplicationField(fd));
            template.findAll(FieldDeclaration.class,
                             fd -> isObjectMapperField(fd)).forEach(fd -> initializeObjectMapperField(fd));
        }
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
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
