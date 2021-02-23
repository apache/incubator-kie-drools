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

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.kie.dmn.feel.codegen.feel11.Expressions.compiledFeelSemanticMappingsFQN;

public class FeelCtx {

    public static final String FEELCTX_N = "feelExprCtx";
    public static final NameExpr FEELCTX = new NameExpr(FEELCTX_N);
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
