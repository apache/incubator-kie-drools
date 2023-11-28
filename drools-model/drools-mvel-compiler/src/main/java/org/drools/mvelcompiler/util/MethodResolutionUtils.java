package org.drools.mvelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.Pair;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassUtils;
import org.drools.util.MethodUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MethodResolutionUtils {

    private MethodResolutionUtils() {
        // It is forbidden to create instances of util classes.
    }

    public static Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodWithEmptyCollectionArguments(
            final Expression methodOrConstructorExpression,
            final MvelCompilerContext mvelCompilerContext,
            final Optional<TypedExpression> scope,
            List<TypedExpression> arguments,
            List<Integer> emptyListArgumentIndexes) {
        // Rather work only with the argumentsType and when a method is resolved, flip the arguments list based on it.
        final List<TypedExpression> coercedArgumentsTypesList = new ArrayList<>(arguments);
        // This needs to go through all possible combinations.
        final int indexesListSize = emptyListArgumentIndexes.size();
        for (int numberOfProcessedIndexes = 0; numberOfProcessedIndexes < indexesListSize; numberOfProcessedIndexes++) {
            for (int indexOfEmptyListIndex = numberOfProcessedIndexes; indexOfEmptyListIndex < indexesListSize; indexOfEmptyListIndex++) {
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
                Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodResult =
                        MethodResolutionUtils.resolveMethod((MethodCallExpr) methodOrConstructorExpression, mvelCompilerContext, scope, coercedArgumentsTypesList);
                if (resolveMethodResult.a.isPresent()) {
                    modifyArgumentsBasedOnCoercedCollectionArguments(arguments, coercedArgumentsTypesList);
                    return resolveMethodResult;
                }
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
            }
            switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(numberOfProcessedIndexes));
        }
        // No method found, return empty.
        return new Pair<>(Optional.empty(), scope);
    }

    public static Pair<Optional<Method>, Optional<TypedExpression>> resolveMethod(
            final MethodCallExpr n,
            final MvelCompilerContext mvelCompilerContext,
            final Optional<TypedExpression> scope,
            final List<TypedExpression> argumentsTypes) {
        final Class<?>[] argumentsTypesClasses = parametersType(argumentsTypes);
        Optional<TypedExpression> finalScope = scope;
        Optional<Method> resolvedMethod;
        resolvedMethod = finalScope.flatMap(TypedExpression::getType)
                .<Class<?>>map(ClassUtils::classFromType)
                .map(scopeClazz -> MethodUtils.findMethod(scopeClazz, n.getNameAsString(), argumentsTypesClasses));

        if (resolvedMethod.isEmpty()) {
            resolvedMethod = mvelCompilerContext.getRootPattern()
                    .map(scopeClazz -> MethodUtils.findMethod(scopeClazz, n.getNameAsString(), argumentsTypesClasses));
            if (resolvedMethod.isPresent()) {
                finalScope = mvelCompilerContext.createRootTypePrefix();
            }
        }

        if (resolvedMethod.isEmpty()) {
            resolvedMethod = mvelCompilerContext.findStaticMethod(n.getNameAsString());
        }
        return new Pair<>(resolvedMethod, finalScope);
    }

    private static Class<?>[] parametersType(List<TypedExpression> arguments) {
        return arguments.stream()
                .map(TypedExpression::getType)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ClassUtils::classFromType)
                .toArray(Class[]::new);
    }

    private static void switchCollectionClassInArgumentsByIndex(final List<TypedExpression> argumentsTypesList, final int index) {
        if (argumentsTypesList.get(index).getClass().equals(ListExprT.class)) {
            argumentsTypesList.set(index, new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
        } else {
            argumentsTypesList.set(index, new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
        }
    }

    private static void modifyArgumentsBasedOnCoercedCollectionArguments(final List<TypedExpression> arguments, final List<TypedExpression> coercedCollectionArguments) {
        int index = 0;
        for (TypedExpression coercedArgument : coercedCollectionArguments) {
            if (!coercedArgument.getClass().equals(arguments.get(index).getClass())) {
                // Originally the resolved type was a List, so if it is different, it is a Map.
                if (coercedArgument.getClass().equals(MapExprT.class)) {
                    arguments.set(index, new MapExprT(new MapCreationLiteralExpression(null, NodeList.nodeList())));
                } else {
                    arguments.set(index, new ListExprT(new ListCreationLiteralExpression(null, NodeList.nodeList())));
                }
            }
            index++;
        }
    }
}
