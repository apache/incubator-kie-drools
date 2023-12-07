package org.drools.mvelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import org.assertj.core.api.Assertions;
import org.drools.Person;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvelcompiler.ast.IntegerLiteralExpressionT;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
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
    public void resolveMethodWithEmptyCollectionArguments() {
        // TODO
    }

    @Test
    public void resolveMethod() {
        // TODO
    }

    @Test
    public void getTypedArgumentsWithEmptyCollectionArgumentDetection() {
        // TODO
    }

    private List<Class<?>> getTypedExpressionsClasses(List<TypedExpression> typedExpressions) {
        return typedExpressions.stream().map(TypedExpression::getClass).collect(Collectors.toList());
    }
}