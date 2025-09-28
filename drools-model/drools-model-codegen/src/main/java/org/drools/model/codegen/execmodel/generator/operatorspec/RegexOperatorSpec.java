/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.operatorspec;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.RegexExpr;

import java.util.regex.Pattern;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class RegexOperatorSpec implements OperatorSpec {
    public static final RegexOperatorSpec INSTANCE = new RegexOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        StringLiteralExpr regexLiteral = pointFreeExpr.getRight().getFirst()
                .filter(Expression::isStringLiteralExpr)
                .map(Expression::asStringLiteralExpr)
                .orElse(new StringLiteralExpr("regexp_failed_to_parse"));

        NameExpr regexFieldName = pointFreeExpr.getRight().getFirst()
                .filter(Expression::isStringLiteralExpr)
                .map(Expression::asStringLiteralExpr)
                .map(StringLiteralExpr::asString)
                .map(s -> "regexp_" + s.hashCode())
                .map(NameExpr::new)
                .orElse(new NameExpr("regexp_failed_to_parse"));

        MethodCallExpr compiledRegexField = new MethodCallExpr(
                new NameExpr(Pattern.class.getName()), "compile", NodeList.nodeList(regexLiteral)
        );

        VariableDeclarator variableDeclarator = new VariableDeclarator(
                toClassOrInterfaceType(Pattern.class),
                regexFieldName.getName(),
                compiledRegexField
        );

        FieldDeclaration compiledRegexMember = new FieldDeclaration(
                NodeList.nodeList(Modifier.privateModifier(), Modifier.staticModifier(), Modifier.finalModifier()),
                variableDeclarator
        );

        MethodCallExpr matchesCallExpr = new MethodCallExpr(
                new MethodCallExpr(regexFieldName, "matcher", NodeList.nodeList(left.getExpression())),
                "matches"
        );

        return new RegexExpr(compiledRegexMember, matchesCallExpr);
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
