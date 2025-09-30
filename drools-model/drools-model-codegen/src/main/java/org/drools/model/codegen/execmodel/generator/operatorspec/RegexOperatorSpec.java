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
import com.github.javaparser.ast.expr.*;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.RegexExpr;

import java.util.regex.Pattern;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.util.StringUtils.md5Hash;

public class RegexOperatorSpec extends NativeOperatorSpec {
    public static final RegexOperatorSpec INSTANCE = new RegexOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        if (shouldInlineRegex(pointFreeExpr.getRight())) {
            return super.getExpression(context, pointFreeExpr, left, expressionTyper);
        } else {
            // TODO(?) throw some exception instead of creating regexp_failed_to_parse expressions
            Expression regexExpression = pointFreeExpr.getRight().getFirst()
                    .orElse(new StringLiteralExpr("regexp_failed_to_parse"));

            NameExpr regexFieldName = new NameExpr("regexp_" + md5Hash(regexExpression.toString()));

            return new RegexExpr(
                    getClassMemberFieldDeclaration(regexFieldName, regexExpression),
                    getClassMemberInvocationExpression(pointFreeExpr, left, regexFieldName)
            );
        }
    }

    private EnclosedExpr getClassMemberInvocationExpression(PointFreeExpr pointFreeExpr, TypedExpression left, NameExpr regexFieldName) {
        // we need to cast the left expression to CharSequence, otherwise it might fail in cases of missing type safety
        // e.g. test method parameters (_this) is of a raw type Map
        EnclosedExpr typeSafeStringToMatch = new EnclosedExpr(new CastExpr(toClassOrInterfaceType(CharSequence.class), left.getExpression()));

        MethodCallExpr matchesCallExpr = new MethodCallExpr(
                new MethodCallExpr(regexFieldName, "matcher", NodeList.nodeList(typeSafeStringToMatch)),
                "matches"
        );

        // null string should not match
        BinaryExpr compoundRegexCallExpr = new BinaryExpr(
                new BinaryExpr(left.getExpression(), new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS),
                pointFreeExpr.isNegated() ? new UnaryExpr(matchesCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT) : matchesCallExpr,
                BinaryExpr.Operator.AND
        );

        return new EnclosedExpr(compoundRegexCallExpr);
    }

    private FieldDeclaration getClassMemberFieldDeclaration(NameExpr regexFieldName, Expression regexExpression) {

        MethodCallExpr compiledRegexField = new MethodCallExpr(
                new NameExpr(Pattern.class.getName()), "compile", NodeList.nodeList(regexExpression)
        );

        VariableDeclarator variableDeclarator = new VariableDeclarator(
                toClassOrInterfaceType(Pattern.class),
                regexFieldName.getName(),
                compiledRegexField
        );

        return new FieldDeclaration(
                NodeList.nodeList(Modifier.privateModifier(), Modifier.staticModifier(), Modifier.finalModifier()),
                variableDeclarator
        );
    }

    /**
     * Recursively check if the regexPatternExpression contains a method call on the fact.
     * If so, we consider it a dynamic regex, and cannot inline the regex expression.
     *
     * @param regexPatternExpression the regex pattern expression under analysis
     * @return true if the regex pattern expression is dynamic, false otherwise
     */
    private boolean shouldInlineRegex(NodeList<Expression> regexPatternExpression) {
        for (Expression expression : regexPatternExpression) {
            if (expression instanceof MethodCallExpr methodCallExpr) {
                boolean isInFactScope = methodCallExpr.getScope()
                        .filter(Expression::isNameExpr)
                        .map(Expression::asNameExpr)
                        .map(NameExpr::getNameAsString)
                        .filter("_this"::equals)
                        .isPresent();

                return isInFactScope || shouldInlineRegex(methodCallExpr.getArguments());
            }

            return false;
        }

        return false;
    }
}
