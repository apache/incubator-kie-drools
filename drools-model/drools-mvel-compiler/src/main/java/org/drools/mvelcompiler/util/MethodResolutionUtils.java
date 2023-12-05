package org.drools.mvelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.utils.Pair;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.ListExprT;
import org.drools.mvelcompiler.ast.MapExprT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassUtils;
import org.drools.util.MethodUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MethodResolutionUtils {

    private MethodResolutionUtils() {
        // It is forbidden to create instances of util classes.
    }

    public static List<TypedExpression> coerceCorrectConstructorArguments(
            final Class<?> type,
            List<TypedExpression> arguments,
            List<Integer> emptyListArgumentIndexes) {
        // Rather work only with the argumentsType and when a method is resolved, flip the arguments list based on it.
        final List<TypedExpression> coercedArgumentsTypesList = new ArrayList<>(arguments);
        // This needs to go through all possible combinations.
        final int indexesListSize = emptyListArgumentIndexes.size();
        for (int numberOfProcessedIndexes = 0; numberOfProcessedIndexes < indexesListSize; numberOfProcessedIndexes++) {
            for (int indexOfEmptyListIndex = numberOfProcessedIndexes; indexOfEmptyListIndex < indexesListSize; indexOfEmptyListIndex++) {
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
                Constructor<?> constructor;
                try {
                    constructor = type.getConstructor(parametersType(coercedArgumentsTypesList));
                } catch (NoSuchMethodException ex) {
                    constructor = null;
                }
                if (constructor != null) {
                    return coercedArgumentsTypesList;
                }
                switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(indexOfEmptyListIndex));
            }
            switchCollectionClassInArgumentsByIndex(coercedArgumentsTypesList, emptyListArgumentIndexes.get(numberOfProcessedIndexes));
        }
        // No constructor found, return the original arguments.
        return arguments;
    }

    public static Pair<Optional<Method>, Optional<TypedExpression>> resolveMethodWithEmptyCollectionArguments(
            final MethodCallExpr methodExpression,
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
                        MethodResolutionUtils.resolveMethod(methodExpression, mvelCompilerContext, scope, coercedArgumentsTypesList);
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

    public static Pair<List<TypedExpression>, List<Integer>> getTypedArgumentsWithEmptyCollectionArgumentDetection(
            final NodeList<Expression> arguments,
            final DrlGenericVisitor<TypedExpression, VisitorContext> drlGenericVisitor,
            final VisitorContext arg) {
        final List<TypedExpression> typedArguments = new ArrayList<>();
        final List<Integer> emptyCollectionArgumentIndexes = new ArrayList<>();
        int argumentIndex = 0;
        for (Expression child : arguments) {
            TypedExpression a = child.accept(drlGenericVisitor, arg);
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
