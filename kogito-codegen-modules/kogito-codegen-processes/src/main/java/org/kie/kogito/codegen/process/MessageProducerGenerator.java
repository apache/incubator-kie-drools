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

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.codegen.core.CodegenUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static org.kie.kogito.internal.utils.ConversionUtils.sanitizeClassName;

public class MessageProducerGenerator {

    protected static final String EVENT_DATA_VAR = "eventData";

    protected final TemplatedGenerator generator;

    private final String processPackageName;
    protected final String resourceClazzName;
    private final String processName;
    protected final KogitoBuildContext context;
    protected WorkflowProcess process;
    private String processId;

    protected TriggerMetaData trigger;

    private CompilationUnit clazz;

    public MessageProducerGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            TriggerMetaData trigger) {
        this(context, process, trigger, "MessageProducer");
    }

    public MessageProducerGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            TriggerMetaData trigger,
            String templateName) {
        this.context = context;
        this.process = process;
        this.trigger = trigger;
        this.processPackageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        this.resourceClazzName = sanitizeClassName(processName) + "MessageProducer_" + trigger.getOwnerId();

        this.generator = TemplatedGenerator.builder()
                .withTargetTypeName(resourceClazzName)
                .withPackageName(processPackageName)
                .build(context, templateName);
        this.clazz = generator.compilationUnitOrThrow("Cannot generate message producer");
    }

    public String generate() {

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        generator,
                        "Cannot find class declaration"));
        template.setName(resourceClazzName);
        template.findAll(ConstructorDeclaration.class).forEach(cd -> cd.setName(resourceClazzName));

        template.findAll(ClassOrInterfaceType.class).forEach(cls -> CodegenUtils.interpolateTypes(cls, trigger.getDataType()));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$ClassName$", resourceClazzName)));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataType$", trigger.getDataType())));
        template.findAll(StringLiteralExpr.class).forEach(s -> s.setString(s.getValue().replace("$channel$", trigger.getName())));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger.getName())));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$ChannelName$", trigger.getChannelName())));

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    public String className() {
        return resourceClazzName;
    }

    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public CompilationUnit compilationUnit() {
        return clazz;
    }
}
