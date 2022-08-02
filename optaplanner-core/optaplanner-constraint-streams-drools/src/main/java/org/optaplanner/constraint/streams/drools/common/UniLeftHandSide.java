package org.optaplanner.constraint.streams.drools.common;

import static java.util.Collections.singletonList;
import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.BetaIndex;
import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.bi.FilteringBiJoiner;
import org.optaplanner.constraint.streams.drools.DroolsVariableFactory;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.JoinerType;

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
        this(new DirectPatternVariable<>(variableFactory.createVariable(aClass, "var")), variableFactory);
    }

    UniLeftHandSide(Variable<A> variable, List<ViewItem<?>> viewItems, DroolsVariableFactory variableFactory) {
        this(new DirectPatternVariable<>(variable, viewItems), variableFactory);
    }

    UniLeftHandSide(PatternVariable<A, ?, ?> patternVariable, DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariable = Objects.requireNonNull(patternVariable);
        this.ruleContext = buildRuleContext();
    }

    private UniRuleContext<A> buildRuleContext() {
        return new UniRuleContext<>(patternVariable.getPrimaryVariable(),
                patternVariable.build().toArray(new ViewItem<?>[0]));
    }

    public PatternVariable<A, ?, ?> getPatternVariableA() {
        return patternVariable;
    }

    public UniLeftHandSide<A> andFilter(Predicate<A> predicate) {
        return new UniLeftHandSide<>(patternVariable.filter(predicate), variableFactory);
    }

    private <B> UniLeftHandSide<A> applyJoiners(Class<B> otherFactType, Predicate<B> nullityFilter,
            DefaultBiJoiner<A, B> joiner, BiPredicate<A, B> predicate, boolean shouldExist) {
        Variable<B> toExist = variableFactory.createVariable(otherFactType, "toExist");
        PatternDSL.PatternDef<B> existencePattern = pattern(toExist);
        if (nullityFilter != null) {
            existencePattern = existencePattern.expr("Exclude nulls using " + nullityFilter,
                    nullityFilter::test);
        }
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        int joinerCount = joiner.getJoinerCount();
        for (int mappingIndex = 0; mappingIndex < joinerCount; mappingIndex++) {
            JoinerType joinerType = joiner.getJoinerType(mappingIndex);
            Function<A, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<B, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            Predicate2<B, A> joinPredicate = (b, a) -> joinerType.matches(leftMapping.apply(a), rightMapping.apply(b));
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    patternVariable.getPrimaryVariable(), joinPredicate, createBetaIndex(joiner, mappingIndex));
        }
        return applyFilters(existencePattern, predicate, shouldExist);
    }

    private <B> BetaIndex<B, A, ?> createBetaIndex(DefaultBiJoiner<A, B> joiner, int mappingIndex) {
        JoinerType joinerType = joiner.getJoinerType(mappingIndex);
        Function<A, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<B, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        if (joinerType == JoinerType.EQUAL) {
            return betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex, rightMapping::apply,
                    leftMapping::apply, Object.class);
        } else { // Drools beta index on LT/LTE/GT/GTE requires Comparable.
            return betaIndexedBy(Comparable.class, getConstraintType(joinerType), mappingIndex,
                    b -> (Comparable) rightMapping.apply(b), leftMapping::apply, Comparable.class);
        }
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
        return new UniLeftHandSide<>(patternVariable.addDependentExpression(existenceExpression), variableFactory);
    }

    private <B> UniLeftHandSide<A> existsOrNot(Class<B> bClass, BiJoiner<A, B>[] joiners, Predicate<B> nullityFilter,
            boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern.
        DefaultBiJoiner<A, B> finalJoiner = null;
        BiPredicate<A, B> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            BiJoiner<A, B> joiner = joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof FilteringBiJoiner) {
                if (!hasAFilter) { // From now on, only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                FilteringBiJoiner<A, B> castJoiner = (FilteringBiJoiner<A, B>) joiner;
                finalFilter = finalFilter == null ? castJoiner.getFilter() : finalFilter.and(castJoiner.getFilter());
            } else {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    DefaultBiJoiner<A, B> castJoiner = (DefaultBiJoiner<A, B>) joiner;
                    finalJoiner = finalJoiner == null ? castJoiner : finalJoiner.and(castJoiner);
                }
            }
        }
        return applyJoiners(bClass, nullityFilter, finalJoiner, finalFilter, shouldExist);
    }

    public <B> UniLeftHandSide<A> andExists(Class<B> bClass, BiJoiner<A, B>[] joiners, Predicate<B> nullityFilter) {
        return existsOrNot(bClass, joiners, nullityFilter, true);
    }

    public <B> UniLeftHandSide<A> andNotExists(Class<B> bClass, BiJoiner<A, B>[] joiners, Predicate<B> nullityFilter) {
        return existsOrNot(bClass, joiners, nullityFilter, false);
    }

    public <B> BiLeftHandSide<A, B> andJoin(UniLeftHandSide<B> right, BiJoiner<A, B> joiner) {
        DefaultBiJoiner<A, B> castJoiner = (DefaultBiJoiner<A, B>) joiner;
        int joinerCount = castJoiner.getJoinerCount();
        PatternVariable<B, ?, ?> newRight = right.patternVariable;
        for (int mappingIndex = 0; mappingIndex < joinerCount; mappingIndex++) {
            JoinerType joinerType = castJoiner.getJoinerType(mappingIndex);
            newRight = newRight.filterForJoin(patternVariable.getPrimaryVariable(), castJoiner, joinerType, mappingIndex);
        }
        return new BiLeftHandSide<>(patternVariable, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(UniConstraintCollector<A, ?, NewA> collector) {
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> outerAccumulatePattern = buildAccumulate(createAccumulateFunction(collector, accumulateOutput));
        return new UniLeftHandSide<>(accumulateOutput, singletonList(outerAccumulatePattern), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(UniConstraintCollector<A, ?, NewA> collectorA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        ViewItem<?> outerAccumulatePattern = buildAccumulate(createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB));
        return new BiLeftHandSide<>(accumulateOutputA,
                new DirectPatternVariable<>(accumulateOutputB, outerAccumulatePattern), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(
            UniConstraintCollector<A, ?, NewA> collectorA, UniConstraintCollector<A, ?, NewB> collectorB,
            UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        ViewItem<?> outerAccumulatePattern = buildAccumulate(createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        return new TriLeftHandSide<>(accumulateOutputA, accumulateOutputB,
                new DirectPatternVariable<>(accumulateOutputC, outerAccumulatePattern), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            UniConstraintCollector<A, ?, NewA> collectorA, UniConstraintCollector<A, ?, NewB> collectorB,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("collectedD");
        ViewItem<?> outerAccumulatePattern = buildAccumulate(createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        return new QuadLeftHandSide<>(accumulateOutputA, accumulateOutputB, accumulateOutputC,
                new DirectPatternVariable<>(accumulateOutputD, outerAccumulatePattern), variableFactory);
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
        Variable<A> variable = patternVariable.getPrimaryVariable();
        return new AccumulateFunction(null, () -> new UniAccumulator<>(variable, collector))
                .with(variable)
                .as(out);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(Function<A, NewA> keyMapping) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMapping::apply);
        return new UniLeftHandSide<>(groupKey, singletonList(groupByPattern), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutput));
        return new BiLeftHandSide<>(groupKey, new DirectPatternVariable<>(accumulateOutput, groupByPattern),
                variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        return new TriLeftHandSide<>(groupKey, accumulateOutputB,
                new DirectPatternVariable<>(accumulateOutputC, groupByPattern), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            UniConstraintCollector<A, ?, NewB> collectorB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        return new QuadLeftHandSide<>(groupKey, accumulateOutputB, accumulateOutputC,
                new DirectPatternVariable<>(accumulateOutputD, groupByPattern), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        IndirectPatternVariable<NewB, BiTuple<NewA, NewB>> bPatternVar =
                decompose(groupKey, groupByPattern, newA, newB);
        return new BiLeftHandSide<>(newA, bPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, UniConstraintCollector<A, ?, NewC> collectorC) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)),
                createAccumulateFunction(collectorC, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<NewC> cPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, accumulateOutput);
        return new TriLeftHandSide<>(newA, newB, cPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, UniConstraintCollector<A, ?, NewC> collectorC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new BiTuple<>(keyMappingA.apply(a), keyMappingB.apply(a)),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<NewD> dPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, accumulateOutputD);
        return new QuadLeftHandSide<>(newA, newB, accumulateOutputC, dPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, Function<A, NewC> keyMappingC) {
        Variable<TriTuple<NewA, NewB, NewC>> groupKey = variableFactory.createVariable(TriTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new TriTuple<>(keyMappingA.apply(a), keyMappingB.apply(a), keyMappingC.apply(a)));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        IndirectPatternVariable<NewC, TriTuple<NewA, NewB, NewC>> cPatternVar =
                decompose(groupKey, groupByPattern, newA, newB, newC);
        return new TriLeftHandSide<>(newA, newB, cPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, Function<A, NewC> keyMappingC,
            UniConstraintCollector<A, ?, NewD> collectorD) {
        Variable<TriTuple<NewA, NewB, NewC>> groupKey = variableFactory.createVariable(TriTuple.class, "groupKey");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new TriTuple<>(keyMappingA.apply(a), keyMappingB.apply(a), keyMappingC.apply(a)),
                createAccumulateFunction(collectorD, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        DirectPatternVariable<NewD> dPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, newC, accumulateOutputD);
        return new QuadLeftHandSide<>(newA, newB, newC, dPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(Function<A, NewA> keyMappingA,
            Function<A, NewB> keyMappingB, Function<A, NewC> keyMappingC, Function<A, NewD> keyMappingD) {
        Variable<QuadTuple<NewA, NewB, NewC, NewD>> groupKey = variableFactory.createVariable(QuadTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                a -> new QuadTuple<>(keyMappingA.apply(a), keyMappingB.apply(a), keyMappingC.apply(a),
                        keyMappingD.apply(a)));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        Variable<NewD> newD = variableFactory.createVariable("newD");
        IndirectPatternVariable<NewD, QuadTuple<NewA, NewB, NewC, NewD>> dPatternVar =
                decompose(groupKey, groupByPattern, newA, newB, newC, newD);
        return new QuadLeftHandSide<>(newA, newB, newC, dPatternVar, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andMap(Function<A, NewA> mapping) {
        Variable<NewA> newA = variableFactory.createVariable("mapped");
        PatternVariable<A, ?, ?> mappedVariable = patternVariable
                .bind(newA, mapping);
        IndirectPatternVariable<NewA, ?> newPatternVariableA;
        if (mappedVariable instanceof DirectPatternVariable) {
            newPatternVariableA = new IndirectPatternVariable<>((DirectPatternVariable<A>) mappedVariable, newA, mapping);
        } else if (mappedVariable instanceof IndirectPatternVariable) {
            newPatternVariableA = new IndirectPatternVariable<>((IndirectPatternVariable<A, ?>) mappedVariable, newA, mapping);
        } else {
            throw new IllegalStateException(
                    "Impossible state: Pattern variable is neither direct nor indirect: " + patternVariable);
        }
        return new UniLeftHandSide<>(newPatternVariableA, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andFlattenLast(Function<A, Iterable<NewA>> mapping) {
        Variable<A> source = patternVariable.getPrimaryVariable();
        Variable<NewA> newA = variableFactory.createFlattenedVariable("flattened", source, mapping);
        PatternVariable<NewA, ?, ?> newPatternVariableA = new DirectPatternVariable<>(newA, patternVariable.build());
        return new UniLeftHandSide<>(newPatternVariableA, variableFactory);
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

    private ViewItem<?> buildAccumulate(AccumulateFunction... accFunctions) {
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariable);
        return buildAccumulate(innerAccumulatePattern, accFunctions);
    }

    private <GroupKey_> ViewItem<?> buildGroupBy(Variable<GroupKey_> groupKey,
            Function1<A, GroupKey_> groupKeyExtractor, AccumulateFunction... accFunctions) {
        Variable<A> input = patternVariable.getPrimaryVariable();
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariable);
        return DSL.groupBy(innerGroupByPattern, input, groupKey, groupKeyExtractor, accFunctions);
    }

}
