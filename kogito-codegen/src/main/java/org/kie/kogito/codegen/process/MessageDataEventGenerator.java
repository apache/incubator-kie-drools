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
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

public class MessageDataEventGenerator {

    private final KogitoBuildContext context;
    private final WorkflowProcess process;
    private final String resourceClazzName;
    private final String processId;
    private final String processName;
    private final TemplatedGenerator generator;
    private final TriggerMetaData trigger;
    
    public MessageDataEventGenerator(
            KogitoBuildContext context,
            WorkflowProcess process,
            TriggerMetaData trigger) {
        this.context = context;
        this.process = process;
        this.trigger = trigger;
        String messageDataPackageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.ucFirst(processName);
        this.resourceClazzName = classPrefix + "MessageDataEvent_" + trigger.getOwnerId();
        this.generator = TemplatedGenerator.builder()
                .withPackageName(messageDataPackageName)
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .withTargetTypeName(resourceClazzName)
                .build(context, "MessageDataEvent");
    }

    public String className() {
        return generator.targetTypeName();
    }
    
    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

    public String generate() {
        CompilationUnit clazz = generator.compilationUnitOrThrow();

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        generator,
                        "Cannot find the class in MessageDataEventTemplate"));
        template.setName(resourceClazzName);  
        
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, trigger.getDataType()));
        template.findAll(ConstructorDeclaration.class).forEach(cd -> cd.setName(resourceClazzName));
        template.findAll(StringLiteralExpr.class).stream().filter(s -> s.getValue().equals("$TypeName$")).forEach(s -> s.setString(resourceClazzName));

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

}
