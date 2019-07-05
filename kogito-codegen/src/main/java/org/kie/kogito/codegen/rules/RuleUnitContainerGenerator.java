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

package org.kie.kogito.codegen.rules;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnits;

public class RuleUnitContainerGenerator extends AbstractApplicationSection {

    private final String packageName;
    private final String generatedFilePath;
    private final String targetCanonicalName;
    private final List<RuleUnitSourceClass> ruleUnits;
    private String targetTypeName;
    private DependencyInjectionAnnotator annotator;
    private List<BodyDeclaration<?>> factoryMethods = new ArrayList<>();

    private String ruleEventListenersConfigClass = DefaultRuleEventListenerConfig.class.getCanonicalName();

    public RuleUnitContainerGenerator(String packageName) {
        super("RuleUnits", "ruleUnits", RuleUnits.class);
        this.packageName = packageName;
        this.targetTypeName = "Module";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.ruleUnits = new ArrayList<>();
    }

    public List<BodyDeclaration<?>> factoryMethods() {
        return factoryMethods;
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    public void addRuleUnit(RuleUnitSourceClass rusc) {
        ruleUnits.add(rusc);
        addRuleUnitFactoryMethod(rusc);
    }

    public String generate() {
        return compilationUnit().toString();
    }

    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration cls =
                compilationUnit.addClass(targetTypeName);

        factoryMethods.forEach(cls::addMember);

        return compilationUnit;
    }

    public MethodDeclaration addRuleUnitFactoryMethod(RuleUnitSourceClass r) {
        MethodDeclaration methodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("create" + r.targetTypeName())
                .setType(r.targetCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(
                        new ObjectCreationExpr()
                                .setType(r.targetCanonicalName()))));
        this.factoryMethods.add(methodDeclaration);
        return methodDeclaration;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {

        NodeList<BodyDeclaration<?>> declarations = new NodeList<>();
        FieldDeclaration kieRuntimeFieldDeclaration = new FieldDeclaration();

        // declare field `ruleRuntimeBuilder`
        kieRuntimeFieldDeclaration
                .addVariable(new VariableDeclarator(
                        new ClassOrInterfaceType(null, KieRuntimeBuilder.class.getCanonicalName()),
                        "ruleRuntimeBuilder")
                                     .setInitializer(new ObjectCreationExpr()
                                                             .setType(CanonicalModelKieProject.PROJECT_RUNTIME_CLASS)));
        declarations.add(kieRuntimeFieldDeclaration);

        // declare method ruleRuntimeBuilder()
        MethodDeclaration methodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("ruleRuntimeBuilder")
                .setType(KieRuntimeBuilder.class.getCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "ruleRuntimeBuilder"))));

        declarations.add(methodDeclaration);

        declarations.addAll(factoryMethods);

        return super.classDeclaration()
                .setMembers(declarations);
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public RuleUnitContainerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    public List<RuleUnitSourceClass> getRuleUnits() {
        return ruleUnits;
    }

    public void setRuleEventListenersConfigClass(String ruleEventListenersConfigClass) {
        this.ruleEventListenersConfigClass = ruleEventListenersConfigClass;
    }

    public String ruleEventListenersConfigClass() {
        return ruleEventListenersConfigClass;
    }
}
