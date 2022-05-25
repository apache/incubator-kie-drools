/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools.common;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.JoinerType;

public interface PatternVariable<A, PatternVar_, Child_ extends PatternVariable<A, PatternVar_, Child_>> {

    Variable<A> getPrimaryVariable();

    List<ViewItem<?>> getPrerequisiteExpressions();

    List<ViewItem<?>> getDependentExpressions();

    Child_ filter(Predicate<A> predicate);

    <LeftJoinVar_> Child_ filter(BiPredicate<LeftJoinVar_, A> predicate, Variable<LeftJoinVar_> leftJoinVariable);

    <LeftJoinVarA_, LeftJoinVarB_> Child_ filter(TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB);

    <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC);

    <LeftJoinVar_> PatternVariable<A, PatternVar_, Child_> filterForJoin(Variable<LeftJoinVar_> leftJoinVar,
            DefaultBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType, int mappingIndex);

    <LeftJoinVarA_, LeftJoinVarB_> PatternVariable<A, PatternVar_, Child_> filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA,
            Variable<LeftJoinVarB_> leftJoinVarB, DefaultTriJoiner<LeftJoinVarA_, LeftJoinVarB_, A> joiner,
            JoinerType joinerType, int mappingIndex);

    <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> PatternVariable<A, PatternVar_, Child_> filterForJoin(
            Variable<LeftJoinVarA_> leftJoinVarA, Variable<LeftJoinVarB_> leftJoinVarB, Variable<LeftJoinVarC_> leftJoinVarC,
            DefaultQuadJoiner<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> joiner, JoinerType joinerType,
            int mappingIndex);

    /**
     * Bind a new variable.
     * This call is safe for use outside accumulate() and groupBy(),
     * unlike {@link #bind(Variable, Variable, BiFunction)} etc.
     *
     * @param boundVariable the new variable
     * @param bindingFunction the function to apply to create the bound variable
     * @param <BoundVar_> generic type of the bound variable
     * @return never null
     */
    <BoundVar_> Child_ bind(Variable<BoundVar_> boundVariable, Function<A, BoundVar_> bindingFunction);

    /**
     * This must only be used in patterns that will eventually end up in accumulate() or groupBy().
     * Drools does not support binding 2+ variables outside of accumulate() or groupBy().
     * Disobeying will result in Drools {@link NullPointerException} at runtime.
     *
     * @param boundVariable the new variable
     * @param leftJoinVariable the variable to use when creating the bound variable
     * @param bindingFunction the function to apply to create the bound variable
     * @param <BoundVar_> generic type of the bound variable
     * @param <LeftJoinVar_> generic type of the join variable
     * @return never null
     */
    <BoundVar_, LeftJoinVar_> Child_ bind(Variable<BoundVar_> boundVariable, Variable<LeftJoinVar_> leftJoinVariable,
            BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction);

    /**
     * As defined by {@link #bind(Variable, Variable, BiFunction)}.
     *
     * @param boundVariable the new variable
     * @param leftJoinVariableA the first variable to use when creating the bound variable
     * @param leftJoinVariableB the second variable to use when creating the bound variable
     * @param bindingFunction the function to apply to create the bound variable
     * @param <BoundVar_> generic type of the bound variable
     * @param <LeftJoinVarA_> generic type of the first join variable
     * @param <LeftJoinVarB_> generic type of the second join variable
     * @return never null
     */
    <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction);

    /**
     * As defined by {@link #bind(Variable, Variable, BiFunction)}.
     *
     * @param boundVariable the new variable
     * @param leftJoinVariableA the first variable to use when creating the bound variable
     * @param leftJoinVariableB the second variable to use when creating the bound variable
     * @param leftJoinVariableC the third variable to use when creating the bound variable
     * @param bindingFunction the function to apply to create the bound variable
     * @param <BoundVar_> generic type of the bound variable
     * @param <LeftJoinVarA_> generic type of the first join variable
     * @param <LeftJoinVarB_> generic type of the second join variable
     * @param <LeftJoinVarC_> generic type of the third join variable
     * @return never null
     */
    <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> Child_ bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVarA_> leftJoinVariableA, Variable<LeftJoinVarB_> leftJoinVariableB,
            Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction);

    Child_ addDependentExpression(ViewItem<?> expression);

    List<ViewItem<?>> build();
}
