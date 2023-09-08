/**
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
package org.drools.impact.analysis.parser.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.getClassFromType;

public class ParserUtil {

    public static Object literalToValue( LiteralExpr literalExpr) {
        if (literalExpr instanceof StringLiteralExpr ) {
            return literalExpr.asStringLiteralExpr().asString();
        }
        if (literalExpr instanceof CharLiteralExpr ) {
            return literalExpr.asCharLiteralExpr().asChar();
        }
        if (literalExpr instanceof IntegerLiteralExpr ) {
            return literalExpr.asIntegerLiteralExpr().asInt();
        }
        if (literalExpr instanceof LongLiteralExpr ) {
            return literalExpr.asLongLiteralExpr().asLong();
        }
        if (literalExpr instanceof DoubleLiteralExpr ) {
            return literalExpr.asDoubleLiteralExpr().asDouble();
        }
        if (literalExpr instanceof BooleanLiteralExpr ) {
            return literalExpr.asBooleanLiteralExpr().getValue();
        }
        if (literalExpr instanceof BigDecimalLiteralExpr) {
            return ((BigDecimalLiteralExpr)literalExpr).asBigDecimal();
        }
        if (literalExpr instanceof BigIntegerLiteralExpr) {
            return ((BigIntegerLiteralExpr)literalExpr).asBigInteger();
        }
        return null;
    }

    public static Object objectCreationExprToValue(ObjectCreationExpr objectCreationExpr, RuleContext context) {
        // Only a few classes/constructors are handled. Otherwise, value becomes null so a link would be UNKNOWN. To be enhanced : DROOLS-6711
        ClassOrInterfaceType type = objectCreationExpr.getType();
        Class<?> clazz = getClassFromType(context.getTypeResolver(), type);
        if (clazz.equals(BigDecimal.class)) {
            NodeList<Expression> arguments = objectCreationExpr.getArguments();
            Optional<Object> opt = arguments.stream()
                                            .findFirst()
                                            .filter(StringLiteralExpr.class::isInstance)
                                            .map(literalExpr -> literalExpr.asStringLiteralExpr().asString())
                                            .map(BigDecimal::new);
            return opt.orElse(null);
        } else if (clazz.equals(BigInteger.class)) {
            NodeList<Expression> arguments = objectCreationExpr.getArguments();
            Optional<Object> opt = arguments.stream()
                                            .findFirst()
                                            .filter(StringLiteralExpr.class::isInstance)
                                            .map(literalExpr -> literalExpr.asStringLiteralExpr().asString())
                                            .map(BigInteger::new);
            return opt.orElse(null);
        }
        return null;
    }

    public static Class<?> literalType( LiteralExpr literalExpr) {
        if (literalExpr instanceof StringLiteralExpr ) {
            return String.class;
        }
        if (literalExpr instanceof IntegerLiteralExpr ) {
            return Integer.class;
        }
        if (literalExpr instanceof LongLiteralExpr ) {
            return Long.class;
        }
        if (literalExpr instanceof DoubleLiteralExpr ) {
            return Double.class;
        }
        if (literalExpr instanceof BooleanLiteralExpr ) {
            return Boolean.class;
        }
        return null;
    }

    public static String getLiteralString(RuleContext context, Expression expr) {
        Object value = getLiteralValue(context, expr);
        return value instanceof String ? (String) value : null;
    }

    public static Object getLiteralValue(RuleContext context, Expression expr) {
        if (expr.isLiteralExpr()) {
            return literalToValue(expr.asLiteralExpr());
        } else if (expr.isMethodCallExpr()) {
            MethodCallExpr mce = expr.asMethodCallExpr();
            Optional<SimpleName> optString = mce.getScope()
                                                .filter(Expression::isNameExpr)
                                                .map(Expression::asNameExpr)
                                                .map(NameExpr::getName)
                                                .filter(name -> name.asString().equals("java.lang.String")); // only work with String for now
            if (optString.isPresent() && mce.getName().asString().equals("valueOf")) {
                return getLiteralValue(context, mce.getArgument(0));
            }
        } else if (expr.isNameExpr()) {
            return ((ImpactAnalysisRuleContext)context).getBindVariableLiteralMap().get(expr.asNameExpr().getNameAsString());
        }
        return null;
    }

    public static boolean isLiteral(Class<?> clazz) {
        return clazz == String.class || clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Boolean.class;
    }

    public static Expression stripEnclosedAndCast(Expression expr) {
        if (expr.isEnclosedExpr()) {
            expr = expr.asEnclosedExpr().getInner();
        }
        if (expr.isCastExpr()) {
            expr = expr.asCastExpr().getExpression();
        }
        return expr;
    }
}
