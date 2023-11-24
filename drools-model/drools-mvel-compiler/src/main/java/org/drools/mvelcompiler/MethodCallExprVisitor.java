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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.Pair;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.util.MethodUtils;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.DeclaredFunction;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassUtils;

import static org.drools.util.StreamUtils.optionalToStream;

public class MethodCallExprVisitor implements DrlGenericVisitor<TypedExpression, RHSPhase.Context> {

    final RHSPhase parentVisitor;
    final MvelCompilerContext mvelCompilerContext;

    public MethodCallExprVisitor(RHSPhase parentVisitor, MvelCompilerContext mvelCompilerContext) {
        this.parentVisitor = parentVisitor;
        this.mvelCompilerContext = mvelCompilerContext;
    }

    @Override
    public TypedExpression defaultMethod(Node n, RHSPhase.Context context) {
        return n.accept(parentVisitor, context);
    }

    @Override
    public TypedExpression visit(final MethodCallExpr n, final RHSPhase.Context arg) {
        final Optional<TypedExpression> scope = n.getScope().map(s -> s.accept(this, arg));
        final TypedExpression name = n.getName().accept(this, new RHSPhase.Context(scope.orElse(null)));
        final Pair<List<TypedExpression>, List<Integer>> typedArgumentsResult = getTypedArguments(n.getArguments(), arg);
        final Class<?>[] argumentsTypes = parametersType(typedArgumentsResult.a);
        return parseMethodFromDeclaredFunction(n, typedArgumentsResult.a)
                .orElseGet(() -> parseMethod(n, scope, name, typedArgumentsResult.a, argumentsTypes, typedArgumentsResult.b));
    }

    private Pair<List<TypedExpression>, List<Integer>> getTypedArguments(final NodeList<Expression> arguments, RHSPhase.Context arg) {
        final List<TypedExpression> typedArguments = new ArrayList<>();
        final List<Integer> emptyListArgumentIndexes = new ArrayList<>();
        int argumentIndex = 0;
        for (Expression child : arguments) {
            TypedExpression a = child.accept(this, arg);
            typedArguments.add(a);
            // [] is ambiguous in mvel - it can represent an empty list or an empty map.
            // It cannot be distinguished on a language level, so this is a workaround:
            // - When there [] written in a rule, mvel parser always parses it as an empty list.
            // - The only possible way with methods, when there is such parameter, is try to guess the correct parameter type when trying to read the method from a class.
            // - This finds all indexes of empty lists in the method parameters.
            // - Later when not possible to resolve the method with a list parameter, it will try to resolve a method with map parameter.
            // - This happens for all empty list parameters resolved by the parser, until a proper method is not found.
            if (child instanceof ListCreationLiteralExpression
                    && (((ListCreationLiteralExpression) a).getExpressions() == null
                        || ((ListCreationLiteralExpression) a).getExpressions().isEmpty())) {
                emptyListArgumentIndexes.add(argumentIndex);
            }
            argumentIndex++;
        }
        return new Pair<>(typedArguments, emptyListArgumentIndexes);
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
                                        Class<?>[] argumentsType,
                                        List<Integer> emptyListArgumentIndexes) {
        final Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodResult = resolveMethod(n, scope, argumentsType);
        final Optional<Type> methodReturnType = name.getType().or(() -> resolveMethodResult.a.map(Method::getReturnType));
        final List<Class<?>> actualArgumentType = optionalToStream(resolveMethodResult.a)
                .flatMap((Method m) -> Arrays.stream(m.getParameterTypes()))
                .collect(Collectors.toList());

        return new MethodCallExprT(n.getName().asString(), resolveMethodResult.b, arguments,
                                   actualArgumentType, methodReturnType);
    }

    private Pair<Optional<Method>, Optional<TypedExpression>> resolveMethod(final MethodCallExpr n,
            final Optional<TypedExpression> scope,
            final Class<?>[] argumentsType) {
        Optional<TypedExpression> finalScope = scope;
        Optional<Method> resolvedMethod;
        resolvedMethod = finalScope.flatMap(TypedExpression::getType)
                .<Class<?>>map(ClassUtils::classFromType)
                .map(scopeClazz -> MethodUtils.findMethod(scopeClazz, n.getNameAsString(), argumentsType));

        if (resolvedMethod.isEmpty()) {
            resolvedMethod = mvelCompilerContext.getRootPattern()
                    .map(scopeClazz -> MethodUtils.findMethod(scopeClazz, n.getNameAsString(), argumentsType));
            if (resolvedMethod.isPresent()) {
                finalScope = mvelCompilerContext.createRootTypePrefix();
            }
        }

        if (resolvedMethod.isEmpty()) {
            resolvedMethod = mvelCompilerContext.findStaticMethod(n.getNameAsString());
        }
        return new Pair<>(resolvedMethod, finalScope);
    }

    private Class<?>[] parametersType(List<TypedExpression> arguments) {
        return arguments.stream()
                .map(TypedExpression::getType)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ClassUtils::classFromType)
                .toArray(Class[]::new);
    }
}
