/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.prediction;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.prediction.PredictionModels;

public class PredictionContainerGenerator extends AbstractApplicationSection {

    private static final String TEMPLATE_JAVA = "/class-templates/PMMLApplicationClassDeclTemplate.java";

    private static final RuntimeException MODIFIED_TEMPLATE_EXCEPTION =
            new RuntimeException("The template " + TEMPLATE_JAVA + " has been modified.");
    final List<PMMLResource> resources;
    final String applicationCanonicalName;
    AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
    final List<String> predictionRulesMapperClasses = new ArrayList<>();

    public PredictionContainerGenerator(String applicationCanonicalName, List<PMMLResource> resources) {
        super("PredictionModels", "predictionModels", PredictionModels.class);
        this.applicationCanonicalName = applicationCanonicalName;
        this.resources = resources;
    }

    public PredictionContainerGenerator withAddons(AddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
        return this;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        CompilationUnit clazz = StaticJavaParser.parse(this.getClass().getResourceAsStream(TEMPLATE_JAVA));
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) clazz.getTypes().get(0);
        populateStaticKieRuntimeFactoryFunctionInit(typeDeclaration);
        return typeDeclaration;
    }

    public void addPredictionRulesMapperClass(String predictionRulesMapperClass) {
        predictionRulesMapperClasses.add(predictionRulesMapperClass);
    }

    private void populateStaticKieRuntimeFactoryFunctionInit(ClassOrInterfaceDeclaration typeDeclaration) {
        final InitializerDeclaration staticDeclaration = typeDeclaration.getMembers()
                .stream()
                .filter(member -> member instanceof InitializerDeclaration)
                .findFirst()
                .map(member -> (InitializerDeclaration) member)
                .orElseThrow(() -> MODIFIED_TEMPLATE_EXCEPTION);
        final NodeList<Statement> statements = staticDeclaration.getBody().getStatements();
        final VariableDeclarationExpr kieRuntimeFactories = statements.stream()
                .filter(statement -> statement instanceof ExpressionStmt && ((ExpressionStmt) statement).getExpression() instanceof VariableDeclarationExpr)
                .map(statement -> (VariableDeclarationExpr) ((ExpressionStmt) statement).getExpression())
                .filter(expression -> expression.getVariable(0).getName().asString().equals("kieRuntimeFactories"))
                .findFirst()
                .orElseThrow(() -> MODIFIED_TEMPLATE_EXCEPTION);
        MethodCallExpr methodCallExpr = kieRuntimeFactories.getVariable(0)
                .getInitializer()
                .map(expression -> (MethodCallExpr) expression)
                .orElseThrow(() -> MODIFIED_TEMPLATE_EXCEPTION);
        for (PMMLResource resource : resources) {
            StringLiteralExpr getResAsStream = getReadResourceMethod(resource);
            methodCallExpr.addArgument(getResAsStream);
        }
    }

    private StringLiteralExpr getReadResourceMethod(PMMLResource resource) {
        String source = resource.getModelPath();
        return new StringLiteralExpr(source);
    }
}
