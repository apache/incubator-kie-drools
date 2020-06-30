/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.Type;
import org.drools.core.addon.TypeResolver;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.nameExprToMethodCallExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.returnTypeOfMethodCallExpr;

public class ToMethodCall {

    public static TypedExpression toMethodCallWithClassCheck(RuleContext context, Expression expr, String bindingId, Class<?> clazz, TypeResolver typeResolver) {

        final Deque<ParsedMethod> callStackLeftToRight = new LinkedList<>();

        createExpressionCall(expr, callStackLeftToRight);

        java.lang.reflect.Type previousClass = clazz;
        Expression previousScope = null;

        for (ParsedMethod e : callStackLeftToRight) {
            if (e.expression instanceof NameExpr || e.expression instanceof FieldAccessExpr || e.expression instanceof NullSafeFieldAccessExpr) {
                if (e.fieldToResolve.equals( bindingId )) {
                    continue;
                }
                if (previousClass == null) {
                    try {
                        previousClass = typeResolver.resolveType( e.fieldToResolve );
                        previousScope = new NameExpr( e.fieldToResolve );
                    } catch (ClassNotFoundException e1) {
                        // ignore
                    }
                    if (previousClass == null) {
                        previousClass = context.getDeclarationById( e.fieldToResolve )
                                .map( DeclarationSpec::getDeclarationClass )
                                .orElseThrow( () -> new RuntimeException( "Unknown field: " + e.fieldToResolve ) );
                        previousScope = e.expression;
                    }
                } else {
                    TypedExpression te = nameExprToMethodCallExpr( e.fieldToResolve, previousClass, previousScope );
                    if (te == null) {
                        context.addCompilationError( new InvalidExpressionErrorResult("Unknown field " + e.fieldToResolve + " on " + previousClass ) );
                        return null;
                    }
                    java.lang.reflect.Type returnType = e.castType.flatMap(t -> safeResolveType(typeResolver, t.asString())).orElse(te.getType());
                    previousScope = te.getExpression();
                    previousClass = returnType;
                }
            } else if (e.expression instanceof MethodCallExpr) {
                java.lang.reflect.Type returnType = returnTypeOfMethodCallExpr(context, typeResolver, (MethodCallExpr) e.expression, previousClass, null);
                MethodCallExpr cloned = ((MethodCallExpr) e.expression.clone()).setScope(previousScope);
                previousScope = cloned;
                previousClass = returnType;
            } else if (e.expression instanceof EnclosedExpr) { // inline cast
                java.lang.reflect.Type returnType = e.castType.flatMap(t -> safeResolveType(typeResolver, t.asString())).orElseThrow(() -> new RuntimeException());
                previousScope = e.expression;
                previousClass = returnType;
            }
        }

        return new TypedExpression(previousScope, previousClass);
    }

    private static Expression createExpressionCall(Expression expr, Deque<ParsedMethod> expressions) {

        if (expr instanceof NodeWithSimpleName) {
            NodeWithSimpleName fae = (NodeWithSimpleName)expr;
            expressions.push(new ParsedMethod(expr, fae.getName().asString()));
        } else if (expr instanceof InlineCastExpr) {
            InlineCastExpr inlineCastExpr = (InlineCastExpr) expr;
            Type castType = inlineCastExpr.getType();
            Expression originalExpression = inlineCastExpr.getExpression();
            EnclosedExpr newExpression = new EnclosedExpr(new CastExpr(castType, originalExpression));
            expressions.push(new ParsedMethod(newExpression, originalExpression.toString()).setCastType(Optional.of(castType)));
        }

        if (expr instanceof NodeWithOptionalScope) {
            final NodeWithOptionalScope<?> exprWithScope = (NodeWithOptionalScope) expr;
            exprWithScope.getScope().ifPresent(expression -> createExpressionCall(expression, expressions));
        } else if (expr instanceof FieldAccessExpr) {
            // Cannot recurse over getScope() as FieldAccessExpr doesn't support the NodeWithOptionalScope,
            // it will support a new interface to traverse among scopes called NodeWithTraversableScope so
            // we can merge this and the previous branch
            createExpressionCall(((FieldAccessExpr) expr).getScope(), expressions);
        }

        return expr;
    }

    static class ParsedMethod {
        final Expression expression;

        final String fieldToResolve;

        Optional<Type> castType = Optional.empty();

        public ParsedMethod(Expression expression, String fieldToResolve) {
            this.expression = expression;
            this.fieldToResolve = fieldToResolve;
        }

        public ParsedMethod setCastType(Optional<Type> castType) {
            this.castType = castType;
            return this;
        }

        @Override
        public String toString() {
            return "{" +
                    "expression=" + expression +
                    ", fieldToResolve='" + fieldToResolve + '\'' +
                    ", castType='" + castType + '\'' +
                    '}';
        }
    }

    private static Optional<java.lang.reflect.Type> safeResolveType(TypeResolver typeResolver, String typeName) {
        try {
            return Optional.of(typeResolver.resolveType(typeName));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }


}
