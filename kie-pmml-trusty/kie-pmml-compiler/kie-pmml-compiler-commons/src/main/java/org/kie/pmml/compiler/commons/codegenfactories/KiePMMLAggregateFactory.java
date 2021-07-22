/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Aggregate;
import org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLAggregate</code> code-generators
 * out of <code>Aggregate</code>s
 */
public class KiePMMLAggregateFactory {

    private KiePMMLAggregateFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_AGGREGATE_TEMPLATE_JAVA = "KiePMMLAggregateTemplate.tmpl";
    static final String KIE_PMML_AGGREGATE_TEMPLATE = "KiePMMLAggregateTemplate";
    static final String GETKIEPMMLAGGREGATE = "getKiePMMLAggregate";
    static final String AGGREGATE = "aggregate";
    static final ClassOrInterfaceDeclaration AGGREGATE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_AGGREGATE_TEMPLATE_JAVA);
        AGGREGATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_AGGREGATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_AGGREGATE_TEMPLATE));
        AGGREGATE_TEMPLATE.getMethodsByName(GETKIEPMMLAGGREGATE).get(0).clone();
    }

    static BlockStmt getAggregateVariableDeclaration(final String variableName, final Aggregate aggregate) {
        final MethodDeclaration methodDeclaration = AGGREGATE_TEMPLATE.getMethodsByName(GETKIEPMMLAGGREGATE).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, AGGREGATE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, AGGREGATE, toReturn)));
        variableDeclarator.setName(variableName);
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, AGGREGATE, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(aggregate.getField().getValue());
        final AGGREGATE_FUNCTIONS function = AGGREGATE_FUNCTIONS.byName(aggregate.getFunction().value());
        final NameExpr functionExpr = new NameExpr(AGGREGATE_FUNCTIONS.class.getName() + "." + function.name());
        builder.setArgument(0, nameExpr);
        builder.setArgument(2, functionExpr);
        Expression groupFieldExpression = aggregate.getGroupField() != null ? getExpressionForObject(aggregate.getGroupField().getValue()) : new NullLiteralExpr();
        getChainedMethodCallExprFrom("withGroupField", initializer).setArgument(0, groupFieldExpression);
        Expression sqlWhereExpression = aggregate.getSqlWhere() != null ? new StringLiteralExpr(aggregate.getSqlWhere()) : new NullLiteralExpr();
        getChainedMethodCallExprFrom("withSqlWhere", initializer).setArgument(0, sqlWhereExpression);
        return toReturn;
    }
}
