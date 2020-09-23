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
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.units.impl.AbstractRuleUnits;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.WildcardType;

public class RuleUnitContainerGenerator extends AbstractApplicationSection {

    private static final String RESOURCE = "/class-templates/rules/RuleUnitContainerTemplate.java";
    private static final String RESOURCE_CDI = "/class-templates/rules/CdiRuleUnitContainerTemplate.java";
    private static final String RESOURCE_SPRING = "/class-templates/rules/SpringRuleUnitContainerTemplate.java";
    public static final String SECTION_CLASS_NAME = "RuleUnits";

    private final List<RuleUnitGenerator> ruleUnits;
    private final TemplatedGenerator templatedGenerator;
    private DependencyInjectionAnnotator annotator;
    private List<BodyDeclaration<?>> factoryMethods = new ArrayList<>();

    public RuleUnitContainerGenerator(String packageName) {
        super(SECTION_CLASS_NAME, "ruleUnits", AbstractRuleUnits.class);
        this.ruleUnits = new ArrayList<>();
        this.templatedGenerator = new TemplatedGenerator(
                packageName,
                SECTION_CLASS_NAME,
                RESOURCE_CDI,
                RESOURCE_SPRING,
                RESOURCE);
    }

    void addRuleUnit(RuleUnitGenerator rusc) {
        ruleUnits.add(rusc);
    }

    private BlockStmt factoryByIdBody() {

        SwitchStmt switchStmt = new SwitchStmt();
        switchStmt.setSelector(new NameExpr("fqcn"));

        for (RuleUnitGenerator ruleUnit : ruleUnits) {
            SwitchEntry switchEntry = new SwitchEntry();
            switchEntry.getLabels().add(new StringLiteralExpr(ruleUnit.getRuleUnitDescription().getCanonicalName()));
            ObjectCreationExpr ruleUnitConstructor = new ObjectCreationExpr()
                    .setType(ruleUnit.targetCanonicalName())
                    .addArgument("application");
            switchEntry.getStatements().add(new ReturnStmt(ruleUnitConstructor));
            switchStmt.getEntries().add(switchEntry);
        }

        SwitchEntry defaultEntry = new SwitchEntry();
        defaultEntry.getStatements().add(new ThrowStmt(new ObjectCreationExpr().setType(UnsupportedOperationException.class.getCanonicalName())));
        switchStmt.getEntries().add(defaultEntry);

        return new BlockStmt().addStatement(switchStmt);
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(
                        SECTION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "No CompilationUnit"));

        if (annotator == null) {
            // only in a non DI context
            compilationUnit.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("create"))
                    .ifPresent(m -> m.setBody(factoryByIdBody())); // ignore if missing
        }

        return compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(
                        SECTION_CLASS_NAME,
                        templatedGenerator.templatePath(),
                        "No class declaration"));
    }

    public RuleUnitContainerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        this.templatedGenerator.withDependencyInjection(annotator);
        return this;
    }

    List<RuleUnitGenerator> getRuleUnits() {
        return ruleUnits;
    }
}
