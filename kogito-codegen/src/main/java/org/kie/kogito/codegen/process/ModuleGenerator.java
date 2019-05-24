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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.Model;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.WildcardType;

public class ModuleGenerator {

    private static final String RESOURCE = "/class-templates/ModuleTemplate.java";
    private final String packageName;
    private final String sourceFilePath;
    private final String completePath;
    private final String targetCanonicalName;
    private final List<ProcessGenerator> processes;
    private final List<ProcessInstanceGenerator> processInstances;
    private final List<MethodDeclaration> factoryMethods;
    private String targetTypeName;
    private boolean hasCdi;
    private String workItemConfigClass = DefaultWorkItemHandlerConfig.class.getCanonicalName();
    private String processEventListenerConfigClass = DefaultProcessEventListenerConfig.class.getCanonicalName();
    
    private List<BodyDeclaration<?>> applicationDeclarations = new ArrayList<>();
    private MethodDeclaration byProcessIdMethodDeclaration;
    private MethodDeclaration processesMethodDeclaration;

    public ModuleGenerator(String packageName) {
        this.packageName = packageName;
        this.targetTypeName = "Module";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.sourceFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + sourceFilePath;
        this.processes = new ArrayList<>();
        this.processInstances = new ArrayList<>();
        this.factoryMethods = new ArrayList<>();
        this.applicationDeclarations = new ArrayList<>();
        
        byProcessIdMethodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("processById")
                .setType(new ClassOrInterfaceType(null, org.kie.kogito.process.Process.class.getCanonicalName())
                         .setTypeArguments(new WildcardType(new ClassOrInterfaceType(null, Model.class.getCanonicalName()))))
                .setBody(new BlockStmt())
                .addParameter("String", "processId");
        
        processesMethodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("processIds")
                .setType(new ClassOrInterfaceType(null, Collection.class.getCanonicalName())
                         .setTypeArguments(new ClassOrInterfaceType(null, "String")))
                .setBody(new BlockStmt());
        
        applicationDeclarations.add(byProcessIdMethodDeclaration);
        applicationDeclarations.add(processesMethodDeclaration);
    }

    public List<MethodDeclaration> factoryMethods() {
        return factoryMethods;
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String generatedFilePath() {
        return sourceFilePath;
    }

    public void addProcess(ProcessGenerator p) {
        processes.add(p);
        addProcessFactoryMethod(p);
        addProcessToApplication(p);
    }

    public void addProcessInstance(ProcessInstanceGenerator pi) {
        processInstances.add(pi);
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit =
                JavaParser.parse(this.getClass().getResourceAsStream(RESOURCE))
                        .setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get();
        if (hasCdi) {
            cls.addAnnotation("javax.inject.Singleton");
        }

        factoryMethods.forEach(cls::addMember);

        cls.findFirst(ObjectCreationExpr.class, p -> p.getType().getNameAsString().equals("$WorkItemHandlerConfig$"))
                .ifPresent(o -> o.setType(workItemConfigClass));

        cls.findFirst(ObjectCreationExpr.class, p -> p.getType().getNameAsString().equals("$ProcessEventListenerConfig$"))
        .ifPresent(o -> o.setType(processEventListenerConfigClass));

        return compilationUnit;
    }

    public MethodDeclaration addProcessFactoryMethod(ProcessGenerator r) {
        MethodDeclaration methodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("create" + r.targetTypeName())
                .setType(r.targetCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(
                        new ObjectCreationExpr()
                                .setType(r.targetCanonicalName())
                                .addArgument(new ThisExpr()))));
        this.factoryMethods.add(methodDeclaration);
        return methodDeclaration;
    }
    
    public void addProcessToApplication(ProcessGenerator r) {
        IfStmt byProcessId = new IfStmt(new MethodCallExpr(new StringLiteralExpr(r.processId()), "equals", NodeList.nodeList(new NameExpr("processId"))), 
                                                  new ReturnStmt(new MethodCallExpr(null, "create" + r.targetTypeName())), 
                                                   null);
        
        byProcessIdMethodDeclaration.getBody().get().addStatement(byProcessId);
    }

    public ModuleGenerator withCdi(boolean hasCdi) {
        this.hasCdi = hasCdi;
        return this;
    }

    public void setWorkItemHandlerClass(String className) {
        this.workItemConfigClass = className;
    }

    public void setProcessEventListenerConfigClass(String processEventListenerConfigClass) {
        this.processEventListenerConfigClass = processEventListenerConfigClass;
    }

    public String workItemConfigClass() {
        return workItemConfigClass;
    }

    public String processEventListenerConfigClass() {
        return processEventListenerConfigClass;
    }
    
    public List<BodyDeclaration<?>> getApplicationBodyDeclaration() {
        
        byProcessIdMethodDeclaration.getBody().get().addStatement(new ReturnStmt(new NullLiteralExpr()));
        
        NodeList<Expression> processIds = NodeList.nodeList(processes.stream().map(p -> new StringLiteralExpr(p.processId())).collect(Collectors.toList()));
        processesMethodDeclaration.getBody().get().addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(Arrays.class.getCanonicalName()), "asList", processIds)));        
        
        ObjectCreationExpr processesClazz = new ObjectCreationExpr(null, new ClassOrInterfaceType(null, Processes.class.getCanonicalName()), NodeList.nodeList())
                .setAnonymousClassBody(NodeList.nodeList(applicationDeclarations));
        
        
        MethodDeclaration processesMethod = new MethodDeclaration()
                .setName("processes")
                .setModifiers(Keyword.PUBLIC)
                .setType(Processes.class)
                .setBody(new BlockStmt().addStatement(new ReturnStmt(processesClazz)));
        
        
        return Collections.singletonList(processesMethod);
    }
}
