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
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.BetaIndex3;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.FilteringQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.NoneQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

/**
 * Represents the left hand side of a Drools rule, the result of which are three variables.
 * For more, see {@link UniLeftHandSide} and {@link BiLeftHandSide}.
 *
 * @param <A> generic type of the first resulting variable
 * @param <B> generic type of the second resulting variable
 * @param <C> generic type of the third resulting variable
 */
public final class TriLeftHandSide<A, B, C> extends AbstractLeftHandSide {

    private final PatternVariable<A, ?, ?> patternVariableA;
    private final PatternVariable<B, ?, ?> patternVariableB;
    private final PatternVariable<C, ?, ?> patternVariableC;

    protected TriLeftHandSide(PatternVariable<A, ?, ?> patternVariableA, PatternVariable<B, ?, ?> patternVariableB,
            PatternVariable<C, ?, ?> patternVariableC, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariableA = patternVariableA;
        this.patternVariableB = patternVariableB;
        this.patternVariableC = patternVariableC;
    }

    protected TriLeftHandSide(TriLeftHandSide<A, B, C> leftHandSide, PatternVariable<C, ?, ?> patternVariable) {
        super(leftHandSide.variableFactory);
        this.patternVariableA = leftHandSide.patternVariableA;
        this.patternVariableB = leftHandSide.patternVariableB;
        this.patternVariableC = patternVariable;
    }

    protected PatternVariable<A, ?, ?> getPatternVariableA() {
        return patternVariableA;
    }

    protected PatternVariable<B, ?, ?> getPatternVariableB() {
        return patternVariableB;
    }

    protected PatternVariable<C, ?, ?> getPatternVariableC() {
        return patternVariableC;
    }

    public TriLeftHandSide<A, B, C> andFilter(TriPredicate<A, B, C> predicate) {
        return new TriLeftHandSide<>(patternVariableA, patternVariableB, patternVariableC.filter(predicate,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable()), variableFactory);
    }

    private <D> TriLeftHandSide<A, B, C> applyJoiners(Class<D> otherFactType, AbstractQuadJoiner<A, B, C, D> joiner,
            QuadPredicate<A, B, C, D> predicate, boolean shouldExist) {
        Variable<D> toExist = (Variable<D>) variableFactory.createVariable(otherFactType, "toExist");
        PatternDSL.PatternDef<D> existencePattern = pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            TriFunction<A, B, C, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<D, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            Predicate4<D, A, B, C> joinPredicate =
                    (d, a, b, c) -> joinerType.matches(leftMapping.apply(a, b, c), rightMapping.apply(d));
            BetaIndex3<D, A, B, C, ?> index = betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex,
                    rightMapping::apply, leftMapping::apply, Object.class);
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                    patternVariableC.getPrimaryVariable(), joinPredicate, index);
        }
        return applyFilters(existencePattern, predicate, shouldExist);
    }

    private <D> TriLeftHandSide<A, B, C> applyFilters(PatternDSL.PatternDef<D> existencePattern,
            QuadPredicate<A, B, C, D> predicate, boolean shouldExist) {
        PatternDSL.PatternDef<D> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, patternVariableA.getPrimaryVariable(),
                        patternVariableB.getPrimaryVariable(), patternVariableC.getPrimaryVariable(),
                        (d, a, b, c) -> predicate.test(a, b, c, d));
        ViewItem<?> existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        return new TriLeftHandSide<>(this, patternVariableC.addDependentExpression(existenceExpression));
    }

    private <D> TriLeftHandSide<A, B, C> existsOrNot(Class<D> dClass, QuadJoiner<A, B, C, D>[] joiners,
            boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern
        AbstractQuadJoiner<A, B, C, D> finalJoiner = null;
        QuadPredicate<A, B, C, D> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractQuadJoiner<A, B, C, D> joiner = (AbstractQuadJoiner<A, B, C, D>) joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof NoneQuadJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneTriJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(joiner instanceof FilteringQuadJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? joiner : AbstractQuadJoiner.merge(finalJoiner, joiner);
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

    public <D> TriLeftHandSide<A, B, C> andExists(Class<D> dClass, QuadJoiner<A, B, C, D>[] joiners) {
        return existsOrNot(dClass, joiners, true);
    }

    public <D> TriLeftHandSide<A, B, C> andNotExists(Class<D> dClass, QuadJoiner<A, B, C, D>[] joiners) {
        return existsOrNot(dClass, joiners, false);
    }

    public <D> QuadLeftHandSide<A, B, C, D> andJoin(UniLeftHandSide<D> right, QuadJoiner<A, B, C, D> joiner) {
        AbstractQuadJoiner<A, B, C, D> castJoiner = (AbstractQuadJoiner<A, B, C, D>) joiner;
        JoinerType[] joinerTypes = castJoiner.getJoinerTypes();
        PatternVariable<D, ?, ?> newRight = right.getPatternVariableA();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            newRight = newRight.filterForJoin(patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                    patternVariableC.getPrimaryVariable(), castJoiner, joinerType, mappingIndex);
        }
        return new QuadLeftHandSide<>(patternVariableA, patternVariableB, patternVariableC, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(TriConstraintCollector<A, B, C, ?, NewA> collector) {
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collector, accumulateSource, accumulateOutput));
        return new UniLeftHandSide<>(
                new DirectPatternVariable<>(accumulateOutput, singletonList(outerAccumulatePattern)), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(TriConstraintCollector<A, B, C, ?, NewA> collectorA,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB) {
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        ViewItem<?> innerAccumulatePattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateSource, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutputB));
        return new BiLeftHandSide<>(new DetachedPatternVariable<>(accumulateOutputA),
                new DirectPatternVariable<>(accumulateOutputB, singletonList(outerAccumulatePattern)), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(
            TriConstraintCollector<A, B, C, ?, NewA> collectorA, TriConstraintCollector<A, B, C, ?, NewB> collectorB,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC) {
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        ViewItem<?> innerAccumulatePattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateSource, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC));
        return new TriLeftHandSide<>(new DetachedPatternVariable<>(accumulateOutputA),
                new DetachedPatternVariable<>(accumulateOutputB),
                new DirectPatternVariable<>(accumulateOutputC, singletonList(outerAccumulatePattern)), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            TriConstraintCollector<A, B, C, ?, NewA> collectorA, TriConstraintCollector<A, B, C, ?, NewB> collectorB,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC, TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("collectedD");
        ViewItem<?> innerAccumulatePattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateSource, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateSource, accumulateOutputD));
        return new QuadLeftHandSide<>(new DetachedPatternVariable<>(accumulateOutputA),
                new DetachedPatternVariable<>(accumulateOutputB), new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, singletonList(outerAccumulatePattern)), variableFactory);
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
    private <Out> AccumulateFunction createAccumulateFunction(TriConstraintCollector<A, B, C, ?, Out> collector,
            Variable<TriTuple<A, B, C>> in, Variable<Out> out) {
        return accFunction(() -> new DroolsTriAccumulateFunction<>(collector), in).as(out);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(TriFunction<A, B, C, NewA> keyMapping) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey,
                keyMapping::apply);
        return new UniLeftHandSide<>(new DirectPatternVariable<>(groupKey, singletonList(groupByPattern)),
                variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(TriFunction<A, B, C, NewA> keyMappingA,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutput));
        return new BiLeftHandSide<>(new DetachedPatternVariable<>(groupKey),
                new DirectPatternVariable<>(accumulateOutput, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(TriFunction<A, B, C, NewA> keyMappingA,
            TriConstraintCollector<A, B, C, ?, NewB> collectorB, TriConstraintCollector<A, B, C, ?, NewC> collectorC) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC));
        return new TriLeftHandSide<>(new DetachedPatternVariable<>(groupKey),
                new DetachedPatternVariable<>(accumulateOutputB),
                new DirectPatternVariable<>(accumulateOutputC, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            TriFunction<A, B, C, NewA> keyMappingA, TriConstraintCollector<A, B, C, ?, NewB> collectorB,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC, TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern =
                joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateSource, accumulateOutputD));
        return new QuadLeftHandSide<>(new DetachedPatternVariable<>(groupKey),
                new DetachedPatternVariable<>(accumulateOutputB), new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(TriFunction<A, B, C, NewA> keyMappingA,
            TriFunction<A, B, C, NewB> keyMappingB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, patternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> groupKeyPatternVar =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        PatternVariable<NewB, BiTuple<NewA, NewB>, ?> bPatternVar =
                new IndirectPatternVariable<>(groupKeyPatternVar, newB, tuple -> tuple.b);
        return new BiLeftHandSide<>(new DetachedPatternVariable<>(newA), bPatternVar, variableFactory);
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
    private <NewA, NewB> Function3<A, B, C, BiTuple<NewA, NewB>> createCompositeBiGroupKey(
            TriFunction<A, B, C, NewA> keyMappingA, TriFunction<A, B, C, NewB> keyMappingB) {
        return (a, b, c) -> new BiTuple<>(keyMappingA.apply(a, b, c), keyMappingB.apply(a, b, c));
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(TriFunction<A, B, C, NewA> keyMappingA,
            TriFunction<A, B, C, NewB> keyMappingB, TriConstraintCollector<A, B, C, ?, NewC> collectorC) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> directPatternVariable =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        List<ViewItem<?>> prerequisites = directPatternVariable.build();
        return new TriLeftHandSide<>(new DetachedPatternVariable<>(newA), new DetachedPatternVariable<>(newB),
                new DirectPatternVariable<>(accumulateOutput, prerequisites), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            TriFunction<A, B, C, NewA> keyMappingA, TriFunction<A, B, C, NewB> keyMappingB,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC, TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<C> inputC = patternVariableC.getPrimaryVariable();
        Variable<TriTuple<A, B, C>> accumulateSource =
                (Variable<TriTuple<A, B, C>>) variableFactory.createVariable(TriTuple.class, "source");
        PatternVariable<C, ?, ?> newPatternVariableC = patternVariableC.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                (c, a, b) -> new TriTuple<>(a, b, c));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB, newPatternVariableC);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, inputC, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateSource, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> directPatternVariable =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        List<ViewItem<?>> prerequisites = directPatternVariable.build();
        return new QuadLeftHandSide<>(new DetachedPatternVariable<>(newA), new DetachedPatternVariable<>(newB),
                new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, prerequisites), variableFactory);
    }

    public AbstractTriConstraintConsequence<A, B, C> andTerminate() {
        return new TriConstraintDefaultConsequence<>(this);
    }

    public AbstractTriConstraintConsequence<A, B, C> andTerminate(ToIntTriFunction<A, B, C> matchWeighter) {
        return new TriConstraintIntConsequence<>(this, matchWeighter);
    }

    public AbstractTriConstraintConsequence<A, B, C> andTerminate(ToLongTriFunction<A, B, C> matchWeighter) {
        return new TriConstraintLongConsequence<>(this, matchWeighter);
    }

    public AbstractTriConstraintConsequence<A, B, C> andTerminate(TriFunction<A, B, C, BigDecimal> matchWeighter) {
        return new TriConstraintBigDecimalConsequence<>(this, matchWeighter);
    }

    @Override
    public List<ViewItem<?>> get() {
        return Stream.of(patternVariableA, patternVariableB, patternVariableC)
                .flatMap(variable -> variable.build().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Variable[] getVariables() {
        return Stream.of(patternVariableA, patternVariableB, patternVariableC)
                .map(PatternVariable::getPrimaryVariable)
                .toArray(Variable[]::new);
    }
}
