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

import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
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

}
