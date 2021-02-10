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
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.BetaIndex2;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.FilteringTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

/**
 * Represents the left hand side of a Drools rule, the result of which are two variables.
 * The simplest variant of such rule, with no filters or groupBys applied, would look like this in equivalent DRL:
 *
 * <pre>
 * {@code
 *  rule "Simplest bivariate rule"
 *  when
 *      $a: Something()
 *      $b: SomethingElse()
 *  then
 *      // Do something with the $a and $b variables.
 *  end
 * }
 * </pre>
 *
 * Usually though, there would be a joiner between the two, limiting the cartesian product:
 *
 * <pre>
 * {@code
 *  rule "Bivariate join rule"
 *  when
 *      $a: Something($leftJoin: someValue)
 *      $b: SomethingElse(someOtherValue == $leftJoin)
 *  then
 *      // Do something with the $a and $b variables.
 *  end
 * }
 * </pre>
 *
 * For more, see {@link UniLeftHandSide}.
 *
 * @param <A> generic type of the first resulting variable
 * @param <B> generic type of the second resulting variable
 */
public final class BiLeftHandSide<A, B> extends AbstractLeftHandSide {

    private final PatternVariable<A> patternVariableA;
    private final PatternVariable<B> patternVariableB;

    protected BiLeftHandSide(PatternVariable<A> left, PatternVariable<B> right, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariableA = left;
        this.patternVariableB = right;
    }

    protected BiLeftHandSide(BiLeftHandSide<A, B> leftHandSide, PatternVariable<B> patternVariable) {
        super(leftHandSide.variableFactory);
        this.patternVariableA = leftHandSide.patternVariableA;
        this.patternVariableB = patternVariable;
    }

    protected BiLeftHandSide(BiLeftHandSide<A, B> leftHandSide, PatternVariable<A> left, PatternVariable<B> right) {
        super(leftHandSide.variableFactory);
        this.patternVariableA = left;
        this.patternVariableB = right;
    }

    protected PatternVariable<A> getPatternVariableA() {
        return patternVariableA;
    }

    protected PatternVariable<B> getPatternVariableB() {
        return patternVariableB;
    }

    public BiLeftHandSide<A, B> andFilter(BiPredicate<A, B> predicate) {
        return new BiLeftHandSide<>(this, patternVariableA,
                patternVariableB.filter(predicate, patternVariableA.getPrimaryVariable()));
    }

    private <C> BiLeftHandSide<A, B> applyJoiners(Class<C> otherFactType, AbstractTriJoiner<A, B, C> joiner,
            TriPredicate<A, B, C> predicate, boolean shouldExist) {
        Variable<C> toExist = (Variable<C>) variableFactory.createVariable(otherFactType, "toExist");
        PatternDSL.PatternDef<C> existencePattern = pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            BiFunction<A, B, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<C, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            Predicate3<C, A, B> joinPredicate =
                    (c, a, b) -> joinerType.matches(leftMapping.apply(a, b), rightMapping.apply(c));
            BetaIndex2<C, A, B, ?> index = betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex,
                    rightMapping::apply, leftMapping::apply, Object.class);
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(), joinPredicate, index);
        }
        return applyFilters(existencePattern, predicate, shouldExist);
    }

    private <C> BiLeftHandSide<A, B> applyFilters(PatternDSL.PatternDef<C> existencePattern, TriPredicate<A, B, C> predicate,
            boolean shouldExist) {
        PatternDSL.PatternDef<C> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, patternVariableA.getPrimaryVariable(),
                        patternVariableB.getPrimaryVariable(), (c, a, b) -> predicate.test(a, b, c));
        ViewItem<?> existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        return new BiLeftHandSide<>(this, patternVariableB.addDependentExpression(existenceExpression));
    }

    private <C> BiLeftHandSide<A, B> existsOrNot(Class<C> cClass, TriJoiner<A, B, C>[] joiners, boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern
        AbstractTriJoiner<A, B, C> finalJoiner = null;
        TriPredicate<A, B, C> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractTriJoiner<A, B, C> joiner = (AbstractTriJoiner<A, B, C>) joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof NoneTriJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneTriJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(joiner instanceof FilteringTriJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? joiner : AbstractTriJoiner.merge(finalJoiner, joiner);
                }
            } else {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? joiner.getFilter() : finalFilter.and(joiner.getFilter());
            }
        }
        return applyJoiners(cClass, finalJoiner, finalFilter, shouldExist);
    }

    public <C> BiLeftHandSide<A, B> andExists(Class<C> cClass, TriJoiner<A, B, C>[] joiners) {
        return existsOrNot(cClass, joiners, true);
    }

    public <C> BiLeftHandSide<A, B> andNotExists(Class<C> cClass, TriJoiner<A, B, C>[] joiners) {
        return existsOrNot(cClass, joiners, false);
    }

    public <C> TriLeftHandSide<A, B, C> andJoin(UniLeftHandSide<C> right, TriJoiner<A, B, C> joiner) {
        AbstractTriJoiner<A, B, C> castJoiner = (AbstractTriJoiner<A, B, C>) joiner;
        JoinerType[] joinerTypes = castJoiner.getJoinerTypes();
        PatternVariable<C> newRight = right.getPatternVariableA();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            newRight = newRight.filterForJoin(patternVariableA.getPrimaryVariable(),
                    patternVariableB.getPrimaryVariable(), castJoiner, joinerType, mappingIndex);
        }
        return new TriLeftHandSide<>(patternVariableA, patternVariableB, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(BiFunction<A, B, NewA> keyMapping) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, groupKey,
                keyMapping::apply);
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        return new UniLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(BiConstraintCollector<A, B, ?, NewA> collector) {
        Variable<BiTuple<A, B>> accumulateSource =
                (Variable<BiTuple<A, B>>) variableFactory.createVariable(BiTuple.class, "source");
        PatternVariable<B> newPatternVariableB = patternVariableB.bind(accumulateSource,
                patternVariableA.getPrimaryVariable(), (b, a) -> new BiTuple<>(a, b));
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariableA, newPatternVariableB);
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
    private <Out> AccumulateFunction createAccumulateFunction(BiConstraintCollector<A, B, ?, Out> collector,
            Variable<BiTuple<A, B>> in, Variable<Out> out) {
        return accFunction(() -> new DroolsBiAccumulateFunction<>(collector), in).as(out);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, groupKey,
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
    private <NewA, NewB> Function2<A, B, BiTuple<NewA, NewB>> createCompositeBiGroupKey(
            BiFunction<A, B, NewA> keyMappingA, BiFunction<A, B, NewB> keyMappingB) {
        return (a, b) -> new BiTuple<>(keyMappingA.apply(a, b), keyMappingB.apply(a, b));
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiConstraintCollector<A, B, ?, NewB> collectorB) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<BiTuple<A, B>> accumulateSource =
                (Variable<BiTuple<A, B>>) variableFactory.createVariable(BiTuple.class, "source");
        PatternVariable<B> newPatternVariableB = patternVariableB.bind(accumulateSource, inputA,
                (b, a) -> new BiTuple<>(a, b));
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, newPatternVariableB);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, groupKey,
                keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateSource, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        Variable<NewB> newB = variableFactory.createVariable("newB", accumulateOutput);
        return new BiLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB, BiConstraintCollector<A, B, ?, NewC> collectorC) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<BiTuple<A, B>> accumulateSource =
                (Variable<BiTuple<A, B>>) variableFactory.createVariable(BiTuple.class, "source");
        PatternVariable<B> newPatternVariableB = patternVariableB.bind(accumulateSource, inputA,
                (b, a) -> new BiTuple<>(a, b));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, newPatternVariableB);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateSource, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        Variable<NewC> newC = variableFactory.createVariable("newC", accumulateOutput);
        return new TriLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), new PatternVariable<>(newC), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        Variable<BiTuple<A, B>> accumulateSource =
                (Variable<BiTuple<A, B>>) variableFactory.createVariable(BiTuple.class, "source");
        PatternVariable<B> newPatternVariableB = patternVariableB.bind(accumulateSource, inputA,
                (b, a) -> new BiTuple<>(a, b));
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, newPatternVariableB);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, inputA, inputB, groupKey,
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

    public AbstractBiConstraintConsequence<A, B> andTerminate() {
        return new BiConstraintDefaultConsequence<>(this);
    }

    public AbstractBiConstraintConsequence<A, B> andTerminate(ToIntBiFunction<A, B> matchWeighter) {
        return new BiConstraintIntConsequence<>(this, matchWeighter);
    }

    public AbstractBiConstraintConsequence<A, B> andTerminate(ToLongBiFunction<A, B> matchWeighter) {
        return new BiConstraintLongConsequence<>(this, matchWeighter);
    }

    public AbstractBiConstraintConsequence<A, B> andTerminate(BiFunction<A, B, BigDecimal> matchWeighter) {
        return new BiConstraintBigDecimalConsequence<>(this, matchWeighter);
    }

    @Override
    public List<ViewItem<?>> get() {
        return Stream.of(patternVariableA, patternVariableB)
                .flatMap(variable -> variable.build().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Variable[] getVariables() {
        return Stream.of(patternVariableA, patternVariableB)
                .map(PatternVariable::getPrimaryVariable)
                .toArray(Variable[]::new);
    }
}
