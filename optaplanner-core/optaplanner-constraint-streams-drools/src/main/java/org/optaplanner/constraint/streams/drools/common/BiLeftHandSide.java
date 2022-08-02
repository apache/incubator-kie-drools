package org.optaplanner.constraint.streams.drools.common;

import static java.util.Collections.singletonList;
import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Stream;

import org.drools.model.BetaIndex2;
import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate3;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.constraint.streams.common.tri.FilteringTriJoiner;
import org.optaplanner.constraint.streams.drools.DroolsVariableFactory;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.JoinerType;

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

    private final PatternVariable<A, ?, ?> patternVariableA;
    private final PatternVariable<B, ?, ?> patternVariableB;
    private final BiRuleContext<A, B> ruleContext;

    BiLeftHandSide(Variable<A> left, PatternVariable<B, ?, ?> right, DroolsVariableFactory variableFactory) {
        this(new DetachedPatternVariable<>(left), right, variableFactory);
    }

    BiLeftHandSide(PatternVariable<A, ?, ?> left, PatternVariable<B, ?, ?> right,
            DroolsVariableFactory variableFactory) {
        super(variableFactory);
        this.patternVariableA = Objects.requireNonNull(left);
        this.patternVariableB = Objects.requireNonNull(right);
        this.ruleContext = buildRuleContext();
    }

    private BiRuleContext<A, B> buildRuleContext() {
        ViewItem<?>[] viewItems = Stream.of(patternVariableA, patternVariableB)
                .flatMap(variable -> variable.build().stream())
                .toArray((IntFunction<ViewItem<?>[]>) ViewItem[]::new);
        return new BiRuleContext<>(patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(),
                viewItems);
    }

    public BiLeftHandSide<A, B> andFilter(BiPredicate<A, B> predicate) {
        return new BiLeftHandSide<>(patternVariableA,
                patternVariableB.filter(predicate, patternVariableA.getPrimaryVariable()), variableFactory);
    }

    private <C> BiLeftHandSide<A, B> applyJoiners(Class<C> otherFactType, Predicate<C> nullityFilter,
            DefaultTriJoiner<A, B, C> joiner, TriPredicate<A, B, C> predicate, boolean shouldExist) {
        Variable<C> toExist = variableFactory.createVariable(otherFactType, "toExist");
        PatternDSL.PatternDef<C> existencePattern = pattern(toExist);
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
            BiFunction<A, B, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
            Function<C, Object> rightMapping = joiner.getRightMapping(mappingIndex);
            Predicate3<C, A, B> joinPredicate =
                    (c, a, b) -> joinerType.matches(leftMapping.apply(a, b), rightMapping.apply(c));
            existencePattern = existencePattern.expr("Join using joiner #" + mappingIndex + " in " + joiner,
                    patternVariableA.getPrimaryVariable(), patternVariableB.getPrimaryVariable(), joinPredicate,
                    index(joiner, mappingIndex));
        }
        return applyFilters(existencePattern, predicate, shouldExist);
    }

    private <C> BetaIndex2<C, A, B, ?> index(DefaultTriJoiner<A, B, C> joiner, int mappingIndex) {
        JoinerType joinerType = joiner.getJoinerType(mappingIndex);
        BiFunction<A, B, Object> leftMapping = joiner.getLeftMapping(mappingIndex);
        Function<C, Object> rightMapping = joiner.getRightMapping(mappingIndex);
        if (joinerType == JoinerType.EQUAL) {
            return betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex, rightMapping::apply,
                    leftMapping::apply, Object.class);
        } else { // Drools beta index on LT/LTE/GT/GTE requires Comparable.
            return betaIndexedBy(Comparable.class, getConstraintType(joinerType), mappingIndex,
                    c -> (Comparable) rightMapping.apply(c), leftMapping::apply, Comparable.class);
        }
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
        return new BiLeftHandSide<>(patternVariableA, patternVariableB.addDependentExpression(existenceExpression),
                variableFactory);
    }

    private <C> BiLeftHandSide<A, B> existsOrNot(Class<C> cClass, TriJoiner<A, B, C>[] joiners,
            Predicate<C> nullityFilter, boolean shouldExist) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern
        DefaultTriJoiner<A, B, C> finalJoiner = null;
        TriPredicate<A, B, C> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            TriJoiner<A, B, C> joiner = joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof FilteringTriJoiner) {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                FilteringTriJoiner<A, B, C> castJoiner = (FilteringTriJoiner<A, B, C>) joiner;
                finalFilter = finalFilter == null ? castJoiner.getFilter() : finalFilter.and(castJoiner.getFilter());
            } else {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    DefaultTriJoiner<A, B, C> castJoiner = (DefaultTriJoiner<A, B, C>) joiner;
                    finalJoiner = finalJoiner == null ? castJoiner : finalJoiner.and(castJoiner);
                }
            }
        }
        return applyJoiners(cClass, nullityFilter, finalJoiner, finalFilter, shouldExist);
    }

    public <C> BiLeftHandSide<A, B> andExists(Class<C> cClass, TriJoiner<A, B, C>[] joiners, Predicate<C> nullityFilter) {
        return existsOrNot(cClass, joiners, nullityFilter, true);
    }

    public <C> BiLeftHandSide<A, B> andNotExists(Class<C> cClass, TriJoiner<A, B, C>[] joiners, Predicate<C> nullityFilter) {
        return existsOrNot(cClass, joiners, nullityFilter, false);
    }

    public <C> TriLeftHandSide<A, B, C> andJoin(UniLeftHandSide<C> right, TriJoiner<A, B, C> joiner) {
        DefaultTriJoiner<A, B, C> castJoiner = (DefaultTriJoiner<A, B, C>) joiner;
        PatternVariable<C, ?, ?> newRight = right.getPatternVariableA();
        int joinerCount = castJoiner.getJoinerCount();
        for (int mappingIndex = 0; mappingIndex < joinerCount; mappingIndex++) {
            JoinerType joinerType = castJoiner.getJoinerType(mappingIndex);
            newRight = newRight.filterForJoin(patternVariableA.getPrimaryVariable(),
                    patternVariableB.getPrimaryVariable(), castJoiner, joinerType, mappingIndex);
        }
        return new TriLeftHandSide<>(patternVariableA, patternVariableB, newRight, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(BiConstraintCollector<A, B, ?, NewA> collector) {
        Variable<NewA> accumulateOutput = variableFactory.createVariable("collected");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> outerAccumulatePattern = buildAccumulate(innerAccumulatePattern,
                createAccumulateFunction(collector, accumulateOutput));
        return new UniLeftHandSide<>(accumulateOutput, singletonList(outerAccumulatePattern), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(BiConstraintCollector<A, B, ?, NewA> collectorA,
            BiConstraintCollector<A, B, ?, NewB> collectorB) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> outerAccumulatePattern = buildAccumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB));
        return new BiLeftHandSide<>(accumulateOutputA,
                new DirectPatternVariable<>(accumulateOutputB, outerAccumulatePattern), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(
            BiConstraintCollector<A, B, ?, NewA> collectorA, BiConstraintCollector<A, B, ?, NewB> collectorB,
            BiConstraintCollector<A, B, ?, NewC> collectorC) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> outerAccumulatePattern = buildAccumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        return new TriLeftHandSide<>(accumulateOutputA, accumulateOutputB,
                new DirectPatternVariable<>(accumulateOutputC, outerAccumulatePattern),
                variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            BiConstraintCollector<A, B, ?, NewA> collectorA, BiConstraintCollector<A, B, ?, NewB> collectorB,
            BiConstraintCollector<A, B, ?, NewC> collectorC, BiConstraintCollector<A, B, ?, NewD> collectorD) {
        Variable<NewA> accumulateOutputA = variableFactory.createVariable("collectedA");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("collectedB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("collectedC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("collectedD");
        ViewItem<?> innerAccumulatePattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        ViewItem<?> outerAccumulatePattern = buildAccumulate(innerAccumulatePattern,
                createAccumulateFunction(collectorA, accumulateOutputA),
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        return new QuadLeftHandSide<>(accumulateOutputA, accumulateOutputB, accumulateOutputC,
                new DirectPatternVariable<>(accumulateOutputD, outerAccumulatePattern), variableFactory);
    }

    /**
     * Creates a Drools accumulate function based on a given collector. The accumulate function will take the pattern
     * variables as input and return its result into another {@link Variable}.
     *
     * @param <Out> type of the accumulate result
     * @param collector collector to use in the accumulate function
     * @param out variable in which to store accumulate result
     * @return Drools accumulate function
     */
    private <Out> AccumulateFunction createAccumulateFunction(BiConstraintCollector<A, B, ?, Out> collector,
            Variable<Out> out) {
        Variable<A> variableA = patternVariableA.getPrimaryVariable();
        Variable<B> variableB = patternVariableB.getPrimaryVariable();
        return new AccumulateFunction(null, () -> new BiAccumulator<>(variableA, variableB, collector))
                .with(variableA, variableB)
                .as(out);
    }

    public <NewA> UniLeftHandSide<NewA> andGroupBy(BiFunction<A, B, NewA> keyMapping) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMapping::apply);
        return new UniLeftHandSide<>(groupKey, singletonList(groupByPattern), variableFactory);
    }

    public <NewA, NewB> BiLeftHandSide<NewA, NewB> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiConstraintCollector<A, B, ?, NewB> collectorB) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutput));
        return new BiLeftHandSide<>(groupKey,
                new DirectPatternVariable<>(accumulateOutput, groupByPattern), variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiConstraintCollector<A, B, ?, NewB> collectorB, BiConstraintCollector<A, B, ?, NewC> collectorC) {
        Variable<NewA> groupKey = variableFactory.createVariable("groupKey");
        Variable<NewB> accumulateOutputB = variableFactory.createVariable("outputB");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, keyMappingA::apply,
                createAccumulateFunction(collectorB, accumulateOutputB),
                createAccumulateFunction(collectorC, accumulateOutputC));
        return new TriLeftHandSide<>(groupKey, accumulateOutputB,
                new DirectPatternVariable<>(accumulateOutputC, groupByPattern), variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            BiFunction<A, B, NewA> keyMappingA, BiConstraintCollector<A, B, ?, NewB> collectorB,
            BiConstraintCollector<A, B, ?, NewC> collectorC, BiConstraintCollector<A, B, ?, NewD> collectorD) {
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
            BiFunction<A, B, NewB> keyMappingB) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey, createCompositeBiGroupKey(keyMappingA, keyMappingB));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        IndirectPatternVariable<NewB, BiTuple<NewA, NewB>> bPatternVar =
                decompose(groupKey, groupByPattern, newA, newB);
        return new BiLeftHandSide<>(newA, bPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB, BiConstraintCollector<A, B, ?, NewC> collectorC) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutput = variableFactory.createVariable("output");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateOutput));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<NewC> cPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, accumulateOutput);
        return new TriLeftHandSide<>(newA, newB, cPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB, BiConstraintCollector<A, B, ?, NewC> collectorC,
            BiConstraintCollector<A, B, ?, NewD> collectorD) {
        Variable<BiTuple<NewA, NewB>> groupKey = variableFactory.createVariable(BiTuple.class, "groupKey");
        Variable<NewC> accumulateOutputC = variableFactory.createVariable("outputC");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                createCompositeBiGroupKey(keyMappingA, keyMappingB),
                createAccumulateFunction(collectorC, accumulateOutputC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        DirectPatternVariable<NewD> dPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, accumulateOutputD);
        return new QuadLeftHandSide<>(newA, newB, accumulateOutputC, dPatternVar, variableFactory);
    }

    /**
     * Takes group key mappings and merges them in such a way that the result is a single composite key.
     * This is necessary because Drools groupBy can only take a single key - therefore multiple variables need to be
     * converted into a singular composite variable.
     *
     * @param keyMappingA mapping for the first variable
     * @param keyMappingB mapping for the second variable
     * @param keyMappingC mapping for the third variable
     * @param <NewA> generic type of the first variable
     * @param <NewB> generic type of the second variable
     * @param <NewC> generic type of the third variable
     * @return never null, Drools function to convert the keys to a singular composite key
     */
    private <NewA, NewB, NewC> Function2<A, B, TriTuple<NewA, NewB, NewC>> createCompositeTriGroupKey(
            BiFunction<A, B, NewA> keyMappingA, BiFunction<A, B, NewB> keyMappingB,
            BiFunction<A, B, NewC> keyMappingC) {
        return (a, b) -> new TriTuple<>(keyMappingA.apply(a, b), keyMappingB.apply(a, b), keyMappingC.apply(a, b));
    }

    public <NewA, NewB, NewC> TriLeftHandSide<NewA, NewB, NewC> andGroupBy(BiFunction<A, B, NewA> keyMappingA,
            BiFunction<A, B, NewB> keyMappingB, BiFunction<A, B, NewC> keyMappingC) {
        Variable<TriTuple<NewA, NewB, NewC>> groupKey = variableFactory.createVariable(TriTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                createCompositeTriGroupKey(keyMappingA, keyMappingB, keyMappingC));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        IndirectPatternVariable<NewC, TriTuple<NewA, NewB, NewC>> cPatternVar =
                decompose(groupKey, groupByPattern, newA, newB, newC);
        return new TriLeftHandSide<>(newA, newB, cPatternVar, variableFactory);
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            BiFunction<A, B, NewA> keyMappingA, BiFunction<A, B, NewB> keyMappingB,
            BiFunction<A, B, NewC> keyMappingC, BiConstraintCollector<A, B, ?, NewD> collectorD) {
        Variable<TriTuple<NewA, NewB, NewC>> groupKey = variableFactory.createVariable(TriTuple.class, "groupKey");
        Variable<NewD> accumulateOutputD = variableFactory.createVariable("outputD");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                createCompositeTriGroupKey(keyMappingA, keyMappingB, keyMappingC),
                createAccumulateFunction(collectorD, accumulateOutputD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        DirectPatternVariable<NewD> dPatternVar =
                decomposeWithAccumulate(groupKey, groupByPattern, newA, newB, newC, accumulateOutputD);
        return new QuadLeftHandSide<>(newA, newB, newC, dPatternVar, variableFactory);
    }

    /**
     * Takes group key mappings and merges them in such a way that the result is a single composite key.
     * This is necessary because Drools groupBy can only take a single key - therefore multiple variables need to be
     * converted into a singular composite variable.
     *
     * @param keyMappingA mapping for the first variable
     * @param keyMappingB mapping for the second variable
     * @param keyMappingC mapping for the third variable
     * @param <NewA> generic type of the first variable
     * @param <NewB> generic type of the second variable
     * @param <NewC> generic type of the third variable
     * @return never null, Drools function to convert the keys to a singular composite key
     */
    private <NewA, NewB, NewC, NewD> Function2<A, B, QuadTuple<NewA, NewB, NewC, NewD>>
            createCompositeQuadGroupKey(BiFunction<A, B, NewA> keyMappingA, BiFunction<A, B, NewB> keyMappingB,
                    BiFunction<A, B, NewC> keyMappingC, BiFunction<A, B, NewD> keyMappingD) {
        return (a, b) -> new QuadTuple<>(keyMappingA.apply(a, b), keyMappingB.apply(a, b), keyMappingC.apply(a, b),
                keyMappingD.apply(a, b));
    }

    public <NewA, NewB, NewC, NewD> QuadLeftHandSide<NewA, NewB, NewC, NewD> andGroupBy(
            BiFunction<A, B, NewA> keyMappingA, BiFunction<A, B, NewB> keyMappingB, BiFunction<A, B, NewC> keyMappingC,
            BiFunction<A, B, NewD> keyMappingD) {
        Variable<QuadTuple<NewA, NewB, NewC, NewD>> groupKey = variableFactory.createVariable(QuadTuple.class, "groupKey");
        ViewItem<?> groupByPattern = buildGroupBy(groupKey,
                createCompositeQuadGroupKey(keyMappingA, keyMappingB, keyMappingC, keyMappingD));
        Variable<NewA> newA = variableFactory.createVariable("newA");
        Variable<NewB> newB = variableFactory.createVariable("newB");
        Variable<NewC> newC = variableFactory.createVariable("newC");
        Variable<NewD> newD = variableFactory.createVariable("newD");
        IndirectPatternVariable<NewD, QuadTuple<NewA, NewB, NewC, NewD>> dPatternVar =
                decompose(groupKey, groupByPattern, newA, newB, newC, newD);
        return new QuadLeftHandSide<>(newA, newB, newC, dPatternVar, variableFactory);
    }

    public <NewA> UniLeftHandSide<NewA> andMap(BiFunction<A, B, NewA> mapping) {
        Variable<NewA> newA = variableFactory.createVariable("mapped", patternVariableA.getPrimaryVariable(),
                patternVariableB.getPrimaryVariable(), mapping);
        List<ViewItem<?>> allPrerequisites = mergeViewItems(patternVariableA, patternVariableB);
        DirectPatternVariable<NewA> newPatternVariableA = new DirectPatternVariable<>(newA, allPrerequisites);
        return new UniLeftHandSide<>(newPatternVariableA, variableFactory);
    }

    public <NewB> BiLeftHandSide<A, NewB> andFlattenLast(Function<B, Iterable<NewB>> mapping) {
        Variable<B> source = patternVariableB.getPrimaryVariable();
        Variable<NewB> newB = variableFactory.createFlattenedVariable("flattened", source, mapping);
        List<ViewItem<?>> allPrerequisites = mergeViewItems(patternVariableA, patternVariableB);
        PatternVariable<NewB, ?, ?> newPatternVariableB = new DirectPatternVariable<>(newB, allPrerequisites);
        return new BiLeftHandSide<>(patternVariableA.getPrimaryVariable(), newPatternVariableB, variableFactory);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate() {
        return ruleContext.newRuleBuilder();
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(ToIntBiFunction<A, B> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(ToLongBiFunction<A, B> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

    public <Solution_> RuleBuilder<Solution_> andTerminate(BiFunction<A, B, BigDecimal> matchWeighter) {
        return ruleContext.newRuleBuilder(matchWeighter);
    }

    private <GroupKey_> ViewItem<?> buildGroupBy(Variable<GroupKey_> groupKey,
            Function2<A, B, GroupKey_> groupKeyExtractor, AccumulateFunction... accFunctions) {
        Variable<A> inputA = patternVariableA.getPrimaryVariable();
        Variable<B> inputB = patternVariableB.getPrimaryVariable();
        ViewItem<?> innerGroupByPattern = joinViewItemsWithLogicalAnd(patternVariableA, patternVariableB);
        return DSL.groupBy(innerGroupByPattern, inputA, inputB, groupKey, groupKeyExtractor, accFunctions);
    }

}
