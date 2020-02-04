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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.UnaryOperator;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block4;
import org.drools.model.functions.Predicate3;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.FilteringTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

import static org.drools.model.DSL.on;

public final class DroolsBiCondition<A, B, PatternVar>
        extends DroolsCondition<PatternVar, DroolsBiRuleStructure<A, B, PatternVar>> {

    public DroolsBiCondition(DroolsBiRuleStructure<A, B, PatternVar> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsBiCondition<A, B, PatternVar> andFilter(BiPredicate<A, B> predicate) {
        Predicate3<PatternVar, A, B> filter = (__, a, b) -> predicate.test(a, b);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        DroolsPatternBuilder<PatternVar> newTargetPattern = ruleStructure.getPrimaryPatternBuilder()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, filter));
        DroolsBiRuleStructure<A, B, PatternVar> newRuleStructure = new DroolsBiRuleStructure<>(aVariable, bVariable,
                newTargetPattern, ruleStructure.getShelvedRuleItems(), ruleStructure.getPrerequisites(),
                ruleStructure.getDependents(), ruleStructure.getVariableIdSupplier());
        return new DroolsBiCondition<>(newRuleStructure);
    }

    public <C, CPatternVar> DroolsTriCondition<A, B, C, CPatternVar> andJoin(
            DroolsUniCondition<C, CPatternVar> cCondition, AbstractTriJoiner<A, B, C> triJoiner) {
        DroolsUniRuleStructure<C, CPatternVar> cRuleStructure = cCondition.getRuleStructure();
        Variable<C> cVariable = cRuleStructure.getA();
        UnaryOperator<PatternDef<CPatternVar>> expander = p -> p.expr("Filter using " + triJoiner,
                ruleStructure.getA(), ruleStructure.getB(), cVariable, (__, a, b, c) -> triJoiner.matches(a, b, c));
        DroolsUniRuleStructure<C, CPatternVar> newCRuleStructure = cRuleStructure.amend(expander);
        return new DroolsTriCondition<>(new DroolsTriRuleStructure<>(ruleStructure, newCRuleStructure,
                ruleStructure.getVariableIdSupplier()));
    }

    @SafeVarargs
    public final <C> DroolsBiCondition<A, B, PatternVar> andIfExists(Class<C> otherClass,
            TriJoiner<A, B, C>... joiners) {
        return andIfExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    public final <C> DroolsBiCondition<A, B, PatternVar> andIfNotExists(Class<C> otherClass,
            TriJoiner<A, B, C>... joiners) {
        return andIfExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <C> DroolsBiCondition<A, B, PatternVar> andIfExistsOrNot(boolean shouldExist, Class<C> otherClass,
            TriJoiner<A, B, C>... joiners) {
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
                    finalJoiner = finalJoiner == null ?
                            joiner :
                            AbstractTriJoiner.merge(finalJoiner, joiner);
                }
            } else {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // We merge all filters into one, so that we don't pay the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ?
                        joiner.getFilter() :
                        finalFilter.and(joiner.getFilter());
            }
        }
        return applyJoiners(otherClass, finalJoiner, finalFilter, shouldExist);
    }

    private <C> DroolsBiCondition<A, B, PatternVar> applyJoiners(Class<C> otherClass,
            AbstractTriJoiner<A, B, C> joiner, TriPredicate<A, B, C> predicate, boolean shouldExist) {
        Variable<C> toExist = (Variable<C>) ruleStructure.createVariable(otherClass, "biToExist");
        PatternDef<C> existencePattern = PatternDSL.pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        // There is no gamma index in Drools, therefore we replace joining with a filter.
        TriPredicate<A, B, C> joinFilter = joiner::matches;
        TriPredicate<A, B, C> result = predicate == null ? joinFilter : joinFilter.and(predicate);
        // And finally we add the filter to the C pattern
        return applyFilters(existencePattern, result, shouldExist);
    }

    private <C> DroolsBiCondition<A, B, PatternVar> applyFilters(PatternDef<C> existencePattern,
            TriPredicate<A, B, C> predicate, boolean shouldExist) {
        PatternDef<C> possiblyFilteredExistencePattern = predicate == null ?
                existencePattern :
                existencePattern.expr("Filter using " + predicate, ruleStructure.getA(), ruleStructure.getB(),
                        (c, a, b) -> predicate.test(a, b, c));
        return new DroolsBiCondition<>(ruleStructure.existsOrNot(possiblyFilteredExistencePattern, shouldExist));
    }

    public <NewA, __> DroolsUniCondition<NewA, NewA> andCollect(BiConstraintCollector<A, B, __, NewA> collector) {
        DroolsBiAccumulateFunctionBridge<A, B, __, NewA> bridge = new DroolsBiAccumulateFunctionBridge<>(collector);
        return collect(bridge, (pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(),
                (b, a) -> new BiTuple<>(a, (B) b)));
    }

    public <NewA> DroolsUniCondition<NewA, NewA> andGroup(BiFunction<A, B, NewA> groupKeyMapping) {
        return group((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(),
                (b, a) -> groupKeyMapping.apply(a, (B) b)));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupWithCollect(
            BiFunction<A, B, NewA> groupKeyMapping, BiConstraintCollector<A, B, ?, NewB> collector) {
        return groupWithCollect(() -> new DroolsBiGroupByInvoker<>(groupKeyMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupBi(
            BiFunction<A, B, NewA> groupKeyAMapping, BiFunction<A, B, NewB> groupKeyBMapping) {
        return groupBi((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), (b, a) -> {
            final NewA newA = groupKeyAMapping.apply(a, (B) b);
            final NewB newB = groupKeyBMapping.apply(a, (B) b);
            return new BiTuple<>(newA, newB);
        }));
    }

    public <NewA, NewB, NewC> DroolsTriCondition<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>> andGroupBiWithCollect(
            BiFunction<A, B, NewA> groupKeyAMapping, BiFunction<A, B, NewB> groupKeyBMapping,
            BiConstraintCollector<A, B, ?, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsBiToTriGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
    andGroupBiWithCollectBi(BiFunction<A, B, NewA> groupKeyAMapping, BiFunction<A, B, NewB> groupKeyBMapping,
            BiConstraintCollector<A, B, ?, NewC> collectorC, BiConstraintCollector<A, B, ?, NewD> collectorD) {
        return groupBiWithCollectBi(() -> new DroolsBiToQuadGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD, getRuleStructure().getA(), getRuleStructure().getB()));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, __, ___) -> impactScore(drools, scoreHolder));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntBiFunction<A, B> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b) -> impactScore(drools, scoreHolder, matchWeighter.applyAsInt(a, b)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongBiFunction<A, B> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b) -> impactScore(drools, scoreHolder, matchWeighter.applyAsLong(a, b)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            BiFunction<A, B, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b) -> impactScore(drools, scoreHolder, matchWeighter.apply(a, b)));
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block4<Drools, ScoreHolder, A, B> consequenceImpl) {
        ConsequenceBuilder._3<ScoreHolder, A, B> consequence =
                on(scoreHolderGlobal, ruleStructure.getA(), ruleStructure.getB())
                        .execute(consequenceImpl);
        return ruleStructure.finish(consequence);
    }
}
