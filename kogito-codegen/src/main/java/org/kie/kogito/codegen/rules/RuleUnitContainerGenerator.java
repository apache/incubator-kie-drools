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
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;

import static org.kie.kogito.codegen.rules.IncrementalRuleCodegen.TEMPLATE_RULE_FOLDER;

public class RuleUnitContainerGenerator extends AbstractApplicationSection {

    public static final String SECTION_CLASS_NAME = "RuleUnits";

    private final List<RuleUnitGenerator> ruleUnits;
    private final TemplatedGenerator templatedGenerator;
    private List<BodyDeclaration<?>> factoryMethods = new ArrayList<>();

    public RuleUnitContainerGenerator(KogitoBuildContext context) {
        super(context, SECTION_CLASS_NAME);
        this.ruleUnits = new ArrayList<>();
        this.templatedGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withTargetTypeName(SECTION_CLASS_NAME)
                .build(context, "RuleUnitContainer");
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
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = templatedGenerator.compilationUnitOrThrow("No CompilationUnit");

        if (!context.hasDI()) {
            // only in a non DI context
            compilationUnit.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("create"))
                    .ifPresent(m -> m.setBody(factoryByIdBody())); // ignore if missing
        }

        return compilationUnit;
    }
}
