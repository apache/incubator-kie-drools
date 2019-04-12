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

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.submarine.process.impl.AbstractProcess;

import static com.github.javaparser.ast.NodeList.nodeList;

public class ProcessGenerator {

    private final String packageName;
    private final WorkflowProcess process;
    private final ProcessExecutableModelGenerator legacyProcessGenerator;
    private final Map<String, WorkflowProcess> processMapping;
    private final String typeName;
    private final String modelTypeName;
    private final String generatedFilePath;
    private final String completePath;
    private final String canonicalName;
    private final String targetCanonicalName;
    private final String moduleCanonicalName;
    private String targetTypeName;
    private boolean hasCdi = false;

    public ProcessGenerator(
            WorkflowProcess process,
            ProcessExecutableModelGenerator legacyProcessGenerator,
            Map<String, WorkflowProcess> processMapping,
            String typeName,
            String modelTypeName,
            String moduleCanonicalName) {

        this.moduleCanonicalName = moduleCanonicalName;

        this.packageName = process.getPackageName();
        this.process = process;
        this.legacyProcessGenerator = legacyProcessGenerator;
        this.processMapping = processMapping;
        this.typeName = typeName;
        this.modelTypeName = modelTypeName;
        this.canonicalName = packageName + "." + typeName;
        this.targetTypeName = typeName + "Process";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.completePath = "src/main/java/" + generatedFilePath;
    }

    public String targetCanonicalName() {
        return targetCanonicalName;
    }

    public String targetTypeName() {
        return targetTypeName;
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(completePath, generate().getBytes());
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.addImport("org.jbpm.process.core.datatype.impl.type.ObjectDataType");
        compilationUnit.addImport("org.jbpm.ruleflow.core.RuleFlowProcessFactory");
        compilationUnit.addImport("org.drools.core.util.KieFunctions");
        compilationUnit.getTypes().add(classDeclaration());
        return compilationUnit;
    }

    private MethodDeclaration createInstanceMethod(String processInstanceFQCN) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();

        ReturnStmt returnStmt = new ReturnStmt(
                new ObjectCreationExpr()
                        .setType(processInstanceFQCN)
                        .setArguments(nodeList(
                                new ThisExpr(),
                                new NameExpr("value"),
                                createProcessRuntime())));

        methodDeclaration.setName("createInstance")
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(modelTypeName, "value")
                .setType(processInstanceFQCN)
                .setBody(new BlockStmt()
                                 .addStatement(returnStmt));
        return methodDeclaration;
    }

    private MethodDeclaration legacyProcess() {
        MethodDeclaration legacyProcess = legacyProcessGenerator.generate()
                .getGeneratedClassModel()
                .findFirst(MethodDeclaration.class).get()
                .setModifiers(Modifier.Keyword.PROTECTED)
                .setType(Process.class.getCanonicalName())
                .setName("legacyProcess");
        return legacyProcess;
    }

    private MethodCallExpr createProcessRuntime() {
        return new MethodCallExpr(
                new ThisExpr(),
                "createLegacyProcessRuntime");
    }

    public static ClassOrInterfaceType processType(String canonicalName) {
        return new ClassOrInterfaceType(null, canonicalName + "Process");
    }

    public static ClassOrInterfaceType abstractProcessType(String canonicalName) {
        return new ClassOrInterfaceType(null, AbstractProcess.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(targetTypeName)
                .setModifiers(Modifier.Keyword.PUBLIC);

        if (hasCdi) {
            cls.addAnnotation("javax.inject.Singleton")
                    .addAnnotation(new SingleMemberAnnotationExpr(
                            new Name("javax.inject.Named"),
                            new StringLiteralExpr(process.getId())));
        }

        String processInstanceFQCN = ProcessInstanceGenerator.qualifiedName(packageName, typeName);

        FieldDeclaration fieldDeclaration = new FieldDeclaration()
                .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, moduleCanonicalName), "module"));

        ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .addParameter(moduleCanonicalName, "module")
                .setBody(new BlockStmt()
                                 // super(module.config().process())
                                 .addStatement(new MethodCallExpr(null, "super")
                                              .addArgument(
                                                      new MethodCallExpr(
                                                              new MethodCallExpr(new NameExpr("module"), "config"),
                                                              "process")))
                                 .addStatement(
                                         new AssignExpr(new FieldAccessExpr(new ThisExpr(), "module"), new NameExpr("module"), AssignExpr.Operator.ASSIGN)));
        ConstructorDeclaration emptyConstructorDeclaration = new ConstructorDeclaration()
                .setName(targetTypeName)
                .addModifier(Modifier.Keyword.PUBLIC)
                .setBody(new BlockStmt()
                                 .addStatement(
                                         new MethodCallExpr(null, "this").addArgument(new ObjectCreationExpr().setType(moduleCanonicalName))));

        MethodDeclaration methodDeclaration = createInstanceMethod(processInstanceFQCN);
        cls.addExtendedType(abstractProcessType(modelTypeName))
                .addMember(fieldDeclaration)
                .addMember(emptyConstructorDeclaration)
                .addMember(constructorDeclaration)
                .addMember(methodDeclaration)
                .addMember(legacyProcess());
        return cls;
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    public boolean isPublic() {
        return WorkflowProcess.PUBLIC_VISIBILITY.equalsIgnoreCase(process.getVisibility());
    }

    public ProcessGenerator withCdi(boolean dependencyInjection) {
        this.hasCdi = dependencyInjection;
        return this;
    }
}
