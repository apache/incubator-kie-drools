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

    private final PatternVariable<A> patternVariable;

    public UniLeftHandSide(Class<A> aClass, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariable = new PatternVariable<>((Variable<A>) variableFactory.createVariable(aClass, "var"));
    }

    protected UniLeftHandSide(UniLeftHandSide<A> leftHandSide, PatternVariable<A> patternVariable) {
        super(leftHandSide.variableFactory);
        this.patternVariable = patternVariable;
    }

    protected UniLeftHandSide(PatternVariable<A> patternVariable, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariable = patternVariable;
    }

    protected PatternVariable<A> getPatternVariableA() {
        return patternVariable;
    }

    public UniLeftHandSide<A> andFilter(Predicate<A> predicate) {
        return new UniLeftHandSide<>(this, patternVariable.filter(predicate));
    }

    private <B> UniLeftHandSide<A> applyJoiners(Class<B> bClass, AbstractBiJoiner<A, B> joiner,
            BiPredicate<A, B> predicate, boolean shouldExist) {
        Variable<B> toExist = (Variable<B>) variableFactory.createVariable(bClass, "toExist");
        PatternDSL.PatternDef<B> existencePattern = pattern(toExist);
        if (joiner == null) {
            return applyFilters(patternVariable, existencePattern, predicate, shouldExist);
        }
        PatternVariable<A> newPatternVariable = patternVariable;
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        // Rebuild the A pattern, binding variables for left parts of the joins.
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            Variable<Object> joinVar = variableFactory.createVariable("joinVar");
            Function<A, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            newPatternVariable = newPatternVariable.bind(joinVar, leftMapping);
            joinVars[mappingIndex] = joinVar;
        }
        // Create the B pattern, joining with the new A pattern using its freshly bound join variables.
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            JoinerType joinerType = joinerTypes[mappingIndex];
            Function<A, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<B, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            // Only extract B; A is coming from a pre-bound join var.
            Predicate2<B, A> joinPredicate = (b, a) -> joinerType.matches(a, rightMapping.apply(b));
            BetaIndex<B, A, ?> index = betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex,
                    rightMapping::apply, leftMapping::apply);
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    joinVars[mappingIndex], joinPredicate, index);
        }
        // And finally add the filter to the B pattern.
        return applyFilters(newPatternVariable, existencePattern, predicate, shouldExist);
    }

    private <B> UniLeftHandSide<A> applyFilters(PatternVariable<A> newPatternVariable,
            PatternDSL.PatternDef<B> existencePattern, BiPredicate<A, B> biPredicate, boolean shouldExist) {
        PatternDSL.PatternDef<B> possiblyFilteredExistencePattern = biPredicate == null ? existencePattern
                : existencePattern.expr("Filter using " + biPredicate, newPatternVariable.getPrimaryVariable(),
                        (b, a) -> biPredicate.test(a, b));
        ViewItem<?> existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        return new UniLeftHandSide<>(this, newPatternVariable.addDependentExpression(existenceExpression));
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
        // Rebuild the A pattern, binding variables for left parts of the joins.
        PatternVariable<A> newLeft = patternVariable;
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            Variable<Object> joinVar = variableFactory.createVariable("joinVar" + mappingIndex);
            newLeft = newLeft.bind(joinVar, castJoiner.getLeftMapping(mappingIndex));
            joinVars[mappingIndex] = joinVar;
        }
        PatternVariable<B> newRight = right.patternVariable;
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            JoinerType joinerType = joinerTypes[mappingIndex];
            newRight = newRight.filterOnJoinVar(joinVars[mappingIndex], castJoiner, joinerType, mappingIndex);
        }
        return new BiLeftHandSide<>(newLeft, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(Function<A, NewA> keyMapping) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMapping::apply);
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        return new UniLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)), variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(UniConstraintCollector<A, ?, NewA> collector) {
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> outerAccumulatePattern = accumulate(innerAccumulatePattern,
                createAccumulateFunction(collector, accumulateOutput));
        return new UniLeftHandSide<>(new PatternVariable<>(accumulateOutput, singletonList(outerAccumulatePattern)),
                variableFactory);
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
        return accFunction(() -> new DroolsUniAccumulateFunction<>(collector), patternVariable.getPrimaryVariable())
                .as(out);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<BiTuple<NewA, NewB>> groupKey =
                (Variable<BiTuple<NewA, NewB>>) variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        return new BiLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        ViewItem<?> groupByPattern = groupBy(innerGroupByPattern, input, groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey);
        Variable<NewB> newB = variableFactory.createVariable("newB", accumulateOutput);
        return new BiLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), variableFactory);
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
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        Variable<NewC> newC = variableFactory.createVariable("newC", accumulateOutput);
        return new TriLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), new PatternVariable<>(newC), variableFactory);
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
        Variable<NewA> newA = variableFactory.createVariable("newA", groupKey, k -> k.a);
        Variable<NewB> newB = variableFactory.createVariable("newB", groupKey, k -> k.b);
        Variable<NewC> newC = variableFactory.createVariable("newC", accumulateOutputC);
        Variable<NewD> newD = variableFactory.createVariable("newD", accumulateOutputD);
        return new QuadLeftHandSide<>(new PatternVariable<>(newA, singletonList(groupByPattern)),
                new PatternVariable<>(newB), new PatternVariable<>(newC), new PatternVariable<>(newD), variableFactory);
    }

    public AbstractUniConstraintConsequence<A> andTerminate() {
        return new UniConstraintDefaultConsequence(this);
    }

    public AbstractUniConstraintConsequence<A> andTerminate(ToIntFunction<A> matchWeighter) {
        return new UniConstraintIntConsequence<>(this, matchWeighter);
    }

    public AbstractUniConstraintConsequence<A> andTerminate(ToLongFunction<A> matchWeighter) {
        return new UniConstraintLongConsequence<>(this, matchWeighter);
    }

    public AbstractUniConstraintConsequence<A> andTerminate(Function<A, BigDecimal> matchWeighter) {
        return new UniConstraintBigDecimalConsequence<>(this, matchWeighter);
    }

    @Override
    public List<ViewItem<?>> get() {
        return patternVariable.build();
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { patternVariable.getPrimaryVariable() };
    }

}
