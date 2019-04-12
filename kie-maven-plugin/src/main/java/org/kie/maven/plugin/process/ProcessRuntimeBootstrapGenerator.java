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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.WorkItemHandler;

import static org.kie.maven.plugin.GenerateProcessModelMojo.BOOTSTRAP_PACKAGE;

public class ProcessRuntimeBootstrapGenerator {

    private static final List<String> DEFAULT_HANDLERS = Arrays.asList("Log", "Human Task");

    private final List<String> compiledClassNames;
    private static final String BOOTSTRAP_CLASS = BOOTSTRAP_PACKAGE + ".ProcessRuntimeProvider";
    private final String generatedFilePath;
    private final Map<String, String> workItemHandlers;

    public ProcessRuntimeBootstrapGenerator(List<ProcessExecutableModelGenerator> legacyProcesses, Map<String, String> workItemHandlers) {
        this.workItemHandlers = workItemHandlers;
        this.generatedFilePath = BOOTSTRAP_CLASS.replace('.', '/') + ".java";
        this.compiledClassNames =
                legacyProcesses.stream()
                        .map(ProcessExecutableModelGenerator::className)
                        .collect(Collectors.toList());
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    public String generate() {
        CompilationUnit clazz = JavaParser.parse(this.getClass().getResourceAsStream("/class-templates/ProcessRuntimeTemplate.java"));
        clazz.setPackageDeclaration(BOOTSTRAP_PACKAGE);
        clazz.addImport(ArrayList.class);
        Optional<ClassOrInterfaceDeclaration> resourceClassOptional = clazz.findFirst(ClassOrInterfaceDeclaration.class, sl -> true);

        if (resourceClassOptional.isPresent()) {

            ClassOrInterfaceDeclaration resourceClass = resourceClassOptional.get();

            MethodDeclaration getProcessesMethod = resourceClass.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getProcesses")).get();
            BlockStmt body = new BlockStmt();

            ClassOrInterfaceType listType = new ClassOrInterfaceType(null, new SimpleName(List.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, Process.class.getSimpleName())));
            VariableDeclarationExpr processesField = new VariableDeclarationExpr(listType, "processes");

            body.addStatement(new AssignExpr(processesField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));

            for (String processClass : compiledClassNames) {
                MethodCallExpr addProcess = new MethodCallExpr(new NameExpr("processes"), "add").addArgument(new MethodCallExpr(new NameExpr(processClass), "process"));
                body.addStatement(addProcess);
            }

            body.addStatement(new ReturnStmt(new NameExpr("processes")));
            getProcessesMethod.setBody(body);

            // set work item handlers if found
            if (!workItemHandlers.isEmpty()) {
                MethodDeclaration getHandlersMethod = resourceClass.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("getWorkItemHandlers")).get();
                BlockStmt hanldersBody = new BlockStmt();

                ClassOrInterfaceType handlersListType = new ClassOrInterfaceType(null, new SimpleName(List.class.getSimpleName()), NodeList.nodeList(new ClassOrInterfaceType(null, WorkItemHandler.class.getSimpleName())));
                VariableDeclarationExpr handlersField = new VariableDeclarationExpr(handlersListType, "handlers");

                hanldersBody.addStatement(new AssignExpr(handlersField, new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ArrayList.class.getSimpleName()), NodeList.nodeList()), AssignExpr.Operator.ASSIGN));

                for (Map.Entry<String, String> e : workItemHandlers.entrySet()) {
                    String workItem = e.getKey();
                    String handlerClass = e.getValue();
                    if (handlerClass == null) {
                        throw new IllegalArgumentException("Cannot find work work item handler for " + workItem);
                    }
                    MethodCallExpr addHandler = new MethodCallExpr(new NameExpr("handlers"), "add").addArgument(new ObjectCreationExpr(null, new ClassOrInterfaceType(null, handlerClass), NodeList.nodeList()));
                    hanldersBody.addStatement(addHandler);
                }

                hanldersBody.addStatement(new ReturnStmt(new NameExpr("handlers")));
                getHandlersMethod.setBody(hanldersBody);
            }
        }

        return clazz.toString();
    }
}
