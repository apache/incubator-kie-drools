/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 *
 */

package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;

public class Constants {

    public static final Expression DECIMAL_128 = parseExpression("java.math.MathContext.DECIMAL128");
    public static final ClassOrInterfaceType BigDecimalT = parseClassOrInterfaceType(BigDecimal.class.getCanonicalName());
    public static final ClassOrInterfaceType BooleanT = parseClassOrInterfaceType(Boolean.class.getCanonicalName());
    private static final Type ListT = parseType(List.class.getCanonicalName());
    public static final ClassOrInterfaceType UnaryTestT = parseClassOrInterfaceType(UnaryTest.class.getCanonicalName());
    public static final String RangeBoundary = Range.RangeBoundary.class.getCanonicalName();
    public static final Expression BuiltInTypeT = parseExpression("org.kie.dmn.feel.lang.types.BuiltInType");
    public static final ClassOrInterfaceType FunctionT = parseClassOrInterfaceType("java.util.function.Function<EvaluationContext, Object>");

    public static FieldDeclaration of(Type type, String name, Expression initializer) {
        return new FieldDeclaration(
                NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier(), Modifier.finalModifier()),
                new VariableDeclarator(type, name, initializer));
    }

    public static FieldDeclaration numeric(String name, String numericValue) {
        ObjectCreationExpr initializer = new ObjectCreationExpr();
        initializer.setType(BigDecimalT);
        String originalText = numericValue;
        try {
            Long.parseLong(originalText);
            initializer.addArgument(originalText.replaceFirst("^0+(?!$)", "")); // see EvalHelper.getBigDecimalOrNull
        } catch (Throwable t) {
            initializer.addArgument(new StringLiteralExpr(originalText));
        }
        initializer.addArgument(DECIMAL_128);
        return of(BigDecimalT, name, initializer);
    }

    public static String numericName(String originalText) {
        return "K_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration unaryTest(String name, LambdaExpr value) {
        return of(UnaryTestT, name, value);
    }

    public static String unaryTestName(String originalText) {
        return "UT_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration function(String name, LambdaExpr value) {
        return of(FunctionT, name, value);
    }

    public static String functionName(String originalText) {
        return "ZZFN_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration dtConstant(String name, Expression initializer) {
        return of(parseClassOrInterfaceType(Object.class.getName()), name, initializer);
    }

    public static String dtConstantName(String originalText) {
        return "K_DT_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldAccessExpr rangeBoundary(RangeNode.IntervalBoundary boundary) {
        return new FieldAccessExpr(
                new NameExpr(RangeBoundary),
                boundary == RangeNode.IntervalBoundary.OPEN ? "OPEN" : "CLOSED");
    }
}
