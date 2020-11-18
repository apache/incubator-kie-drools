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
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.process.Processes;

import static com.github.javaparser.ast.NodeList.nodeList;

public class ProcessContainerGenerator extends AbstractApplicationSection {

    private static final String RESOURCE = "/class-templates/ProcessContainerTemplate.java";
    private static final String RESOURCE_CDI = "/class-templates/CdiProcessContainerTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/SpringProcessContainerTemplate.java";
    public static final String SECTION_CLASS_NAME = "Processes";

    private final String packageName;
    private final List<ProcessGenerator> processes;
    private final List<BodyDeclaration<?>> factoryMethods;

    private DependencyInjectionAnnotator annotator;

    private BlockStmt byProcessIdBody = new BlockStmt();
    private BlockStmt processesBody = new BlockStmt();
    private final TemplatedGenerator templatedGenerator;

    public ProcessContainerGenerator(String packageName) {
        super(SECTION_CLASS_NAME, "processes", Processes.class);
        this.packageName = packageName;
        this.processes = new ArrayList<>();
        this.factoryMethods = new ArrayList<>();

        this.templatedGenerator = new TemplatedGenerator(
                packageName,
                SECTION_CLASS_NAME,
                RESOURCE_CDI,
                RESOURCE_SPRING,
                RESOURCE);
    }

    public void addProcess(ProcessGenerator p) {
        processes.add(p);
        addProcessToApplication(p);
    }

    public void addProcessToApplication(ProcessGenerator r) {
        ObjectCreationExpr newProcess = new ObjectCreationExpr()
                .setType(r.targetCanonicalName())
                .addArgument("application");
        MethodCallExpr expr = new MethodCallExpr(newProcess, "configure");
        MethodCallExpr method = new MethodCallExpr(new NameExpr("mappedProcesses"), "computeIfAbsent",
                                                   nodeList(new StringLiteralExpr(r.processId()), 
                                                            new LambdaExpr(new Parameter(new UnknownType(), "k"), expr)));
        IfStmt byProcessId = new IfStmt(new MethodCallExpr(new StringLiteralExpr(r.processId()), "equals", nodeList(new NameExpr("processId"))),
                                        new ReturnStmt(method),
                                        null);

        byProcessIdBody.addStatement(byProcessId);
    }

    public ProcessContainerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.templatedGenerator.withDependencyInjection(annotator);
        return this;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnit()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Template: No CompilationUnit"));

        registerProcessesExplicitly(compilationUnit);
        return compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        SECTION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "Cannot find class definition"));
    }

    private void registerProcessesExplicitly(CompilationUnit compilationUnit) {
        // only for non-DI cases
        if (annotator == null) {
            setupProcessById(compilationUnit);
            setupProcessIds(compilationUnit);
        }
    }

    private void setupProcessIds(CompilationUnit compilationUnit) {
        NodeList<Expression> processIds = nodeList(processes.stream().map(p -> new StringLiteralExpr(p.processId())).collect(Collectors.toList()));
        processesBody
                .addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(Arrays.class.getCanonicalName()), "asList", processIds)));

        compilationUnit.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("processIds"))
                .orElseThrow(() -> new InvalidTemplateException(
                        SECTION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "Cannot find 'processIds' method body"))
                .setBody(this.processesBody);
    }

    private void setupProcessById(CompilationUnit compilationUnit) {
        byProcessIdBody
                .addStatement(new ReturnStmt(new NullLiteralExpr()));
        compilationUnit.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("processById"))
                .orElseThrow(() -> new InvalidTemplateException(
                        SECTION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "Cannot find 'processById' method body"))
                .setBody(this.byProcessIdBody);
    }
}
