/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common;

import static org.drools.model.DSL.and;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsVariableFactory;

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
     * Create a {@link DirectPatternVariable} on {@link BiTuple} with pre-made bindings for its components variables.
     *
     * @param primaryVariable never null
     * @param prerequisitePattern never null, pattern required to construct the variable
     * @param boundVarA never null, {@link BiTuple#a}
     * @param boundVarB never null, {@link BiTuple#b}
     * @param <A> generic type of the first bound variable
     * @param <B> generic type of the second bound variable
     * @return never null
     */
    protected static <A, B> DirectPatternVariable<BiTuple<A, B>> decompose(Variable<BiTuple<A, B>> primaryVariable,
            ViewItem<?> prerequisitePattern, Variable<A> boundVarA, Variable<B> boundVarB) {
        return new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                .bind(boundVarA, tuple -> tuple.a)
                .bind(boundVarB, tuple -> tuple.b);
    }

    /**
     * Create a {@link DirectPatternVariable} on {@link TriTuple} with pre-made bindings for its components variables.
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
    protected static <A, B, C> DirectPatternVariable<TriTuple<A, B, C>> decompose(
            Variable<TriTuple<A, B, C>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> boundVarC) {
        return new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                .bind(boundVarA, tuple -> tuple.a)
                .bind(boundVarB, tuple -> tuple.b)
                .bind(boundVarC, tuple -> tuple.c);
    }

    /**
     * Create a {@link DirectPatternVariable} on {@link QuadTuple} with pre-made bindings for its components variables.
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
    protected static <A, B, C, D> DirectPatternVariable<QuadTuple<A, B, C, D>> decompose(
            Variable<QuadTuple<A, B, C, D>> primaryVariable, ViewItem<?> prerequisitePattern, Variable<A> boundVarA,
            Variable<B> boundVarB, Variable<C> boundVarC, Variable<D> boundVarD) {
        return new DirectPatternVariable<>(primaryVariable, prerequisitePattern)
                .bind(boundVarA, tuple -> tuple.a)
                .bind(boundVarB, tuple -> tuple.b)
                .bind(boundVarC, tuple -> tuple.c)
                .bind(boundVarD, tuple -> tuple.d);
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
