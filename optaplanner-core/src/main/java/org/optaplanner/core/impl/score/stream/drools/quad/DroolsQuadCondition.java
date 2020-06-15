/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.quad;

import static org.drools.model.DSL.on;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block6;
import org.drools.model.functions.Predicate5;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;
import org.optaplanner.core.impl.score.stream.penta.AbstractPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.FilteringPentaJoiner;
import org.optaplanner.core.impl.score.stream.penta.NonePentaJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

public final class DroolsQuadCondition<A, B, C, D, PatternVar> extends
        DroolsCondition<PatternVar, DroolsQuadRuleStructure<A, B, C, D, PatternVar>> {

    private final ImmediatelyPreviousFilter<QuadPredicate<A, B, C, D>> previousFilter;

    public DroolsQuadCondition(DroolsQuadRuleStructure<A, B, C, D, PatternVar> ruleStructure) {
        this(ruleStructure, null);
    }

    public DroolsQuadCondition(DroolsQuadRuleStructure<A, B, C, D, PatternVar> ruleStructure,
            ImmediatelyPreviousFilter<QuadPredicate<A, B, C, D>> previousFilter) {
        super(ruleStructure);
        this.previousFilter = previousFilter;
    }

    public DroolsQuadCondition<A, B, C, D, PatternVar> andFilter(QuadPredicate<A, B, C, D> predicate) {
        boolean shouldMergeFilters = (previousFilter != null);
        QuadPredicate<A, B, C, D> actualPredicate = shouldMergeFilters ? previousFilter.predicate.and(predicate) : predicate;
        Predicate5<PatternVar, A, B, C, D> filter = (__, a, b, c, d) -> actualPredicate.test(a, b, c, d);
        // If we're merging consecutive filters, amend the original rule structure, before the first filter was applied.
        DroolsQuadRuleStructure<A, B, C, D, PatternVar> actualStructure = shouldMergeFilters ? previousFilter.ruleStructure
                : ruleStructure;
        Variable<A> aVariable = actualStructure.getA();
        Variable<B> bVariable = actualStructure.getB();
        Variable<C> cVariable = actualStructure.getC();
        Variable<D> dVariable = actualStructure.getD();
        DroolsPatternBuilder<PatternVar> newTargetPattern = actualStructure.getPrimaryPatternBuilder()
                .expand(p -> p.expr("Filter using " + actualPredicate, aVariable, bVariable, cVariable, dVariable,
                        filter));
        DroolsQuadRuleStructure<A, B, C, D, PatternVar> newRuleStructure = new DroolsQuadRuleStructure<>(aVariable,
                bVariable, cVariable, dVariable, newTargetPattern, actualStructure.getShelvedRuleItems(),
                actualStructure.getPrerequisites(), actualStructure.getDependents(),
                actualStructure.getVariableIdSupplier());
        ImmediatelyPreviousFilter<QuadPredicate<A, B, C, D>> newPreviousFilter =
                new ImmediatelyPreviousFilter<QuadPredicate<A, B, C, D>>(actualStructure, actualPredicate);
        // Carry forward the information for filter merging.
        return new DroolsQuadCondition<>(newRuleStructure, newPreviousFilter);
    }

    @SafeVarargs
    public final <E> DroolsQuadCondition<A, B, C, D, PatternVar> andIfExists(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return andIfExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    public final <E> DroolsQuadCondition<A, B, C, D, PatternVar> andIfNotExists(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return andIfExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <E> DroolsQuadCondition<A, B, C, D, PatternVar> andIfExistsOrNot(boolean shouldExist,
            Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners) {
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
                // We merge all filters into one, so that we don't pay the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? joiner.getFilter() : finalFilter.and(joiner.getFilter());
            }
        }
        return applyJoiners(otherClass, finalJoiner, finalFilter, shouldExist);
    }

    private <E> DroolsQuadCondition<A, B, C, D, PatternVar> applyJoiners(Class<E> otherClass,
            AbstractPentaJoiner<A, B, C, D, E> joiner, PentaPredicate<A, B, C, D, E> predicate, boolean shouldExist) {
        Variable<E> toExist = (Variable<E>) ruleStructure.createVariable(otherClass, "quadToExist");
        PatternDef<E> existencePattern = PatternDSL.pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        // There is no index higher than beta in Drools, therefore we replace joining with a filter.
        PentaPredicate<A, B, C, D, E> joinFilter = joiner::matches;
        PentaPredicate<A, B, C, D, E> result = predicate == null ? joinFilter : joinFilter.and(predicate);
        // And finally we add the filter to the E pattern.
        return applyFilters(existencePattern, result, shouldExist);
    }

    private <E> DroolsQuadCondition<A, B, C, D, PatternVar> applyFilters(PatternDef<E> existencePattern,
            PentaPredicate<A, B, C, D, E> predicate, boolean shouldExist) {
        PatternDef<E> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, ruleStructure.getA(), ruleStructure.getB(),
                        ruleStructure.getC(), ruleStructure.getD(), (e, a, b, c, d) -> predicate.test(a, b, c, d, e));
        return new DroolsQuadCondition<>(ruleStructure.existsOrNot(possiblyFilteredExistencePattern, shouldExist));
    }

    @Override
    protected <InTuple> PatternDef<PatternVar> bindTupleVariableOnFirstGrouping(PatternDef<PatternVar> pattern,
            Variable<InTuple> tupleVariable) {
        return pattern.bind(tupleVariable, ruleStructure.getA(), ruleStructure.getB(), ruleStructure.getC(),
                (d, a, b, c) -> (InTuple) new QuadTuple<>(a, b, c, (D) d));
    }

    public <NewA, __> DroolsUniCondition<NewA, NewA> andCollect(
            QuadConstraintCollector<A, B, C, D, __, NewA> collector) {
        DroolsQuadAccumulateFunction<A, B, C, D, __, NewA> bridge = new DroolsQuadAccumulateFunction<>(collector);
        return collect(bridge);
    }

    public <NewA> DroolsUniCondition<NewA, ?> andGroup(QuadFunction<A, B, C, D, NewA> groupKeyMapping) {
        DroolsBiCondition<NewA, ?, ? extends BiTuple<NewA, ?>> biCondition = andGroupWithCollect(groupKeyMapping, null);
        DroolsBiRuleStructure<NewA, ?, ? extends BiTuple<NewA, ?>> biRuleStructure = biCondition.getRuleStructure();
        // Downgrade the bi-stream to a uni-stream by ignoring the dummy no-op collector variable.
        DroolsUniRuleStructure<NewA, ? extends BiTuple<NewA, ?>> uniRuleStructure = new DroolsUniRuleStructure<>(
                biRuleStructure);
        return new DroolsUniCondition<>(uniRuleStructure);
    }

    public <NewA, NewB, __> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupWithCollect(
            QuadFunction<A, B, C, D, NewA> groupKeyMapping, QuadConstraintCollector<A, B, C, D, __, NewB> collector) {
        return groupWithCollect(() -> new DroolsQuadToBiGroupByAccumulator<>(groupKeyMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, ?> andGroupBi(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping) {
        DroolsTriCondition<NewA, NewB, ?, ? extends TriTuple<NewA, NewB, ?>> triCondition = andGroupBiWithCollect(
                groupKeyAMapping, groupKeyBMapping, null);
        DroolsTriRuleStructure<NewA, NewB, ?, ? extends TriTuple<NewA, NewB, ?>> triRuleStructure = triCondition
                .getRuleStructure();
        // Downgrade the tri-stream to a bi-stream by ignoring the dummy no-op collector variable.
        DroolsBiRuleStructure<NewA, NewB, ? extends TriTuple<NewA, NewB, ?>> biRuleStructure = new DroolsBiRuleStructure<>(
                triRuleStructure);
        return new DroolsBiCondition<>(biRuleStructure);
    }

    public <NewA, NewB, NewC, __> DroolsTriCondition<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>> andGroupBiWithCollect(
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, __, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsQuadToTriGroupByAccumulator<>(groupKeyAMapping, groupKeyBMapping,
                collector, getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
            andGroupBiWithCollectBi(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
                    QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
                    QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        return groupBiWithCollectBi(() -> new DroolsQuadGroupByAccumulator<>(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD, getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, ToIntQuadFunction<A, B, C, D> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                        matchWeighter.applyAsInt(a, b, c, d)));

    }

    public List<RuleItemBuilder<?>> completeWithScoring(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, ToLongQuadFunction<A, B, C, D> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                        matchWeighter.applyAsLong(a, b, c, d)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(DroolsConstraint<?> constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(constraint, drools, scoreHolder,
                        matchWeighter.apply(a, b, c, d)));
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block6<Drools, ScoreHolder, A, B, C, D> consequenceImpl) {
        ConsequenceBuilder._5<ScoreHolder, A, B, C, D> consequence = on(scoreHolderGlobal, ruleStructure.getA(),
                ruleStructure.getB(), ruleStructure.getC(), ruleStructure.getD())
                        .execute(consequenceImpl);
        return ruleStructure.finish(consequence);
    }

}
