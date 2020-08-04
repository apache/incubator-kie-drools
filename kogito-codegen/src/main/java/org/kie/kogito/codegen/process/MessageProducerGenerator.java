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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.StringUtils;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.CodegenUtils.interpolateTypes;

public class MessageProducerGenerator {
    
    private static final String EVENT_DATA_VAR = "eventData";
    
    private final String relativePath;

    private WorkflowProcess process;
    private final String packageName;
    private final String resourceClazzName;
    private String processId;
    private final String processName;
    private final String messageDataEventClassName;
    private DependencyInjectionAnnotator annotator;
    
    private TriggerMetaData trigger;
    
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
        this.relativePath = packageName.replace(".", "/") + "/" + resourceClazzName + ".java";
        this.messageDataEventClassName = messageDataEventClassName;
    }

    public MessageProducerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
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
                this.getClass().getResourceAsStream("/class-templates/MessageProducerTemplate.java"));
        clazz.setPackageDeclaration(process.getPackageName());

        ClassOrInterfaceDeclaration template = clazz.findFirst(ClassOrInterfaceDeclaration.class).get();
        template.setName(resourceClazzName);        
        
        template.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, trigger.getDataType()));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("produce")).forEach(md -> md.getParameters().stream().filter(p -> p.getNameAsString().equals(EVENT_DATA_VAR)).forEach(p -> p.setType(trigger.getDataType())));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("configure")).forEach(md -> md.addAnnotation("javax.annotation.PostConstruct"));
        template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("marshall")).forEach(md -> {
            md.getParameters().stream().filter(p -> p.getNameAsString().equals(EVENT_DATA_VAR)).forEach(p -> p.setType(trigger.getDataType()));
            md.findAll(ClassOrInterfaceType.class).forEach(t -> t.setName(t.getNameAsString().replace("$DataEventType$", messageDataEventClassName)));
        });
        
        if (useInjection()) {
            annotator.withApplicationComponent(template);
            
            FieldDeclaration emitterField = template.findFirst(FieldDeclaration.class).filter(fd -> fd.getVariable(0).getNameAsString().equals("emitter")).get();
            annotator.withInjection(emitterField);
            annotator.withOutgoingMessage(emitterField, trigger.getName());
            emitterField.getVariable(0).setType(annotator.emitterType("String"));
            
            MethodDeclaration produceMethod = template.findAll(MethodDeclaration.class).stream().filter(md -> md.getNameAsString().equals("produce")).findFirst().orElseThrow(() -> new IllegalStateException("Cannot find produce methos in MessageProducerTemplate"));
            BlockStmt body = new BlockStmt();
            MethodCallExpr sendMethodCall = new MethodCallExpr(new NameExpr("emitter"), "send");
            annotator.withMessageProducer(sendMethodCall, trigger.getName(), new MethodCallExpr(new ThisExpr(), "marshall").addArgument(new NameExpr("pi")).addArgument(new NameExpr(EVENT_DATA_VAR)));
            body.addStatement(sendMethodCall);
            produceMethod.setBody(body);

            template.findAll(FieldDeclaration.class,
                    fd -> fd.getVariable(0).getNameAsString().equals("useCloudEvents")).forEach(fd -> annotator.withConfigInjection(fd, "kogito.messaging.as-cloudevents"));
            
        } 
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

}
