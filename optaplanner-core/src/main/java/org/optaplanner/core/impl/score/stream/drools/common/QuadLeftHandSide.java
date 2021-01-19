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

import static java.util.Collections.singletonList;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.groupBy;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.pattern;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function4;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.penta.AbstractPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.FilteringPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.NonePentaJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

/**
 * Represents the left hand side of a Drools rule, the result of which are four variables.
 * For more, see {@link UniLeftHandSide} and {@link BiLeftHandSide}.
 *
 * @param <A> generic type of the first resulting variable
 * @param <B> generic type of the second resulting variable
 * @param <C> generic type of the third resulting variable
 * @param <D> generic type of the fourth resulting variable
 */
public final class QuadLeftHandSide<A, B, C, D> extends AbstractLeftHandSide {

    private final PatternVariable<A> patternVariableA;
    private final PatternVariable<B> patternVariableB;
    private final PatternVariable<C> patternVariableC;
    private final PatternVariable<D> patternVariableD;

    protected QuadLeftHandSide(PatternVariable<A> patternVariableA, PatternVariable<B> patternVariableB,
            PatternVariable<C> patternVariableC, PatternVariable<D> patternVariableD, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariableA = patternVariableA;
        this.patternVariableB = patternVariableB;
        this.patternVariableC = patternVariableC;
        this.patternVariableD = patternVariableD;
    }

    public QuadLeftHandSide<A, B, C, D> andFilter(QuadPredicate<A, B, C, D> predicate) {
        return new QuadLeftHandSide<>(patternVariableA, patternVariableB, patternVariableC,
                patternVariableD.filter(predicate, patternVariableA.getPrimaryVariable(),
                        patternVariableB.getPrimaryVariable(), patternVariableC.getPrimaryVariable()),
                variableFactory);
    }

    private <E> QuadLeftHandSide<A, B, C, D> applyJoiners(Class<E> otherFactType, PentaJoiner<A, B, C, D, E> joiner,
            PentaPredicate<A, B, C, D, E> predicate, boolean shouldExist) {
        if (joiner == null) {
            return applyFilters(otherFactType, predicate, shouldExist);
        }
        // There is no epsilon index in Drools, therefore we replace joining with a filter.
        AbstractPentaJoiner<A, B, C, D, E> castJoiner = (AbstractPentaJoiner<A, B, C, D, E>) joiner;
        PentaPredicate<A, B, C, D, E> joinFilter = castJoiner::matches;
        PentaPredicate<A, B, C, D, E> result = predicate == null ? joinFilter : joinFilter.and(predicate);
        // And finally we add the filter to the E pattern.
        return applyFilters(otherFactType, result, shouldExist);
    }

    private <E> QuadLeftHandSide<A, B, C, D> applyFilters(Class<E> otherFactType, PentaPredicate<A, B, C, D, E> predicate,
            boolean shouldExist) {
        Variable<E> toExist = (Variable<E>) variableFactory.createVariable(otherFactType, "biToExist");
        PatternDSL.PatternDef<E> existencePattern = pattern(toExist);
        PatternDSL.PatternDef<E> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, patternVariableA.getPrimaryVariable(),
                        patternVariableB.getPrimaryVariable(), patternVariableC.getPrimaryVariable(),
                        patternVariableD.getPrimaryVariable(), (e, a, b, c, d) -> predicate.test(a, b, c, d, e));
        ViewItem<?> existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        return new QuadLeftHandSide<>(patternVariableA, patternVariableB, patternVariableC,
                patternVariableD.addDependentExpression(existenceExpression), variableFactory);
    }

    private <E> QuadLeftHandSide<A, B, C, D> existsOrNot(Class<E> dClass, PentaJoiner<A, B, C, D, E>[] joiners,
            boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern
        AbstractPentaJoiner<A, B, C, D, E> finalJoiner = null;
        PentaPredicate<A, B, C, D, E> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractPentaJoiner<A, B, C, D, E> joiner = (AbstractPentaJoiner<A, B, C, D, E>) joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof NonePentaJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneTriJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(joiner instanceof FilteringPentaJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? joiner : AbstractPentaJoiner.merge(finalJoiner, joiner);
                }
            } else {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? joiner.getFilter() : finalFilter.and(joiner.getFilter());
            }
        }
        return applyJoiners(dClass, finalJoiner, finalFilter, shouldExist);
    }

    public <E> QuadLeftHandSide<A, B, C, D> andExists(Class<E> dClass, PentaJoiner<A, B, C, D, E>[] joiners) {
        return existsOrNot(dClass, joiners, true);
    }

    public <E> QuadLeftHandSide<A, B, C, D> andNotExists(Class<E> dClass, PentaJoiner<A, B, C, D, E>[] joiners) {
        return existsOrNot(dClass, joiners, false);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(QuadFunction<A, B, C, D, NewA> keyMapping) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<D> inputD = patternVariableD.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, patternVariableD);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, inputD, groupKey,
                keyMapping::apply);
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        return new UniLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(QuadConstraintCollector<A, B, C, D, ?, NewA> collector) {
        Variable<QuadTuple<A, B, C, D>> accumulateSource =
                (Variable<QuadTuple<A, B, C, D>>) variableFactory.createVariable(QuadTuple.class, "source");
        PatternVariable<D> newPatternVariableD = patternVariableD.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                patternVariableC.getPrimaryVariable(), (d, a, b, c) -> new QuadTuple<>(a, b, c, d));
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, newPatternVariableD);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collector, accumulateSource, accumulateOutput));
        return new UniLeftHandSide<>(new PatternVariable<>(accumulateOutput, singletonList(outerAccumulatePattern)),
                variableFactory);
    }

    /**
     * Creates a Drools accumulate function based on a given collector. The accumulate function will take one
     * {@link Variable} as input and return its result into another {@link Variable}.
     *
     * @param collector collector to use in the accumulate function
     * @param in variable to use as accumulate input
     * @param out variable in which to store accumulate result
     * @param <Out> type of the accumulate result
     * @return Drools accumulate function
     */
    private <Out> AccumulateFunction createAccumulateFunction(QuadConstraintCollector<A, B, C, D, ?, Out> collector,
            Variable<QuadTuple<A, B, C, D>> in, Variable<Out> out) {
        return accFunction(() -> new DroolsQuadAccumulateFunction<>(collector), in).as(out);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(QuadFunction<A, B, C, D, NewA> keyMappingA,
            QuadFunction<A, B, C, D, NewB> keyMappingB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<D> inputD = patternVariableD.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, patternVariableD);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, inputD, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        return new BiLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), variableFactory);
    }

    /**
     * Takes group key mappings and merges them in such a way that the result is a single composite key.
     * This is necessary because Drools groupBy can only take a single key - therefore multiple variables need to be
     * converted into a singular composite variable.
     *
     * @param keyMappingA mapping for the first variable
     * @param keyMappingB mapping for the second variable
     * @param <NewA> generic type of the first variable
     * @param <NewB> generic type of the second variable
     * @return never null, Drools function to convert the keys to a singular composite key
     */
    private <NewA, NewB> Function4<A, B, C, D, BiTuple<NewA, NewB>> createCompositeBiGroupKey(
            QuadFunction<A, B, C, D, NewA> keyMappingA, QuadFunction<A, B, C, D, NewB> keyMappingB) {
        return (a, b, c, d) -> new BiTuple<>(keyMappingA.apply(a, b, c, d), keyMappingB.apply(a, b, c, d));
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(QuadFunction<A, B, C, D, NewA> keyMappingA,
            QuadConstraintCollector<A, B, C, D, ?, NewB> collectorB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<D> inputD = patternVariableD.getPrimaryVariable();
        Variable<QuadTuple<A, B, C, D>> accumulateSource =
                (Variable<QuadTuple<A, B, C, D>>) variableFactory.createVariable(QuadTuple.class, "source");
        PatternVariable<D> newPatternVariableD = patternVariableD.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                patternVariableC.getPrimaryVariable(), (d, a, b, c) -> new QuadTuple<>(a, b, c, d));
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, newPatternVariableD);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, inputD, groupKey,
                keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        Variable<NewB> newB = variableFactory.createVariable("newB", accumulateOutput);
        return new BiLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(QuadFunction<A, B, C, D, NewA> keyMappingA,
            QuadFunction<A, B, C, D, NewB> keyMappingB, QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<D> inputD = patternVariableD.getPrimaryVariable();
        Variable<QuadTuple<A, B, C, D>> accumulateSource =
                (Variable<QuadTuple<A, B, C, D>>) variableFactory.createVariable(QuadTuple.class, "source");
        PatternVariable<D> newPatternVariableD = patternVariableD.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                patternVariableC.getPrimaryVariable(), (d, a, b, c) -> new QuadTuple<>(a, b, c, d));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, newPatternVariableD);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, inputD, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        Variable<NewC> newC = variableFactory.createVariable("newC", accumulateOutput);
        return new TriLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), new PatternVariable<>(newC), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            QuadFunction<A, B, C, D, NewA> keyMappingA, QuadFunction<A, B, C, D, NewB> keyMappingB,
            QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<D> inputD = patternVariableD.getPrimaryVariable();
        Variable<QuadTuple<A, B, C, D>> accumulateSource =
                (Variable<QuadTuple<A, B, C, D>>) variableFactory.createVariable(QuadTuple.class, "source");
        PatternVariable<D> newPatternVariableD = patternVariableD.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                patternVariableC.getPrimaryVariable(), (d, a, b, c) -> new QuadTuple<>(a, b, c, d));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC, newPatternVariableD);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, inputD, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateSource, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        Variable<NewC> newC = variableFactory.createVariable("newC", accumulateOutputC);
        Variable<NewD> newD = variableFactory.createVariable("newD", accumulateOutputD);
        return new QuadLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), new PatternVariable<>(newC), new PatternVariable<>(newD), variableFactory);
    }

    public AbstractQuadConstraintConsequence<A, B, C, D> andTerminate() {
        return new QuadConstraintDefaultConsequence<>(this);
    }

    public AbstractQuadConstraintConsequence<A, B, C, D> andTerminate(ToIntQuadFunction<A, B, C, D> matchWeighter) {
        return new QuadConstraintIntConsequence<>(this, matchWeighter);
    }

    public AbstractQuadConstraintConsequence<A, B, C, D> andTerminate(ToLongQuadFunction<A, B, C, D> matchWeighter) {
        return new QuadConstraintLongConsequence<>(this, matchWeighter);
    }

    public AbstractQuadConstraintConsequence<A, B, C, D> andTerminate(QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        return new QuadConstraintBigDecimalConsequence<>(this, matchWeighter);
    }

    @Override
    public List<ViewItem<?>> get() {
        return Stream.of(patternVariableA, patternVariableB, patternVariableC, patternVariableD)
                .flatMap(variable -> variable.build().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Variable[] getVariables() {
        return Stream.of(patternVariableA, patternVariableB, patternVariableC, patternVariableD)
                .map(PatternVariable::getPrimaryVariable)
                .toArray(Variable[]::new);
    }
}
