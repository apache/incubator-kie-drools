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
package org.drools.model.codegen.execmodel.generator.expressiontyper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import org.drools.util.TypeResolver;
import org.drools.mvel.parser.ast.expr.FullyQualifiedInlineCastExpr;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;
import org.drools.mvel.parser.printer.PrintUtil;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class FlattenScope {

    public static List<Node> flattenScope( TypeResolver typeResolver, Expression expressionWithScope ) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FullyQualifiedInlineCastExpr) {
            res.addAll( flattenScope( typeResolver, transformFullyQualifiedInlineCastExpr( typeResolver, (FullyQualifiedInlineCastExpr) expressionWithScope ) ) );
        } else if (expressionWithScope instanceof FieldAccessExpr) {
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
        } else if (expressionWithScope instanceof EnclosedExpr) {
            res.addAll(flattenScope(typeResolver, ((EnclosedExpr) expressionWithScope).getInner()));
        } else {
            res.add(expressionWithScope);
        }
        return res;
    }

    public static Expression transformFullyQualifiedInlineCastExpr( TypeResolver typeResolver, FullyQualifiedInlineCastExpr fqInlineCastExpr ) {
        final Deque<String> expressionNamesWithoutType = new ArrayDeque<>();
        String className = findClassName(fqInlineCastExpr.getName(), typeResolver, expressionNamesWithoutType);
        Expression scope = fqInlineCastExpr.getScope();
        if (scope instanceof FullyQualifiedInlineCastExpr) {
            scope = transformFullyQualifiedInlineCastExpr( typeResolver, (FullyQualifiedInlineCastExpr) scope );
        }

        Expression expr = new InlineCastExpr( toClassOrInterfaceType(className), scope );
        // last element is the one with the actual arguments and need a special case
        if(!expressionNamesWithoutType.isEmpty()) {
            String lastElement = expressionNamesWithoutType.removeLast();

            // the others will be considered FieldAccessExpr
            for(String s : expressionNamesWithoutType) {
                expr = new FieldAccessExpr(expr, s);
            }
            if ( fqInlineCastExpr.hasArguments() ) {
                expr = new MethodCallExpr( expr, lastElement, fqInlineCastExpr.getArguments() );
            } else {
                expr = new FieldAccessExpr( expr, lastElement );
            }
        }

        return expr;
    }

    private static String findClassName(Name name, TypeResolver typeResolver, Deque<String> remainings) {
        return findClassNameRec(Optional.of(name), typeResolver, remainings).orElseThrow( () -> new RuntimeException("Cannot find class name in " + name.asString()));
    }

    private static Optional<String> findClassNameRec(Optional<Name> optQualifier, TypeResolver typeResolver, Deque<String> remainings) {
        return optQualifier.flatMap( qualifier -> {
            try {
                String possibleClassName = qualifier.asString();
                typeResolver.resolveType(possibleClassName);
                return Optional.of(possibleClassName);
            } catch (ClassNotFoundException e) {
                remainings.push(qualifier.getIdentifier());
                return findClassNameRec(qualifier.getQualifier(), typeResolver, remainings);
            }
        } );
    }

    private static boolean isFullyQualifiedClassName( TypeResolver typeResolver, Expression scope ) {
        if (scope instanceof FieldAccessExpr ) {
            try {
                typeResolver.resolveType( PrintUtil.printNode(scope) );
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
