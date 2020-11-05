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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

public class MessageProducerGenerator {

    protected static final String EVENT_DATA_VAR = "eventData";
    private static final String RESOURCE = "/class-templates/MessageProducerTemplate.java";
    private static final String RESOURCE_CDI = "/class-templates/CdiMessageProducerTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/SpringMessageProducerTemplate.java";

    private final TemplatedGenerator generator;

    private final String packageName;
    protected final String resourceClazzName;
    private final String processName;
    protected final String messageDataEventClassName;
    protected WorkflowProcess process;
    private String processId;
    protected DependencyInjectionAnnotator annotator;

    protected TriggerMetaData trigger;

    public MessageProducerGenerator(
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String messageDataEventClassName,
            TriggerMetaData trigger) {
        this.process = process;
        this.trigger = trigger;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.ucFirst(processName);
        this.resourceClazzName = classPrefix + "MessageProducer_" + trigger.getOwnerId();
        this.messageDataEventClassName = messageDataEventClassName;

        this.generator = new TemplatedGenerator(
                packageName,
                resourceClazzName,
                RESOURCE_CDI,
                RESOURCE_SPRING,
                RESOURCE);
    }

    public MessageProducerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        generator.withDependencyInjection(annotator);
        return this;
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(resourceClazzName, generator.templatePath(), "Cannot generate message producer"));
        clazz.setPackageDeclaration(process.getPackageName());

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).get();
        template.setName(resourceClazzName);
        template.findAll(ConstructorDeclaration.class).forEach(cd -> cd.setName(resourceClazzName));

        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, trigger.getDataType()));
        template.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger.getName())));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataEventType$", messageDataEventClassName)));
        template.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataType$", trigger.getDataType())));
        template.findAll(StringLiteralExpr.class).forEach(s -> s.setString(s.getValue().replace("$channel$", trigger.getName())));

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    public String className() {
        return resourceClazzName;
    }

    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    protected boolean useInjection() {
        return this.annotator != null;
    }
}
