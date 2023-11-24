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
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.MethodCallExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.DeclaredFunction;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassUtils;
import org.drools.util.MethodUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        final List<Integer> emptyCollectionArgumentIndexes = new ArrayList<>();
        int argumentIndex = 0;
        for (Expression child : arguments) {
            TypedExpression a = child.accept(this, arg);
            typedArguments.add(a);
            // [] is ambiguous in mvel - it can represent an empty list or an empty map.
            // It cannot be distinguished on a language level, so this is a workaround:
            // - When there [] written in a rule, mvel parser always parses it as an empty list.
            // - The only possible way with methods, when there is such parameter, is try to guess the correct parameter type when trying to read the method from a class.
            // - This finds all indexes of empty lists or empty maps in the method parameters.
            // - Later when not possible to resolve the method with a list or map parameter, it will try to resolve a method with the other collection parameter.
            // - This happens for all empty list and map parameters resolved by the parser, until a proper method is not found.
            if (child instanceof ListCreationLiteralExpression
                    && (((ListCreationLiteralExpression) child).getExpressions() == null
                        || ((ListCreationLiteralExpression) child).getExpressions().isEmpty())) {
                emptyCollectionArgumentIndexes.add(argumentIndex);
            } else if (child instanceof MapCreationLiteralExpression
                    && (((MapCreationLiteralExpression) child).getExpressions() == null
                    || ((MapCreationLiteralExpression) child).getExpressions().isEmpty())) {
                emptyCollectionArgumentIndexes.add(argumentIndex);
            }
            argumentIndex++;
        }
        return new Pair<>(typedArguments, emptyCollectionArgumentIndexes);
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
                                        List<Integer> emptyCollectionArgumentIndexes) {
        Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodResult = resolveMethod(n, scope, argumentsType);
        // This is a workaround for mvel empty list and map ambiguity, please see the description in getTypedArguments() method.
        if (resolveMethodResult.a.isEmpty() && !emptyCollectionArgumentIndexes.isEmpty()) {
            resolveMethodResult = resolveMethodWithEmptyCollectionArguments(n, resolveMethodResult.b, arguments, argumentsType, emptyCollectionArgumentIndexes);
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

    private Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodWithEmptyCollectionArguments(final MethodCallExpr n,
            final Optional<TypedExpression> scope,
            List<TypedExpression> arguments,
            final Class<?>[] argumentsType,
            List<Integer> emptyListArgumentIndexes) {
        // Rather work only with the argumentsType and when a method is resolved, flip the arguments list based on it.
        final List<Class<?>> coercedArgumentsTypesList = new ArrayList<>(Arrays.asList(argumentsType));
        // This needs to go through all possible combinations.
        final int indexesListSize = emptyListArgumentIndexes.size();
        for (int numberOfProcessedIndexes = 0; numberOfProcessedIndexes < indexesListSize; numberOfProcessedIndexes++) {
            for (int indexOfEmptyListIndex = numberOfProcessedIndexes; indexOfEmptyListIndex < indexesListSize; indexOfEmptyListIndex++) {
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
                Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodResult = resolveMethod(n, scope, coercedArgumentsTypesList.toArray(new Class[0]));
                if (resolveMethodResult.a.isPresent()) {
                    modifyArgumentsBasedOnCoercedCollectionArguments(arguments, argumentsType, coercedArgumentsTypesList);
                    return resolveMethodResult;
                }
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
            }
            switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(numberOfProcessedIndexes));
        }
        // No method found, return empty.
        return new Pair<>(Optional.empty(), scope);
    }

    private void switchCollectionClassInArgumentsByIndex(final List<Class<?>> argumentsTypesList, final int index) {
        if (argumentsTypesList.get(index).equals(List.class)) {
             argumentsTypesList.set(index, Map.class);
        } else {
            argumentsTypesList.set(index, List.class);
        }
    }

    private void modifyArgumentsBasedOnCoercedCollectionArguments(final List<TypedExpression> arguments, final Class<?>[] argumentsType, final List<Class<?>> coercedCollectionArguments) {
        int index = 0;
        for (Class<?> coercedClass : coercedCollectionArguments) {
            if (!coercedClass.equals(argumentsType[index])) {
                // Originally the resolved type was a List, so if it is different, it is a Map.
                argumentsType[index] = coercedClass;
                if (coercedClass.equals(List.class)) {
                    arguments.set(index, new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
                } else {
                    arguments.set(index, new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
                }
            }
            index++;
        }
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
