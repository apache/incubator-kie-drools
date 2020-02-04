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

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Predicate4;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadCondition;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.FilteringQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.NoneQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

import static org.drools.model.DSL.on;

public final class DroolsTriCondition<A, B, C, PatternVar>
        extends DroolsCondition<PatternVar, DroolsTriRuleStructure<A, B, C, PatternVar>> {

    public DroolsTriCondition(DroolsTriRuleStructure<A, B, C, PatternVar> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsTriCondition<A, B, C, PatternVar> andFilter(TriPredicate<A, B, C> predicate) {
        Predicate4<PatternVar, A, B, C> filter = (__, a, b, c) -> predicate.test(a, b, c);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        Variable<C> cVariable = ruleStructure.getC();
        DroolsPatternBuilder<PatternVar> newTargetPattern = ruleStructure.getPrimaryPatternBuilder()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, cVariable, filter));
        DroolsTriRuleStructure<A, B, C, PatternVar> newRuleStructure =
                new DroolsTriRuleStructure<>(aVariable, bVariable, cVariable, newTargetPattern,
                        ruleStructure.getShelvedRuleItems(), ruleStructure.getPrerequisites(),
                        ruleStructure.getDependents(), ruleStructure.getVariableIdSupplier());
        return new DroolsTriCondition<>(newRuleStructure);
    }

    public <D, DPatternVar> DroolsQuadCondition<A, B, C, D, DPatternVar> andJoin(
            DroolsUniCondition<D, DPatternVar> dCondition, AbstractQuadJoiner<A, B, C, D> quadJoiner) {
        DroolsUniRuleStructure<D, DPatternVar> dRuleStructure = dCondition.getRuleStructure();
        Variable<D> dVariable = dRuleStructure.getA();
        UnaryOperator<PatternDef<DPatternVar>> expander =
                p -> p.expr("Filter using " + quadJoiner, ruleStructure.getA(), ruleStructure.getB(),
                        ruleStructure.getC(), dVariable, (__, a, b, c, d) -> quadJoiner.matches(a, b, c, d));
        DroolsUniRuleStructure<D, DPatternVar> newDRuleStructure = dRuleStructure.amend(expander);
        return new DroolsQuadCondition<>(new DroolsQuadRuleStructure<>(ruleStructure, newDRuleStructure,
                ruleStructure.getVariableIdSupplier()));
    }

    @SafeVarargs
    public final <D> DroolsTriCondition<A, B, C, PatternVar> andIfExists(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        return andIfExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    public final <D> DroolsTriCondition<A, B, C, PatternVar> andIfNotExists(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        return andIfExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <D> DroolsTriCondition<A, B, C, PatternVar> andIfExistsOrNot(boolean shouldExist, Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
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
                    finalJoiner = finalJoiner == null ?
                            joiner :
                            AbstractQuadJoiner.merge(finalJoiner, joiner);
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

    private <D> DroolsTriCondition<A, B, C, PatternVar> applyJoiners(Class<D> otherClass,
            AbstractQuadJoiner<A, B, C, D> joiner, QuadPredicate<A, B, C, D> predicate, boolean shouldExist) {
        Variable<D> toExist = (Variable<D>) ruleStructure.createVariable(otherClass, "triToExist");
        PatternDef<D> existencePattern = PatternDSL.pattern(toExist);
        if (joiner == null) {
            return applyFilters(existencePattern, predicate, shouldExist);
        }
        // There is no index higher than beta in Drools, therefore we replace joining with a filter.
        QuadPredicate<A, B, C, D> joinFilter = joiner::matches;
        QuadPredicate<A, B, C, D> result = predicate == null ? joinFilter : joinFilter.and(predicate);
        // And finally we add the filter to the D pattern.
        return applyFilters(existencePattern, result, shouldExist);
    }

    private <D> DroolsTriCondition<A, B, C, PatternVar> applyFilters(PatternDef<D> existencePattern,
            QuadPredicate<A, B, C, D> predicate, boolean shouldExist) {
        PatternDef<D> possiblyFilteredExistencePattern = predicate == null ?
                existencePattern :
                existencePattern.expr("Filter using " + predicate, ruleStructure.getA(), ruleStructure.getB(),
                        ruleStructure.getC(), (d, a, b, c) -> predicate.test(a, b, c, d));
        return new DroolsTriCondition<>(ruleStructure.existsOrNot(possiblyFilteredExistencePattern, shouldExist));
    }

    public <NewA, __> DroolsUniCondition<NewA, NewA> andCollect(TriConstraintCollector<A, B, C, __, NewA> collector) {
        DroolsTriAccumulateFunctionBridge<A, B, C, __, NewA> bridge =
                new DroolsTriAccumulateFunctionBridge<>(collector);
        return collect(bridge, (pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(),
                ruleStructure.getB(), (c, a, b) -> new TriTuple<>(a, b, (C) c)));
    }

    public <NewA> DroolsUniCondition<NewA, NewA> andGroup(TriFunction<A, B, C, NewA> groupKeyMapping) {
        return super.group((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                (c, a, b) -> groupKeyMapping.apply(a, b, (C) c)));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupWithCollect(
            TriFunction<A, B, C, NewA> groupKeyMapping, TriConstraintCollector<A, B, C, ?, NewB> collector) {
        return groupWithCollect(() -> new DroolsTriToBiGroupByInvoker<>(groupKeyMapping, collector, getRuleStructure().getA(),
                        getRuleStructure().getB(), getRuleStructure().getC()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupBi(
            TriFunction<A, B, C, NewA> groupKeyAMapping, TriFunction<A, B, C, NewB> groupKeyBMapping) {
        return groupBi((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                (c, a, b) -> {
                    final NewA newA = groupKeyAMapping.apply(a, b, (C) c);
                    final NewB newB = groupKeyBMapping.apply(a, b, (C) c);
                    return new BiTuple<>(newA, newB);
                }));
    }

    public <NewA, NewB, NewC> DroolsTriCondition<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>> andGroupBiWithCollect(
            TriFunction<A, B, C, NewA> groupKeyAMapping, TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ?, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsTriGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
    andGroupBiWithCollectBi(TriFunction<A, B, C, NewA> groupKeyAMapping, TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ?, NewC> collectorC, TriConstraintCollector<A, B, C, ?, NewD> collectorD) {
        return groupBiWithCollectBi(() -> new DroolsTriToQuadGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD, getRuleStructure().getA(), getRuleStructure().getB(),
                getRuleStructure().getC()));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c) -> impactScore(drools, scoreHolder));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntTriFunction<A, B, C> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c) -> impactScore(drools, scoreHolder, matchWeighter.applyAsInt(a, b, c)));

    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongTriFunction<A, B, C> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c) -> impactScore(drools, scoreHolder, matchWeighter.applyAsLong(a, b, c)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            TriFunction<A, B, C, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c) -> impactScore(drools, scoreHolder, matchWeighter.apply(a, b, c)));
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block5<Drools, ScoreHolder, A, B, C> consequenceImpl) {
        ConsequenceBuilder._4<ScoreHolder, A, B, C> consequence =
                on(scoreHolderGlobal, ruleStructure.getA(), ruleStructure.getB(), ruleStructure.getC())
                        .execute(consequenceImpl);
        return ruleStructure.finish(consequence);
    }

}
