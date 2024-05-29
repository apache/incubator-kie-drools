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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.runtime.FEELFunction;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.kie.dmn.feel.codegen.feel11.Expressions.compiledFeelSemanticMappingsFQN;

public class FeelCtx {

    public static final String FEELCTX_N = "feelExprCtx";
    public static final NameExpr FEELCTX = new NameExpr(FEELCTX_N);
    public static final ClassOrInterfaceType BOOLEAN_T = parseClassOrInterfaceType(Boolean.class.getCanonicalName());
    public static final String STATIC_EVALUATION = "staticEvaluation";
    public static final ClassOrInterfaceType BIG_DECIMAL_T = parseClassOrInterfaceType(BigDecimal.class.getCanonicalName());
    public static final NameExpr BIG_DECIMAL_N = new NameExpr(BigDecimal.class.getCanonicalName());
    public static final String VALUE_OF = "valueOf";
    public static final ClassOrInterfaceType STRING_T = parseClassOrInterfaceType(String.class.getCanonicalName());
    public static final ClassOrInterfaceType LIST_T = parseClassOrInterfaceType(List.class.getCanonicalName());
    public static final NameExpr ARRAYS_N = new NameExpr(Arrays.class.getCanonicalName());
    public static final String AS_LIST = "asList";
    public static final ClassOrInterfaceType TYPE_T = parseClassOrInterfaceType(Type.class.getCanonicalName());
    public static final ClassOrInterfaceType OBJECT_T = parseClassOrInterfaceType(Object.class.getCanonicalName());
    public static final ClassOrInterfaceType INFIXOPERATOR_T = parseClassOrInterfaceType(InfixOperator.class.getCanonicalName());
    public static final ClassOrInterfaceType PARAM_T = parseClassOrInterfaceType(FEELFunction.Param.class.getCanonicalName());

    private static final String FEEL_SUPPORT = CompiledFEELSupport.class.getSimpleName();
    private static final Expression EMPTY_MAP = parseExpression("java.util.Collections.emptyMap()");

    public static Expression emptyContext() {
        return EMPTY_MAP;
    }

    public static MethodCallExpr getValue(String nameRef) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "getValue", new NodeList<>(FEELCTX, new StringLiteralExpr(nameRef)));
    }

    public static MethodCallExpr current() {
        return new MethodCallExpr(FeelCtx.FEELCTX, "current");
    }

    public static MethodCallExpr openContext() {
        return new MethodCallExpr(
                new NameExpr(FEEL_SUPPORT),
                "openContext")
                .addArgument(FEELCTX);
    }


    public static MethodCallExpr setEntry(String keyText, Expression expression) {
        return new MethodCallExpr(
                null,
                "setEntry",
                new NodeList<>(
                        new StringLiteralExpr(keyText),
                        expression));
    }

    public static MethodCallExpr closeContext(DirectCompilerResult contextEntriesMethodChain) {
        return new MethodCallExpr(
                contextEntriesMethodChain.getExpression(),
                "closeContext");
    }

}
