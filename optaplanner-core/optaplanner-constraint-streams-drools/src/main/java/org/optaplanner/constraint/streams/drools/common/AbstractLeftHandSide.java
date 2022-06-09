package org.optaplanner.constraint.streams.drools.common;

import static org.drools.model.DSL.and;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.drools.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.JoinerType;

abstract class AbstractLeftHandSide {

    protected final DroolsVariableFactory variableFactory;

    protected AbstractLeftHandSide(DroolsVariableFactory variableFactory) {
        this.variableFactory = Objects.requireNonNull(variableFactory);
    }

    protected static Index.ConstraintType getConstraintType(JoinerType type) {
        switch (type) {
            case EQUAL:
                return Index.ConstraintType.EQUAL;
            case LESS_THAN:
                return Index.ConstraintType.LESS_THAN;
            case LESS_THAN_OR_EQUAL:
                return Index.ConstraintType.LESS_OR_EQUAL;
            case GREATER_THAN:
                return Index.ConstraintType.GREATER_THAN;
            case GREATER_THAN_OR_EQUAL:
                return Index.ConstraintType.GREATER_OR_EQUAL;
            default:
                throw new IllegalStateException("Unsupported joiner type (" + type + ").");
        }
    }

    protected static ViewItem<?> joinViewItemsWithLogicalAnd(PatternVariable<?, ?, ?>... patternVariables) {
        List<ViewItem<?>> viewItemList = mergeViewItems(patternVariables);
        int viewItemListSize = viewItemList.size();
        ViewItem<?> firstPattern = viewItemList.get(0);
        if (viewItemListSize == 1) {
            return firstPattern;
        }
        ViewItem<?>[] remainingPatternArray = viewItemList.subList(1, viewItemListSize)
                .toArray(new ViewItem[0]);
        return and(firstPattern, remainingPatternArray);
    }

    protected static List<ViewItem<?>> mergeViewItems(PatternVariable<?, ?, ?>... patternVariables) {
        List<ViewItem<?>> viewItemList = new ArrayList<>();
        for (PatternVariable<?, ?, ?> patternVariable : patternVariables) {
            viewItemList.addAll(patternVariable.build());
        }
        return viewItemList;
    }

    /**
     * Create an {@link IndirectPatternVariable} on {@link BiTuple} with pre-made bindings for its components variables.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link BiTuple#a}
     * @param boundVarB never null, {@link BiTuple#b}
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @return never null
     */
    protected static <A, B> IndirectPatternVariable<B, BiTuple<A, B>> decompose(Variable<BiTuple<A, B>> primaryVariable,
            ViewItem<?> prerequisitePattern, Variable<A> boundVarA, Variable<B> boundVarB) {
        Function<BiTuple<A, B>, B> bExtractor = tuple -> tuple.b;
        DirectPatternVariable<BiTuple<A, B>> tuplePatternVar =
                new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                        .bind(boundVarA, tuple -> tuple.a)
                        .bind(boundVarB, bExtractor);
        return new IndirectPatternVariable<>(tuplePatternVar, boundVarB, bExtractor);
    }

    /**
     * Create a {@link DirectPatternVariable} on {@link BiTuple} with pre-made bindings for its components variables
     * and one accumulate output variable.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link TriTuple#a}
     * @param boundVarB never null, {@link TriTuple#b}
     * @param accumulateOutput never null, output of the accumulate function
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @param <C> generic type of the accumulate output variable
     * @return never null
     */
    protected static <A, B, C> DirectPatternVariable<C> decomposeWithAccumulate(
            Variable<BiTuple<A, B>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> accumulateOutput) {
        DirectPatternVariable<BiTuple<A, B>> tuplePatternVar =
                new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                        .bind(boundVarA, tuple -> tuple.a)
                        .bind(boundVarB, tuple -> tuple.b);
        return new DirectPatternVariable<>(accumulateOutput, tuplePatternVar.build());
    }

    /**
     * Create an {@link IndirectPatternVariable} on {@link TriTuple} with pre-made bindings for its components variables.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link TriTuple#a}
     * @param boundVarB never null, {@link TriTuple#b}
     * @param boundVarC never null, {@link TriTuple#c}
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @param <C> generic type of the third bound variable
     * @return never null
     */
    protected static <A, B, C> IndirectPatternVariable<C, TriTuple<A, B, C>> decompose(
            Variable<TriTuple<A, B, C>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> boundVarC) {
        Function<TriTuple<A, B, C>, C> cExtractor = tuple -> tuple.c;
        DirectPatternVariable<TriTuple<A, B, C>> tuplePatternVar =
                new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                        .bind(boundVarA, tuple -> tuple.a)
                        .bind(boundVarB, tuple -> tuple.b)
                        .bind(boundVarC, cExtractor);
        return new IndirectPatternVariable<>(tuplePatternVar, boundVarC, cExtractor);
    }

    /**
     * Create an {@link IndirectPatternVariable} on {@link QuadTuple} with pre-made bindings for its components variables.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link QuadTuple#a}
     * @param boundVarB never null, {@link QuadTuple#b}
     * @param boundVarC never null, {@link QuadTuple#c}
     * @param boundVarD never null, {@link QuadTuple#d}
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @param <C> generic type of the third bound variable
     * @param <D> generic type of the fourth bound variable
     * @return never null
     */
    protected static <A, B, C, D> IndirectPatternVariable<D, QuadTuple<A, B, C, D>> decompose(
            Variable<QuadTuple<A, B, C, D>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> boundVarC, Variable<D> boundVarD) {
        Function<QuadTuple<A, B, C, D>, D> dExtractor = tuple -> tuple.d;
        DirectPatternVariable<QuadTuple<A, B, C, D>> tuplePatternVar =
                new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                        .bind(boundVarA, tuple -> tuple.a)
                        .bind(boundVarB, tuple -> tuple.b)
                        .bind(boundVarC, tuple -> tuple.c)
                        .bind(boundVarD, dExtractor);
        return new IndirectPatternVariable<>(tuplePatternVar, boundVarD, dExtractor);
    }

    /**
     * Create a {@link DirectPatternVariable} on {@link TriTuple} with pre-made bindings for its components variables
     * and one accumulate output variable.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link TriTuple#a}
     * @param boundVarB never null, {@link TriTuple#b}
     * @param boundVarC never null, {@link TriTuple#c}
     * @param accumulateOutput never null, output of the accumulate function
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @param <C> generic type of the third bound variable
     * @param <D> generic type of the accumulate output variable
     * @return never null
     */
    protected static <A, B, C, D> DirectPatternVariable<D> decomposeWithAccumulate(
            Variable<TriTuple<A, B, C>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> boundVarC, Variable<D> accumulateOutput) {
        DirectPatternVariable<TriTuple<A, B, C>> tuplePatternVar =
                new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                        .bind(boundVarA, tuple -> tuple.a)
                        .bind(boundVarB, tuple -> tuple.b)
                        .bind(boundVarC, tuple -> tuple.c);
        return new DirectPatternVariable<>(accumulateOutput, tuplePatternVar.build());
    }

    protected static ViewItem<?> buildAccumulate(ViewItem<?> innerAccumulatePattern,
            AccumulateFunction... accFunctions) {
        if (accFunctions.length == 0) {
            throw new IllegalStateException("Impossible state: no accumulate functions provided.");
        } else if (accFunctions.length == 1) {
            return DSL.accumulate(innerAccumulatePattern, accFunctions[0]);
        } else {
            return DSL.accumulate(innerAccumulatePattern, accFunctions[0],
                    Arrays.stream(accFunctions)
                            .skip(1)
                            .toArray(AccumulateFunction[]::new));
        }
    }

}
