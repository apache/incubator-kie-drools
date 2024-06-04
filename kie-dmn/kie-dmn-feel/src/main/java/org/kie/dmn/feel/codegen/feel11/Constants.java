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
package org.kie.dmn.feel.codegen.feel11;

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
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.ast.RangeNode;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BIGDECIMAL_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FUNCTION_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.RANGEBOUNDARY_S;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.UNARYTEST_CT;

public class Constants {

    public static final Expression DECIMAL_128_E = parseExpression("java.math.MathContext.DECIMAL128");
    public static final Expression BUILTINTYPE_E = parseExpression("org.kie.dmn.feel.lang.types.BuiltInType");

    public static FieldDeclaration of(Type type, String name, Expression initializer) {
        return new FieldDeclaration(
                NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier(), Modifier.finalModifier()),
                new VariableDeclarator(type, name, initializer));
    }

    public static FieldDeclaration numeric(String name, String numericValue) {
        ObjectCreationExpr initializer = new ObjectCreationExpr();
        initializer.setType(BIGDECIMAL_CT);
        String originalText = numericValue;
        try {
            Long.parseLong(originalText);
            initializer.addArgument(originalText.replaceFirst("^0+(?!$)", "")); // see NumberEvalHelper.getBigDecimalOrNull
        } catch (Throwable t) {
            initializer.addArgument(new StringLiteralExpr(originalText));
        }
        initializer.addArgument(DECIMAL_128_E);
        return of(BIGDECIMAL_CT, name, initializer);
    }

    public static String numericName(String originalText) {
        return "K_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration unaryTest(String name, LambdaExpr value) {
        return of(UNARYTEST_CT, name, value);
    }

    public static String unaryTestName(String originalText) {
        return "UT_" + CodegenStringUtil.escapeIdentifier(originalText);
    }

    public static FieldDeclaration function(String name, LambdaExpr value) {
        return of(FUNCTION_CT, name, value);
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
                new NameExpr(RANGEBOUNDARY_S),
                boundary == RangeNode.IntervalBoundary.OPEN ? "OPEN" : "CLOSED");
    }
}
