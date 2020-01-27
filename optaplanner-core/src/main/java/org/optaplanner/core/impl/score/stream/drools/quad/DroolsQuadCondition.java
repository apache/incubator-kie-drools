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

import java.math.BigDecimal;
import java.util.List;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block6;
import org.drools.model.functions.Predicate5;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;

import static org.drools.model.DSL.on;

public final class DroolsQuadCondition<A, B, C, D, PatternVar> extends
        DroolsCondition<PatternVar, DroolsQuadRuleStructure<A, B, C, D, PatternVar>> {

    public DroolsQuadCondition(DroolsQuadRuleStructure<A, B, C, D, PatternVar> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsQuadCondition<A, B, C, D, PatternVar> andFilter(QuadPredicate<A, B, C, D> predicate) {
        Predicate5<PatternVar, A, B, C, D> filter = (__, a, b, c, d) -> predicate.test(a, b, c, d);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        Variable<C> cVariable = ruleStructure.getC();
        Variable<D> dVariable = ruleStructure.getD();
        DroolsPatternBuilder<PatternVar> newTargetPattern = ruleStructure.getPrimaryPatternBuilder()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, cVariable, dVariable, filter));
        DroolsQuadRuleStructure<A, B, C, D, PatternVar> newRuleStructure = new DroolsQuadRuleStructure<>(aVariable,
                bVariable, cVariable, dVariable, newTargetPattern, ruleStructure.getShelvedRuleItems(),
                ruleStructure.getPrerequisites(), ruleStructure.getDependents(), ruleStructure.getVariableIdSupplier());
        return new DroolsQuadCondition<>(newRuleStructure);
    }

    public <NewA, __> DroolsUniCondition<NewA, NewA> andCollect(
            QuadConstraintCollector<A, B, C, D, __, NewA> collector) {
        DroolsQuadAccumulateFunctionBridge<A, B, C, D, __, NewA> bridge =
                new DroolsQuadAccumulateFunctionBridge<>(collector);
        return collect(bridge, (pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(),
                ruleStructure.getB(), ruleStructure.getC(),
                (d, a, b, c) -> new QuadTuple<>(a, b, c, (D) d)));
    }

    public <NewA> DroolsUniCondition<NewA, NewA> andGroup(QuadFunction<A, B, C, D, NewA> groupKeyMapping) {
        return super.group((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                ruleStructure.getC(), (d, a, b, c) -> groupKeyMapping.apply(a, b, c, (D) d)));
    }

    public <NewA, NewB, __> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupWithCollect(
            QuadFunction<A, B, C, D, NewA> groupKeyMapping, QuadConstraintCollector<A, B, C, D, __, NewB> collector) {
        return groupWithCollect(() -> new DroolsQuadToBiGroupByInvoker<>(groupKeyMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupBi(
            QuadFunction<A, B, C, D, NewA> groupKeyAMapping, QuadFunction<A, B, C, D, NewB> groupKeyBMapping) {
        return groupBi((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                ruleStructure.getC(), (d, a, b, c) -> {
                    final NewA newA = groupKeyAMapping.apply(a, b, c, (D) d);
                    final NewB newB = groupKeyBMapping.apply(a, b, c, (D) d);
                    return new BiTuple<>(newA, newB);
                }));
    }

    public <NewA, NewB, NewC, __> DroolsTriCondition<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>>
    andGroupBiWithCollect(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, __, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsQuadToTriGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collector, getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
    andGroupBiWithCollectBi(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping, QuadConstraintCollector<A, B, C, D, ?, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ?, NewD> collectorD) {
        return groupBiWithCollectBi(() -> new DroolsQuadGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD, getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC(),
                getRuleStructure().getD()));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntQuadFunction<A, B, C, D> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder,
                        matchWeighter.applyAsInt(a, b, c, d)));

    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongQuadFunction<A, B, C, D> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder,
                        matchWeighter.applyAsLong(a, b, c, d)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            QuadFunction<A, B, C, D, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a, b, c, d) -> impactScore(drools, scoreHolder, matchWeighter.apply(a, b, c, d)));
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block6<Drools, ScoreHolder, A, B, C, D> consequenceImpl) {
        ConsequenceBuilder._5<ScoreHolder, A, B, C, D> consequence = on(scoreHolderGlobal, ruleStructure.getA(),
                ruleStructure.getB(), ruleStructure.getC(), ruleStructure.getD())
                .execute(consequenceImpl);
        return ruleStructure.finish(consequence);
    }

}
