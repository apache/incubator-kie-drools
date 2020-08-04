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
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.CodegenUtils.interpolateArguments;
import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;
import static org.kie.kogito.codegen.CodegenUtils.isApplicationField;
import static org.kie.kogito.codegen.CodegenUtils.isProcessField;

public class MessageConsumerGenerator {
    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private final String processClazzName;
    private String processId;
    private String dataClazzName;
    private String modelfqcn;
    private final String processName;
    private final String appCanonicalName;
    private final String messageDataEventClassName;
    private DependencyInjectionAnnotator annotator;
    
    private TriggerMetaData trigger;
    
    public MessageConsumerGenerator(
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName,
            String messageDataEventClassName,
            TriggerMetaData trigger) {
        this.process = process;
        this.trigger = trigger;
        this.packageName = process.getPackageName();
        this.processId = process.getId();
        this.processName = processId.substring(processId.lastIndexOf('.') + 1);
        String classPrefix = StringUtils.ucFirst(processName);
        this.resourceClazzName = classPrefix + "MessageConsumer_" + trigger.getOwnerId();
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.modelfqcn = modelfqcn;
        this.dataClazzName = modelfqcn.substring(modelfqcn.lastIndexOf('.') + 1);
        this.processClazzName = processfqcn;
        this.appCanonicalName = appCanonicalName;
        this.messageDataEventClassName = messageDataEventClassName;
    }

    public MessageConsumerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public String className() {
        return resourceClazzName;
    }
    
    public String generatedFilePath() {
        return relativePath;
    }
    
    protected boolean useInjection() {
        return this.annotator != null;
    }
    
    public String generate() {
        CompilationUnit clazz = parse(
                this.getClass().getResourceAsStream("/class-templates/MessageConsumerTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());
        clazz.addImport(modelfqcn);

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).get();
        template.setName(resourceClazzName);        
        
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, dataClazzName));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("configure")).forEach(md -> md.addAnnotation("javax.annotation.PostConstruct"));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("consume")).forEach(md -> { 
            interpolateArguments(md, "String");
            md.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger.getName())));
            md.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataEventType$", messageDataEventClassName)));
            md.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataType$", trigger.getDataType())));
        });
        template.findAll(MethodCallExpr.class).forEach(this::interpolateStrings);
        
        if (useInjection()) {
            annotator.withApplicationComponent(template);
            
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> annotator.withNamedInjection(fd, processId));
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> annotator.withInjection(fd));

            template.findAll(FieldDeclaration.class,
                    fd -> fd.getVariable(0).getNameAsString().equals("useCloudEvents")).forEach(fd -> annotator.withConfigInjection(fd, "kogito.messaging.as-cloudevents"));
            
            template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("consume")).forEach(md -> annotator.withIncomingMessage(md, trigger.getName()));
        } else {
            template.findAll(FieldDeclaration.class,
                             fd -> isProcessField(fd)).forEach(fd -> initializeProcessField(fd, template));
            
            template.findAll(FieldDeclaration.class,
                             fd -> isApplicationField(fd)).forEach(fd -> initializeApplicationField(fd, template));
        }
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }
    
    private void initializeProcessField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(processClazzName));
    }
    
    private void initializeApplicationField(FieldDeclaration fd, ClassOrInterfaceDeclaration template) {        
        fd.getVariable(0).setInitializer(new ObjectCreationExpr().setType(appCanonicalName));
    }
    
    private void interpolateStrings(MethodCallExpr vv) {
        String s = vv.getNameAsString();        
        String interpolated =
                s.replace("$ModelRef$", StringUtils.ucFirst(trigger.getModelRef()));
        vv.setName(interpolated);
    }
}
