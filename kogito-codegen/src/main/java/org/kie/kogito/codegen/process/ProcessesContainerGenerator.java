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

import static com.github.javaparser.StaticJavaParser.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.drools.modelcompiler.builder.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.process.Processes;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.WildcardType;

public class ProcessesContainerGenerator extends AbstractApplicationSection {

    private static final String RESOURCE = "/class-templates/ProcessesTemplate.java";
    private final String packageName;
    private final List<ProcessGenerator> processes;
    private final List<ProcessInstanceGenerator> processInstances;
    private final List<BodyDeclaration<?>> factoryMethods;

    private DependencyInjectionAnnotator annotator;
    
    private NodeList<BodyDeclaration<?>> applicationDeclarations;
    private MethodDeclaration byProcessIdMethodDeclaration;
    private MethodDeclaration processesMethodDeclaration;

    public ProcessesContainerGenerator(String packageName) {
        super("Processes", "processes", Processes.class);
        this.packageName = packageName;
        this.processes = new ArrayList<>();
        this.processInstances = new ArrayList<>();
        this.factoryMethods = new ArrayList<>();
        this.applicationDeclarations = new NodeList<>();

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

    public List<BodyDeclaration<?>> factoryMethods() {
        return factoryMethods;
    }

    public void addProcess(ProcessGenerator p) {
        processes.add(p);
        addProcessToApplication(p);
    }

    public void addProcessToApplication(ProcessGenerator r) {
        ObjectCreationExpr newProcess = new ObjectCreationExpr()
                .setType(r.targetCanonicalName())
                .addArgument(new ThisExpr(new NameExpr("Application")));
        IfStmt byProcessId = new IfStmt(new MethodCallExpr(new StringLiteralExpr(r.processId()), "equals", NodeList.nodeList(new NameExpr("processId"))),
                                        new ReturnStmt(new MethodCallExpr(
                                                newProcess,
                                                "configure")),
                                        null);

        byProcessIdMethodDeclaration.getBody().get().addStatement(byProcessId);
    }

    public ProcessesContainerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public ClassOrInterfaceDeclaration classDeclaration() {
        byProcessIdMethodDeclaration.getBody().get().addStatement(new ReturnStmt(new NullLiteralExpr()));

        NodeList<Expression> processIds = NodeList.nodeList(processes.stream().map(p -> new StringLiteralExpr(p.processId())).collect(Collectors.toList()));
        processesMethodDeclaration.getBody().get().addStatement(new ReturnStmt(new MethodCallExpr(new NameExpr(Arrays.class.getCanonicalName()), "asList", processIds)));

        ClassOrInterfaceDeclaration cls = super.classDeclaration().setMembers(applicationDeclarations);
        cls.getMembers().sort(new BodyDeclarationComparator());
        
        return cls;
    }

    @Override
    public CompilationUnit injectableClass() {
        CompilationUnit compilationUnit = parse(this.getClass().getResourceAsStream(RESOURCE)).setPackageDeclaration(packageName);                        
        ClassOrInterfaceDeclaration cls = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get();
        
        cls.findAll(FieldDeclaration.class, (fd) -> fd.getVariable(0).getNameAsString().equals("processes")).forEach(fd -> {
            annotator.withInjection(fd);
            fd.getVariable(0).setType(new ClassOrInterfaceType(null, new SimpleName(annotator.multiInstanceInjectionType()), 
                                                               NodeList.nodeList(new ClassOrInterfaceType(null, new SimpleName(org.kie.kogito.process.Process.class.getCanonicalName()), NodeList.nodeList(new WildcardType(new ClassOrInterfaceType(null, Model.class.getCanonicalName())))))));
        });
        
        annotator.withApplicationComponent(cls);
        
        return compilationUnit;
    }
}
