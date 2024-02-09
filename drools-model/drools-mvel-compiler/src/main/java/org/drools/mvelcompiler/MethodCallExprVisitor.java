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
package org.drools.mvelcompiler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.Pair;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.DeclaredFunction;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.util.MethodResolutionUtils;
import org.drools.mvelcompiler.util.VisitorContext;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.drools.util.StreamUtils.optionalToStream;

public class MethodCallExprVisitor implements DrlGenericVisitor<TypedExpression, VisitorContext> {

    final RHSPhase parentVisitor;
    final MvelCompilerContext mvelCompilerContext;

    public MethodCallExprVisitor(RHSPhase parentVisitor, MvelCompilerContext mvelCompilerContext) {
        this.parentVisitor = parentVisitor;
        this.mvelCompilerContext = mvelCompilerContext;
    }

    @Override
    public TypedExpression defaultMethod(Node n, VisitorContext context) {
        return n.accept(parentVisitor, context);
    }

    @Override
    public TypedExpression visit(final MethodCallExpr n, final VisitorContext arg) {
        final Optional<TypedExpression> scope = n.getScope().map(s -> s.accept(this, arg));
        final TypedExpression name = n.getName().accept(this, new VisitorContext(scope.orElse(null)));
        final Pair<List<TypedExpression>, List<Integer>> typedArgumentsResult =
                MethodResolutionUtils.getTypedArgumentsWithEmptyCollectionArgumentDetection(n.getArguments(), this, arg);
        return parseMethodFromDeclaredFunction(n, typedArgumentsResult.a)
                .orElseGet(() -> parseMethod(n, scope, name, typedArgumentsResult.a, typedArgumentsResult.b));
    }

    private Optional<TypedExpression> parseMethodFromDeclaredFunction(MethodCallExpr n, List<TypedExpression> arguments) {

        Optional<DeclaredFunction> optDeclaredFunction = mvelCompilerContext.findDeclaredFunction(n.getNameAsString());

        if(optDeclaredFunction.isEmpty()) return Optional.empty();

        DeclaredFunction declaredFunction = optDeclaredFunction.get();

        Optional<Class<?>> methodReturnType = declaredFunction.findReturnType();
        List<Class<?>> actualArgumentTypes = declaredFunction.findArgumentsType();

        return methodReturnType.map(t -> new MethodCallExprT(n.getName().asString(), Optional.empty(), arguments,
                                                             actualArgumentTypes, Optional.of(t)));
    }

    private MethodCallExprT parseMethod(MethodCallExpr n,
                                        Optional<TypedExpression> scope,
                                        TypedExpression name,
                                        List<TypedExpression> arguments,
                                        List<Integer> emptyCollectionArgumentIndexes) {
        Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodResult =
                MethodResolutionUtils.resolveMethod(n, mvelCompilerContext, scope, arguments);
        // This is a workaround for mvel empty list and map ambiguity, please see the description in getTypedArguments() method.
        if (resolveMethodResult.a.isEmpty() && !emptyCollectionArgumentIndexes.isEmpty()) {
            resolveMethodResult = MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(n, mvelCompilerContext, resolveMethodResult.b, arguments, emptyCollectionArgumentIndexes);
        }
        final Optional<Method> finalMethod = resolveMethodResult.a;
        final Optional<TypedExpression> finalScope = resolveMethodResult.b;
        final Optional<Type> methodReturnType = name.getType().or(() -> finalMethod.map(Method::getReturnType));
        final List<Class<?>> actualArgumentType = optionalToStream(finalMethod)
                .flatMap((Method m) -> Arrays.stream(m.getParameterTypes()))
                .collect(Collectors.toList());

        return new MethodCallExprT(n.getName().asString(), finalScope, arguments,
                                   actualArgumentType, methodReturnType);
    }
}
