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
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.nameExprToMethodCallExprWithCast;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.returnTypeOfMethodCallExpr;

public class ToMethodCall {

    private final RuleContext context;
    private final TypeResolver typeResolver;

    public ToMethodCall(RuleContext context) {
        this.context = context;
        this.typeResolver = context.getTypeResolver();
    }

    public ToMethodCall(TypeResolver typeResolver) {
        this.context = null;
        this.typeResolver = typeResolver;
    }

    java.lang.reflect.Type previousClass;
    Expression previousScope;

    public TypedExpression toMethodCallWithClassCheck(Expression expr, String bindingId, Class<?> clazz) {
        Deque<ParsedMethod> createExpressionCallLeftToRight = createExpressionCallLeftToRight(expr);

        previousClass = clazz; // Start from input class

        for (ParsedMethod e : createExpressionCallLeftToRight) {
            if (e.expression instanceof EnclosedExpr) { // inline cast
                setCursorForEnclosedExpr(e);
            } else if (e.expression instanceof MethodCallExpr) {
                setCursorForMethodCall(e);
            } else {
                convertNameToMethod(bindingId, e);
            }
        }

        return new TypedExpression(previousScope, previousClass);
    }

    // do not use this, use needConversion
    private boolean needConversionRec(Expression expression) {
        if(expression.isCastExpr()) {
            return needConversionRec(expression.asCastExpr().getExpression());
        } else if (expression.isEnclosedExpr()) {
            return needConversionRec(expression.asEnclosedExpr().getInner());
        } else {
            return expression instanceof NameExpr || expression instanceof FieldAccessExpr || expression instanceof NullSafeFieldAccessExpr;
        }
    }

    private boolean needConversion(Expression expression) {
        return needConversionRec(expression);
    }


    private void setCursorForEnclosedExpr(ParsedMethod e) {
        java.lang.reflect.Type returnType = e.castType
                .flatMap(t -> safeResolveType(typeResolver, t.asString()))
                .orElseThrow(() -> new CannotResolveTypeException(e));

        EnclosedExpr enclosedExpr = (EnclosedExpr) e.expression;

        if(enclosedExpr.getInner().isCastExpr()) {
            CastExpr castExpr = enclosedExpr.getInner().asCastExpr();
            Type castType = castExpr.getType();

            if(needConversion(e.expression)) {
                TypedExpression te = nameExprToMethodCallExprWithCast(e.fieldToResolve, previousClass, previousScope, castType);
                previousScope = te.getExpression();
            } else {
                previousScope = e.expression;
            }
        }

        previousClass = returnType;
    }

    private void setCursorForMethodCall(ParsedMethod e) {
        java.lang.reflect.Type returnType = returnTypeOfMethodCallExpr(context, typeResolver, (MethodCallExpr) e.expression, previousClass, null);
        previousScope = ((MethodCallExpr) e.expression.clone()).setScope(previousScope);
        previousClass = returnType;
    }

    private void convertNameToMethod(String bindingId, ParsedMethod e) {
        if (e.fieldToResolve.equals(bindingId)) {
            return;
        }
        if (previousClass == null) {
            setCursorForMissingClass(e);
        } else {
            TypedExpression te = nameExprToMethodCallExpr(e.fieldToResolve, previousClass, previousScope);
            if (te == null) {
                throw new CannotConvertException(new InvalidExpressionErrorResult("Unknown field " + e.fieldToResolve + " on " + previousClass));
            }

            previousScope = te.getExpression();
            previousClass = te.getType();
        }
    }

    private void setCursorForMissingClass(ParsedMethod e) {
        try {
            previousClass = typeResolver.resolveType(e.fieldToResolve);
            previousScope = new NameExpr(e.fieldToResolve);
        } catch (ClassNotFoundException e1) {
            // ignore
        }
        if (previousClass == null) {
            previousClass = context.getDeclarationById(e.fieldToResolve)
                    .map(DeclarationSpec::getDeclarationClass)
                    .orElseThrow(() -> new RuntimeException("Unknown field: " + e.fieldToResolve));
            previousScope = e.expression;
        }
    }

    private Deque<ParsedMethod> createExpressionCallLeftToRight(Expression expr) {
        final Deque<ParsedMethod> callStackLeftToRight = new LinkedList<>();
        createExpressionCallRec(expr, callStackLeftToRight);
        return callStackLeftToRight;
    }

    private static void createExpressionCallRec(Expression expr, Deque<ParsedMethod> expressions) {

        if (expr instanceof NodeWithSimpleName) {
            NodeWithSimpleName<?> fae = (NodeWithSimpleName<?>) expr;
            expressions.push(new ParsedMethod(expr, fae.getName().asString()));
        } else if (expr instanceof InlineCastExpr) {
            InlineCastExpr inlineCastExpr = (InlineCastExpr) expr;
            Type castType = inlineCastExpr.getType();
            Expression originalExpression = inlineCastExpr.getExpression();
            EnclosedExpr newExpression = new EnclosedExpr(new CastExpr(castType, originalExpression));
            expressions.push(new ParsedMethod(newExpression, originalExpression.toString()).setCastType(Optional.of(castType)));
        }

        if (expr instanceof NodeWithOptionalScope) {
            final NodeWithOptionalScope<?> exprWithScope = (NodeWithOptionalScope<?>) expr;
            exprWithScope.getScope().ifPresent(expression -> createExpressionCallRec(expression, expressions));
        } else if (expr instanceof FieldAccessExpr) {
            // Cannot recurse over getScope() as FieldAccessExpr doesn't support the NodeWithOptionalScope,
            // it will support a new interface to traverse among scopes called NodeWithTraversableScope so
            // we can merge this and the previous branch
            createExpressionCallRec(((FieldAccessExpr) expr).getScope(), expressions);
        }
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

    public class CannotConvertException extends RuntimeException {

        private InvalidExpressionErrorResult invalidExpressionErrorResult;

        public CannotConvertException(InvalidExpressionErrorResult invalidExpressionErrorResult) {
            this.invalidExpressionErrorResult = invalidExpressionErrorResult;
        }

        public InvalidExpressionErrorResult getInvalidExpressionErrorResult() {
            return invalidExpressionErrorResult;
        }
    }

    public class CannotResolveTypeException extends RuntimeException {

        private ParsedMethod parsedMethod;

        public CannotResolveTypeException(ParsedMethod parsedMethod) {
            this.parsedMethod = parsedMethod;
        }
    }
}
