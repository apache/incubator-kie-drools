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

package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.core.addon.TypeResolver;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;

public class FlattenScope {

    public static List<Node> flattenScope( TypeResolver typeResolver, Expression expressionWithScope ) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FieldAccessExpr) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope( typeResolver, fieldAccessExpr.getScope() ));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof NullSafeFieldAccessExpr) {
            NullSafeFieldAccessExpr fieldAccessExpr = (NullSafeFieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope( typeResolver, fieldAccessExpr.getScope() ));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                Expression scope = methodCallExpr.getScope().get();
                if (isFullyQualifiedClassName( typeResolver, scope )) {
                    res.add(scope);
                } else {
                    res.addAll( flattenScope( typeResolver, scope ) );
                }
            }
            res.add(methodCallExpr);
        } else if (expressionWithScope instanceof NullSafeMethodCallExpr) {
            NullSafeMethodCallExpr methodCallExpr = (NullSafeMethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(typeResolver, methodCallExpr.getScope().orElseThrow(() -> new IllegalStateException("Scope expression is not present!"))));
            }
            res.add(methodCallExpr);
        } else if (expressionWithScope instanceof InlineCastExpr && ((InlineCastExpr) expressionWithScope).getExpression() instanceof FieldAccessExpr) {
            InlineCastExpr inlineCastExpr = (InlineCastExpr) expressionWithScope;
            Expression internalScope = ((FieldAccessExpr) inlineCastExpr.getExpression()).getScope();
            res.addAll(flattenScope( typeResolver, internalScope ));
            res.add(expressionWithScope);
        } else if (expressionWithScope instanceof ArrayAccessExpr) {
            ArrayAccessExpr arrayAccessExpr = (ArrayAccessExpr) expressionWithScope;
            res.addAll(flattenScope( typeResolver, arrayAccessExpr.getName()) );
            res.add(arrayAccessExpr);
        } else {
            res.add(expressionWithScope);
        }
        return res;
    }

    private static boolean isFullyQualifiedClassName( TypeResolver typeResolver, Expression scope ) {
        if (scope instanceof FieldAccessExpr ) {
            try {
                typeResolver.resolveType( scope.toString() );
                return true;
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        return false;
    }

    private FlattenScope() {
        // It is not allowed to create instances of util classes.
    }
}
