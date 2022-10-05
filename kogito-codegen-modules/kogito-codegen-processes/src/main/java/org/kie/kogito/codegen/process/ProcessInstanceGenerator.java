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
package org.kie.kogito.codegen.process;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;

public class ProcessInstanceGenerator {

    private static final String PROCESS = "process";
    private static final String VALUE = "value";
    private static final String PROCESS_RUNTIME = "processRuntime";
    private static final String BUSINESS_KEY = "businessKey";
    private static final String CORRELATION = "correlation";
    private static final String WPI = "wpi";

    private final String packageName;
    private final ModelMetaData model;
    private final String canonicalName;
    private final String targetTypeName;
    private final String targetCanonicalName;
    private final String generatedFilePath;
    private final String completePath;

    public static String qualifiedName(String packageName, String typeName) {
        return packageName + "." + typeName + "ProcessInstance";
    }

    public ProcessInstanceGenerator(String packageName, String typeName, ModelMetaData model) {
        this.packageName = packageName;
        this.model = model;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "ProcessInstance";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + generatedFilePath;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(completePath, generate().getBytes(StandardCharsets.UTF_8));
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.getTypes().add(classDeclaration());
        compilationUnit.addImport(model.getModelClassName());
        return compilationUnit;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration classDecl = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC);
        classDecl
                .addExtendedType(
                        new ClassOrInterfaceType(null, AbstractProcessInstance.class.getCanonicalName())
                                .setTypeArguments(new ClassOrInterfaceType(null, model.getModelClassSimpleName())))
                .addMember(constructorDecl())
                .addMember(constructorWithBusinessKeyDecl())
                .addMember(constructorWithWorkflowInstanceAndRuntimeDecl())
                .addMember(constructorWorkflowInstanceDecl())
                .addMember(constructorWithCorrelationDecl())
                .addMember(bind())
                .addMember(unbind());
        classDecl.getMembers().sort(new BodyDeclarationComparator());
        return classDecl;
    }

    private MethodDeclaration bind() {
        String modelName = model.getModelClassSimpleName();
        BlockStmt body = new BlockStmt()
                .addStatement(new IfStmt()
                        .setCondition(new BinaryExpr(new NullLiteralExpr(), new NameExpr("variables"), BinaryExpr.Operator.NOT_EQUALS))
                        .setThenStmt(new ReturnStmt(model.toMap("variables")))
                        .setElseStmt(new ReturnStmt(new ObjectCreationExpr().setType(new ClassOrInterfaceType(null, HashMap.class.getCanonicalName())))));
        return new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PROTECTED)
                .setName("bind")
                .addParameter(modelName, "variables")
                .setType(new ClassOrInterfaceType()
                        .setName("java.util.Map")
                        .setTypeArguments(new ClassOrInterfaceType().setName("String"),
                                new ClassOrInterfaceType().setName("Object")))
                .setBody(body);

    }

    private MethodDeclaration unbind() {
        String modelName = model.getModelClassSimpleName();
        BlockStmt body = new BlockStmt()
                .addStatement(model.fromMap("variables", "vmap"));

        return new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PROTECTED)
                .setName("unbind")
                .setType(new VoidType())
                .addParameter(modelName, "variables")
                .addParameter(new ClassOrInterfaceType()
                        .setName("java.util.Map")
                        .setTypeArguments(new ClassOrInterfaceType().setName("String"),
                                new ClassOrInterfaceType().setName("Object")),
                        "vmap")
                .setBody(body);
    }

    private ConstructorDeclaration constructorDecl() {
        return new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(ProcessGenerator.processType(canonicalName), PROCESS)
                .addParameter(model.getModelClassSimpleName(), VALUE)
                .addParameter(ProcessRuntime.class.getCanonicalName(), PROCESS_RUNTIME)
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr(PROCESS),
                        new NameExpr(VALUE),
                        new NameExpr(PROCESS_RUNTIME))));
    }

    private ConstructorDeclaration constructorWithBusinessKeyDecl() {
        return new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(ProcessGenerator.processType(canonicalName), PROCESS)
                .addParameter(model.getModelClassSimpleName(), VALUE)
                .addParameter(String.class.getCanonicalName(), BUSINESS_KEY)
                .addParameter(ProcessRuntime.class.getCanonicalName(), PROCESS_RUNTIME)
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr(PROCESS),
                        new NameExpr(VALUE),
                        new NameExpr(BUSINESS_KEY),
                        new NameExpr(PROCESS_RUNTIME))));
    }

    private ConstructorDeclaration constructorWithCorrelationDecl() {
        return new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(ProcessGenerator.processType(canonicalName), PROCESS)
                .addParameter(model.getModelClassSimpleName(), VALUE)
                .addParameter(String.class.getCanonicalName(), BUSINESS_KEY)
                .addParameter(ProcessRuntime.class.getCanonicalName(), PROCESS_RUNTIME)
                .addParameter(CompositeCorrelation.class.getCanonicalName(), CORRELATION)
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr(PROCESS),
                        new NameExpr(VALUE),
                        new NameExpr(BUSINESS_KEY),
                        new NameExpr(PROCESS_RUNTIME),
                        new NameExpr(CORRELATION))));
    }

    private ConstructorDeclaration constructorWithWorkflowInstanceAndRuntimeDecl() {
        return new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(ProcessGenerator.processType(canonicalName), PROCESS)
                .addParameter(model.getModelClassSimpleName(), VALUE)
                .addParameter(ProcessRuntime.class.getCanonicalName(), PROCESS_RUNTIME)
                .addParameter(WorkflowProcessInstance.class.getCanonicalName(), WPI)
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr(PROCESS),
                        new NameExpr(VALUE),
                        new NameExpr(PROCESS_RUNTIME),
                        new NameExpr(WPI))));
    }

    private ConstructorDeclaration constructorWorkflowInstanceDecl() {
        return new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(ProcessGenerator.processType(canonicalName), PROCESS)
                .addParameter(model.getModelClassSimpleName(), VALUE)
                .addParameter(WorkflowProcessInstance.class.getCanonicalName(), WPI)
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr(PROCESS),
                        new NameExpr(VALUE),
                        new NameExpr(WPI))));
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }
}
