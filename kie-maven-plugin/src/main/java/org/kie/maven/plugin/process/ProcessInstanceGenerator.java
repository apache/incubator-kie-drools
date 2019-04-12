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

package org.kie.maven.plugin.process;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.submarine.process.impl.AbstractProcessInstance;

public class ProcessInstanceGenerator {

    private final String packageName;
    private final String typeName;
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
        this.typeName = typeName;
        this.model = model;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "ProcessInstance";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + generatedFilePath;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(completePath, generate().getBytes());
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.getTypes().add(classDeclaration());
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
                .addMember(bind())
                .addMember(unbind());

        return classDecl;
    }

    private MethodDeclaration bind() {
        String modelName = model.getModelClassSimpleName();
        BlockStmt body = new BlockStmt()
                .addStatement(new ReturnStmt(model.toMap("variables")));
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
                .addParameter(ProcessGenerator.processType(canonicalName), "process")
                .addParameter(model.getModelClassSimpleName(), "value")
                .addParameter(ProcessRuntime.class.getCanonicalName(), "processRuntime")
                .setBody(new BlockStmt().addStatement(new MethodCallExpr(
                        "super",
                        new NameExpr("process"),
                        new NameExpr("value"),
                        new NameExpr("processRuntime"))));
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }
}
