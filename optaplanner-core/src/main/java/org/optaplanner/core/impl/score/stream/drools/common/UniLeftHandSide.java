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
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.BetaIndex;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.FilteringBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.NoneBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsVariableFactory;

/**
 * Represents the left-hand side of a Drools rule, the result of which is a single variable.
 * The simplest variant of such rule, with no filters or groupBys applied, would look like this in equivalent DRL:
 *
 * <pre>
 * {@code
 *  rule "Simplest univariate rule"
 *  when
 *      $a: Something()
 *  then
 *      // Do something with the $a variable.
 *  end
 * }
 * </pre>
 *
 * Left-hand side is that part of the rule between the "when" and "then" keywords.
 * The part between the "then" and "end" keywords is called the consequence of the rule, and this class does not represent it.
 * It can be created by calling {@link #andTerminate()}.
 *
 * There are also more complex variants of rules that still result in just one variable:
 *
 * <pre>
 * {@code
 *  rule "Complex univariate rule"
 *  when
 *      $accumulateResult: Collection() from accumulate(
 *          ...
 *      )
 *      $a: Object() from $accumulateResult
 *      exists Something()
 *  then
 *      // Do something with the $a variable.
 *  end
 * }
 * </pre>
 *
 * To create the simplest possible variant, call {@link #UniLeftHandSide(Class, DroolsVariableFactory)}.
 * Further specializations can be introduced by calling builder methods such as {@link #andFilter(Predicate)}.
 *
 * These builder methods will always return a new instance of {@link AbstractLeftHandSide}, as these are immutable.
 * Some builder methods, such as {@link #andJoin(UniLeftHandSide, BiJoiner)}, will return an instance of
 * {@link BiLeftHandSide} ({@link TriLeftHandSide}, ...), as that particular operation will increase the cardinality
 * of the parent constraint stream.
 *
 * @param <A> generic type of the resulting variable
 */
public final class UniLeftHandSide<A> extends AbstractLeftHandSide {

    private final PatternVariable<A, ?, ?> patternVariable;
    private final UniRuleContext<A> ruleContext;

    public UniLeftHandSide(Class<A> aClass, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariable = new DirectPatternVariable<>((Variable<A>) variableFactory.createVariable(aClass, "var"));
        // This LHS will use the same variable access both for direct consequence and for subsequent operations.
        this.ruleContext = buildDefaultRuleContext();
    }

    protected UniLeftHandSide(UniLeftHandSide<A> leftHandSide, PatternVariable<A, ?, ?> patternVariable) {
        super(leftHandSide.variableFactory);
        this.patternVariable = patternVariable;
        // This LHS will use the same variable access both for direct consequence and for subsequent operations.
        this.ruleContext = buildDefaultRuleContext();
    }

    protected UniLeftHandSide(UniRuleContext<A> ruleContext, PatternVariable<A, ?, ?> patternVariable,
            DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariable = patternVariable;
        // This LHS allows for quicker access to variables, if consequence directly follows.
        this.ruleContext = Objects.requireNonNull(ruleContext);
    }

    private UniRuleContext<A> buildDefaultRuleContext() {
        return new UniRuleContext<>(patternVariable.getPrimaryVariable(),
                patternVariable.build().toArray(new ViewItem<?>[0]));
    }

    protected PatternVariable<A, ?, ?> getPatternVariableA() {
        return patternVariable;
    }

    public UniLeftHandSide<A> andFilter(Predicate<A> predicate) {
        return new UniLeftHandSide<>(this, patternVariable.filter(predicate));
    }

    private <B> UniLeftHandSide<A> applyJoiners(Class<B> otherFactType, AbstractBiJoiner<A, B> joiner,
            BiPredicate<A, B> predicate, boolean shouldExist) {
        Variable<B> toExist = (Variable<B>) variableFactory.createVariable(otherFactType, "toExist");
        PatternDSL.PatternDef<B> existencePattern = pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            Function<A, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<B, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            Predicate2<B, A> joinPredicate = (b, a) -> joinerType.matches(leftMapping.apply(a), rightMapping.apply(b));
            BetaIndex<B, A, ?> index = betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex,
                    rightMapping::apply, leftMapping::apply);
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    patternVariable.getPrimaryVariable(), joinPredicate, index);
        }
        return applyFilters(existencePattern, predicate, shouldExist);
    }

    private <B> UniLeftHandSide<A> applyFilters(PatternDSL.PatternDef<B> existencePattern, BiPredicate<A, B> predicate,
            boolean shouldExist) {
        PatternDSL.PatternDef<B> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, patternVariable.getPrimaryVariable(),
                        (b, a) -> predicate.test(a, b));
        ViewItem<?> existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        return new UniLeftHandSide<>(this, patternVariable.addDependentExpression(existenceExpression));
    }

    private <B> UniLeftHandSide<A> existsOrNot(Class<B> bClass, BiJoiner<A, B>[] joiners, boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern.
        AbstractBiJoiner<A, B> finalJoiner = null;
        BiPredicate<A, B> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractBiJoiner<A, B> biJoiner = (AbstractBiJoiner<A, B>) joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (biJoiner instanceof NoneBiJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneBiJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(biJoiner instanceof FilteringBiJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + biJoiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? biJoiner : AbstractBiJoiner.merge(finalJoiner, biJoiner);
                }
            } else {
                if (!hasAFilter) { // From now on, only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? biJoiner.getFilter() : finalFilter.and(biJoiner.getFilter());
            }
        }
        return applyJoiners(bClass, finalJoiner, finalFilter, shouldExist);
    }

    public <B> UniLeftHandSide<A> andExists(Class<B> bClass, BiJoiner<A, B>[] joiners) {
        return existsOrNot(bClass, joiners, true);
    }

    public <B> UniLeftHandSide<A> andNotExists(Class<B> bClass, BiJoiner<A, B>[] joiners) {
        return existsOrNot(bClass, joiners, false);
    }

    public <B> BiLeftHandSide<A, B> andJoin(UniLeftHandSide<B> right, BiJoiner<A, B> joiner) {
        AbstractBiJoiner<A, B> castJoiner = (AbstractBiJoiner<A, B>) joiner;
        JoinerType[] joinerTypes = castJoiner.getJoinerTypes();
        PatternVariable<B, ?, ?> newRight = right.patternVariable;
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            newRight = newRight.filterForJoin(patternVariable.getPrimaryVariable(), castJoiner, joinerType, mappingIndex);
        }
        return new BiLeftHandSide<>(patternVariable, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(UniConstraintCollector<A, ?, NewA> collector) {
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collector, accumulateOutput));
        UniRuleContext<NewA> simpleRuleContext = new UniRuleContext<>(accumulateOutput, outerAccumulatePattern);
        return new UniLeftHandSide<>(simpleRuleContext,
                new DirectPatternVariable<>(accumulateOutput, singletonList(outerAccumulatePattern)), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(UniConstraintCollector<A, ?, NewA> collectorA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB));
        BiRuleContext<NewA, NewB> simpleRuleContext = new BiRuleContext<>(accumulateOutputA, accumulateOutputB,
                outerAccumulatePattern);
        return new BiLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(accumulateOutputA),
                new DirectPatternVariable<>(accumulateOutputB, singletonList(outerAccumulatePattern)),
                variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(
            UniConstraintCollector<A, ?, NewA> collectorA, UniConstraintCollector<A, ?, NewB> collectorB,
            UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        TriRuleContext<NewA, NewB, NewC> simpleRuleContext = new TriRuleContext<>(accumulateOutputA, accumulateOutputB,
                accumulateOutputC, outerAccumulatePattern);
        return new TriLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(accumulateOutputA),
                new DetachedPatternVariable<>(accumulateOutputB),
                new DirectPatternVariable<>(accumulateOutputC, singletonList(outerAccumulatePattern)),
                variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            UniConstraintCollector<A, ?, NewA> collectorA, UniConstraintCollector<A, ?, NewB> collectorB,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("collectedD");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        QuadRuleContext<NewA, NewB, NewC, NewD> simpleRuleContext = new QuadRuleContext<>(accumulateOutputA,
                accumulateOutputB, accumulateOutputC, accumulateOutputD, outerAccumulatePattern);
        return new QuadLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(accumulateOutputA),
                new DetachedPatternVariable<>(accumulateOutputB), new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, singletonList(outerAccumulatePattern)), variableFactory);
    }

    /**
     * Creates a Drools accumulate function based on a given collector. The accumulate function will take
     * {@link PatternVariable}'s primary variable as input and return its result into another {@link Variable}.
     *
     * @param collector collector to use in the accumulate function
     * @param out variable in which to store accumulate result
     * @param <Out> type of the accumulate result
     * @return Drools accumulate function
     */
    private <Out> AccumulateFunction createAccumulateFunction(UniConstraintCollector<A, ?, Out> collector,
            Variable<Out> out) {
        return accFunction(() -> new UniAccumulateFunction<>(collector), patternVariable.getPrimaryVariable())
                .as(out);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(Function<A, NewA> keyMapping) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMapping::apply);
        UniRuleContext<NewA> simpleRuleContext = new UniRuleContext<>(groupKey, groupByPattern);
        return new UniLeftHandSide<>(simpleRuleContext,
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutput));
        BiRuleContext<NewA, NewB> simpleRuleContext = new BiRuleContext<>(groupKey, accumulateOutput, groupByPattern);
        return new BiLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(groupKey),
                new DirectPatternVariable<>(accumulateOutput, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        TriRuleContext<NewA, NewB, NewC> simpleRuleContext = new TriRuleContext<>(groupKey, accumulateOutputB,
                accumulateOutputC, groupByPattern);
        return new TriLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(groupKey),
                new DetachedPatternVariable<>(accumulateOutputB),
                new DirectPatternVariable<>(accumulateOutputC, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        QuadRuleContext<NewA, NewB, NewC, NewD> simpleRuleContext = new QuadRuleContext<>(groupKey, accumulateOutputB,
                accumulateOutputC, accumulateOutputD, groupByPattern);
        return new QuadLeftHandSide<>(simpleRuleContext, new DetachedPatternVariable<>(groupKey),
                new DetachedPatternVariable<>(accumulateOutputB), new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> groupKeyPatternVar =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        PatternVariable<NewB, BiTuple<NewA, NewB>, ?> bPatternVar =
                new IndirectPatternVariable<>(groupKeyPatternVar, newB, tuple -> tuple.b);
        // No simple context; due to the need to decompose the group key, the pattern variables are required.
        return new BiLeftHandSide<>(new DetachedPatternVariable<>(newA), bPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)),
                createAccumulateFunction(collectorC, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> directPatternVariable =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        List<ViewItem<?>> prerequisites = directPatternVariable.build();
        // No simple context; due to the need to decompose the group key, the pattern variables are required.
        return new TriLeftHandSide<>(new DetachedPatternVariable<>(newA), new DetachedPatternVariable<>(newB),
                new DirectPatternVariable<>(accumulateOutput, prerequisites), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<BiTuple<NewA, NewB>> directPatternVariable =
                new DirectPatternVariable<>(groupKey, singletonList(groupByPattern))
                        .bind(newA, tuple -> tuple.a)
                        .bind(newB, tuple -> tuple.b);
        List<ViewItem<?>> prerequisites = directPatternVariable.build();
        // No simple context; due to the need to decompose the group key, the pattern variables are required.
        return new QuadLeftHandSide<>(new DetachedPatternVariable<>(newA), new DetachedPatternVariable<>(newB),
                new DetachedPatternVariable<>(accumulateOutputC),
                new DirectPatternVariable<>(accumulateOutputD, prerequisites),
                variableFactory);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate() {
        return ruleContext.newRuleBuilder();
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(ToIntFunction<A> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(ToLongFunction<A> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(Function<A, BigDecimal> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

}
