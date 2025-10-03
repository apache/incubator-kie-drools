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
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
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
        if (canPrecompileRegex(pointFreeExpr.getRight())) {
            Expression regexExpression = pointFreeExpr.getRight().getFirst()
                    .orElseThrow(() -> new IllegalStateException("Failed to parse regex expression"));

            NameExpr regexFieldName = new NameExpr("regexp_" + md5Hash(regexExpression.toString()));

            return new RegexExpr(
                    getClassMemberFieldDeclaration(regexFieldName, regexExpression),
                    getClassMemberInvocationExpression(pointFreeExpr, left, regexFieldName)
            );
        } else {
            return super.getExpression(context, pointFreeExpr, left, expressionTyper);
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

    private boolean canPrecompileRegex(NodeList<Expression> regexPatternExpression) {
        return regexPatternExpression.size() == 1 &&
                regexPatternExpression.getFirst()
                        .filter(StringLiteralExpr.class::isInstance)
                        .isPresent();
    }
}
