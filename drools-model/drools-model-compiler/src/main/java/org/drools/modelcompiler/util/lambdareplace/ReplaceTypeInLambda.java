/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACCUMULATE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EVAL_EXPR_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXPR_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;

public class ReplaceTypeInLambda {

    private ReplaceTypeInLambda() {

    }

    public static void replaceTypeInExprLambda(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        if (expression instanceof MethodCallExpr && (( MethodCallExpr ) expression).getNameAsString().equals( ACCUMULATE_CALL )) {
            return;
        }

        expression.findAll(MethodCallExpr.class).forEach(mc -> {
            if (mc.getArguments().stream().anyMatch(a -> a.toString().equals(toVar(bindingId)))) {
                List<LambdaExpr> allLambdas = new ArrayList<>();

                if (mc.getNameAsString().equals(EXPR_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                if (mc.getNameAsString().equals(EVAL_EXPR_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Expression> optScope = mc.getScope();
                if (optScope.isPresent() && optScope.get().asMethodCallExpr().getNameAsString().equals(BIND_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Node> optParent = mc.getParentNode(); // In the Pattern DSL they're in the direct pattern
                if (mc.getNameAsString().equals(PATTERN_CALL) && optParent.isPresent()) {
                    List<LambdaExpr> all = expression.findAll(LambdaExpr.class);
                    allLambdas.addAll(all);
                }
                allLambdas.forEach(lambdaExpr -> replaceLambdaParameter(accumulateFunctionResultType, lambdaExpr, bindingId));
            }
        });
    }

    private static void replaceLambdaParameter(Class accumulateFunctionResultType, LambdaExpr lambdaExpr, String bindingId) {
        for (Parameter a : lambdaExpr.getParameters()) {

            if (!a.getType().isUnknownType() &&
                    (a.getNameAsString().equals("_this") || a.getNameAsString().equals(bindingId))) {
                a.setType( toClassOrInterfaceType(accumulateFunctionResultType) );
            }
        }
    }
}
