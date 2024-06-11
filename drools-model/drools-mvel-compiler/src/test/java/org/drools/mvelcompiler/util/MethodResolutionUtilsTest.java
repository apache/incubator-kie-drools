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

package org.drools.mvelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.Pair;
import org.assertj.core.api.Assertions;
import org.drools.Person;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.ObjectCreationExpressionT;
import org.drools.mvelcompiler.ast.StringLiteralExpressionT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodResolutionUtilsTest {

    @Test
    public void coerceCorrectConstructorArgumentsTypeIsNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.coerceCorrectConstructorArguments(
                                null,
                                null,
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void coerceCorrectConstructorArgumentsArgumentsAreNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.coerceCorrectConstructorArguments(
                                Person.class,
                                null,
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void coerceCorrectConstructorArgumentsEmptyCollectionIndexesAreNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.coerceCorrectConstructorArguments(
                                Person.class,
                                Collections.emptyList(),
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void coerceCorrectConstructorArgumentsEmptyCollectionIndexesBiggerThanArguments() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.coerceCorrectConstructorArguments(
                                Person.class,
                                Collections.emptyList(),
                                List.of(1, 2, 4)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void coerceCorrectConstructorArgumentsNoCollectionArguments() {
        final List<TypedExpression> arguments = List.of(new IntegerLiteralExpressionT(new IntegerLiteralExpr("12")));
        final List<TypedExpression> coercedArguments = MethodResolutionUtils.coerceCorrectConstructorArguments(
                Person.class, arguments, Collections.emptyList());
        Assertions.assertThat(coercedArguments).containsExactlyElementsOf(arguments);
    }

    @Test
    public void coerceCorrectConstructorArgumentsIsNotCollectionAtIndex() {
        final List<TypedExpression> arguments = List.of(new IntegerLiteralExpressionT(new IntegerLiteralExpr("12")));
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.coerceCorrectConstructorArguments(
                                Person.class,
                                arguments,
                                List.of(0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void coerceCorrectConstructorArgumentsList() {
        final List<TypedExpression> arguments = List.of(new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final List<TypedExpression> coercedArguments = MethodResolutionUtils.coerceCorrectConstructorArguments(
                Person.class, arguments, List.of(0));
        Assertions.assertThat(coercedArguments).containsExactlyElementsOf(arguments);
    }

    @Test
    public void coerceCorrectConstructorArgumentsCoerceMap() {
        final List<TypedExpression> arguments =
                List.of(
                        new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())),
                        new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final List<Class<?>> expectedArgumentClasses = List.of(ListExprT.class, MapExprT.class);
        final List<TypedExpression> coercedArguments = MethodResolutionUtils.coerceCorrectConstructorArguments(
                Person.class, arguments, List.of(1));
        Assertions.assertThat(getTypedExpressionsClasses(coercedArguments))
                .containsExactlyElementsOf(expectedArgumentClasses);
    }

    @Test
    public void coerceCorrectConstructorArgumentsCoerceList() {
        final List<TypedExpression> arguments =
                List.of(
                        new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())),
                        new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
        final List<Class<?>> expectedArgumentClasses = List.of(ListExprT.class, MapExprT.class);
        final List<TypedExpression> coercedArguments = MethodResolutionUtils.coerceCorrectConstructorArguments(
                Person.class, arguments, List.of(0));
        Assertions.assertThat(getTypedExpressionsClasses(coercedArguments))
                .containsExactlyElementsOf(expectedArgumentClasses);
    }

    @Test
    public void coerceCorrectConstructorArgumentsCoerceListAndMap() {
        final List<TypedExpression> arguments =
                List.of(
                        new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())),
                        new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final List<Class<?>> expectedArgumentClasses = List.of(ListExprT.class, MapExprT.class);
        final List<TypedExpression> coercedArguments = MethodResolutionUtils.coerceCorrectConstructorArguments(
                Person.class, arguments, List.of(0, 1));
        Assertions.assertThat(getTypedExpressionsClasses(coercedArguments))
                .containsExactlyElementsOf(expectedArgumentClasses);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsMethodExpressionIsNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                null,
                                null,
                                Optional.empty(),
                                null,
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsMvelCompilerContextIsNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                new MethodCallExpr(),
                                null,
                                Optional.empty(),
                                null,
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsArgumentsAreNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                new MethodCallExpr(),
                                new MvelCompilerContext(null),
                                Optional.empty(),
                                null,
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsEmptyCollectionIndexesAreNull() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                new MethodCallExpr(),
                                new MvelCompilerContext(null),
                                Optional.empty(),
                                Collections.emptyList(),
                                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsEmptyCollectionIndexesBiggerThanArguments() {
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                new MethodCallExpr(),
                                new MvelCompilerContext(null),
                                Optional.empty(),
                                Collections.emptyList(),
                                List.of(1, 2, 4)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsNoCollectionArguments() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setIntegerBoxed", new IntegerLiteralExpr("12"));
        final List<TypedExpression> arguments = List.of(new IntegerLiteralExpressionT(new IntegerLiteralExpr("12")));
        final List<TypedExpression> expectedArguments = new ArrayList<>(arguments);
        final TypedExpression scope = new ObjectCreationExpressionT(arguments, Person.class);
        final Pair<Optional<Method>, Optional<TypedExpression>> resolvedMethodResult =
                MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                        methodExpression,
                        new MvelCompilerContext(null),
                        Optional.of(scope),
                        arguments,
                        Collections.emptyList());
        Assertions.assertThat(resolvedMethodResult.a).isPresent();
        Assertions.assertThat(arguments).containsExactlyElementsOf(expectedArguments);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsIsNotCollectionAtIndex() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setIntegerBoxed", new IntegerLiteralExpr("12"));
        final List<TypedExpression> arguments = List.of(new StringLiteralExpressionT(new StringLiteralExpr("12")));
        final TypedExpression scope = new ObjectCreationExpressionT(arguments, Person.class);
        Assertions.assertThatThrownBy(
                        () -> MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                                methodExpression,
                                new MvelCompilerContext(null),
                                Optional.of(scope),
                                arguments,
                                List.of(0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void resolveMethodWithEmptyCollectionArguments() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setAddresses", new ListCreationLiteralExpression(null, NodeList.nodeList()));
        final List<TypedExpression> arguments = List.of(new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final TypedExpression scope = new ObjectCreationExpressionT(arguments, Person.class);
        final Pair<Optional<Method>, Optional<TypedExpression>> resolvedMethodResult =
                MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                        methodExpression,
                        new MvelCompilerContext(null),
                        Optional.of(scope),
                        arguments,
                        List.of(0));
        Assertions.assertThat(resolvedMethodResult.a).isPresent();
        Assertions.assertThat(getTypedExpressionsClasses(arguments))
                .containsExactlyElementsOf(List.of(ListExprT.class));
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsCoerceMap() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setItems", new MapCreationLiteralExpression(null, NodeList.nodeList()));
        final List<TypedExpression> arguments = new ArrayList<>();
        arguments.add(new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final TypedExpression scope = new ObjectCreationExpressionT(Collections.emptyList(), Person.class);
        final Pair<Optional<Method>, Optional<TypedExpression>> resolvedMethodResult =
                MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                        methodExpression,
                        new MvelCompilerContext(null),
                        Optional.of(scope),
                        arguments,
                        List.of(0));
        Assertions.assertThat(resolvedMethodResult.a).isPresent();
        Assertions.assertThat(getTypedExpressionsClasses(arguments))
                .containsExactlyElementsOf(List.of(MapExprT.class));
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsCoerceList() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setAddresses", new MapCreationLiteralExpression(null, NodeList.nodeList()));
        final List<TypedExpression> arguments = new ArrayList<>();
        arguments.add(new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
        final TypedExpression scope = new ObjectCreationExpressionT(Collections.emptyList(), Person.class);
        final Pair<Optional<Method>, Optional<TypedExpression>> resolvedMethodResult =
                MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                        methodExpression,
                        new MvelCompilerContext(null),
                        Optional.of(scope),
                        arguments,
                        List.of(0));
        Assertions.assertThat(resolvedMethodResult.a).isPresent();
        Assertions.assertThat(getTypedExpressionsClasses(arguments))
                .containsExactlyElementsOf(List.of(ListExprT.class));
    }

    @Test
    public void resolveMethodWithEmptyCollectionArgumentsCoerceListAndMap() {
        final MethodCallExpr methodExpression = new MethodCallExpr("setAddressesAndItems", new MapCreationLiteralExpression(null, NodeList.nodeList()));
        final List<TypedExpression> arguments = new ArrayList<>();
        arguments.add(new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
        arguments.add(new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        final TypedExpression scope = new ObjectCreationExpressionT(Collections.emptyList(), Person.class);
        final Pair<Optional<Method>, Optional<TypedExpression>> resolvedMethodResult =
                MethodResolutionUtils.resolveMethodWithEmptyCollectionArguments(
                        methodExpression,
                        new MvelCompilerContext(null),
                        Optional.of(scope),
                        arguments,
                        List.of(0, 1));
        Assertions.assertThat(resolvedMethodResult.a).isPresent();
        Assertions.assertThat(getTypedExpressionsClasses(arguments))
                .containsExactlyElementsOf(List.of(ListExprT.class, MapExprT.class));
    }

    private List<Class<?>> getTypedExpressionsClasses(List<TypedExpression> typedExpressions) {
        return typedExpressions.stream().map(TypedExpression::getClass).collect(Collectors.toList());
    }
}