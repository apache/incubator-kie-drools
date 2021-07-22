/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.parser.impl;

import java.util.Optional;

import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class ParserUtil {

    public static Object literalToValue( LiteralExpr literalExpr) {
        if (literalExpr instanceof StringLiteralExpr ) {
            return literalExpr.asStringLiteralExpr().asString();
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
        return null;
    }

    public static String getLiteralString(Expression expr) {
        Object value = getLiteralValue(expr);
        return value instanceof String ? (String) value : null;
    }

    public static Object getLiteralValue(Expression expr) {
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
                return getLiteralValue(mce.getArgument(0));
            }
        }
        return null;
    }

    public static boolean isLiteral(Class<?> clazz) {
        return clazz == String.class || clazz == Integer.class || clazz == Long.class || clazz == Double.class;
    }
}
