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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.AbstractApplicationSection;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
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

import static com.github.javaparser.ast.NodeList.nodeList;

public class ProcessContainerGenerator extends AbstractApplicationSection {

    public static final String SECTION_CLASS_NAME = "Processes";

    private final List<ProcessGenerator> processes;

    private BlockStmt byProcessIdBody = new BlockStmt();
    private BlockStmt processesBody = new BlockStmt();
    private final TemplatedGenerator templatedGenerator;

    public ProcessContainerGenerator(KogitoBuildContext context) {
        super(context, SECTION_CLASS_NAME);
        this.processes = new ArrayList<>();
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTargetTypeName(SECTION_CLASS_NAME)
                .build(context, "ProcessContainer");
    }

    public void addProcess(ProcessGenerator p) {
        processes.add(p);
        addProcessToApplication(p);
    }

    public List<ProcessGenerator> getProcesses() {
        return Collections.unmodifiableList(this.processes);
    }

    public void addProcessToApplication(ProcessGenerator r) {
        ObjectCreationExpr newProcess = new ObjectCreationExpr()
                .setType(r.targetCanonicalName())
                .addArgument("application")
                .addArgument(new NullLiteralExpr());
        MethodCallExpr expr = new MethodCallExpr(newProcess, "configure");
        MethodCallExpr method = new MethodCallExpr(new NameExpr("mappedProcesses"), "computeIfAbsent",
                nodeList(new StringLiteralExpr(r.processId()),
                        new LambdaExpr(new Parameter(new UnknownType(), "k"), expr)));
        IfStmt byProcessId = new IfStmt(new MethodCallExpr(new StringLiteralExpr(r.processId()), "equals", nodeList(new NameExpr("processId"))),
                new ReturnStmt(method),
                null);

        byProcessIdBody.addStatement(byProcessId);
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("Invalid Template: No CompilationUnit");

        registerProcessesExplicitly(compilationUnit);
        return compilationUnit;
    }

    private void registerProcessesExplicitly(CompilationUnit compilationUnit) {
        // only for non-DI cases
        if (!context.hasDI()) {
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
                        templatedGenerator,
                        "Cannot find 'processIds' method body"))
                .setBody(this.processesBody);
    }

    private void setupProcessById(CompilationUnit compilationUnit) {
        byProcessIdBody
                .addStatement(new ReturnStmt(new NullLiteralExpr()));
        compilationUnit.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("processById"))
                .orElseThrow(() -> new InvalidTemplateException(
                        templatedGenerator,
                        "Cannot find 'processById' method body"))
                .setBody(this.byProcessIdBody);
    }
}
