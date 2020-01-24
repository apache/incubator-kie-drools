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
import java.util.List;
import java.util.function.UnaryOperator;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Predicate4;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadCondition;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;

import static org.drools.model.DSL.on;

public final class DroolsTriCondition<A, B, C> extends DroolsCondition<DroolsTriRuleStructure<A, B, C>> {

    public DroolsTriCondition(DroolsTriRuleStructure<A, B, C> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsTriCondition<A, B, C> andFilter(TriPredicate<A, B, C> predicate) {
        Predicate4<Object, A, B, C> filter = (__, a, b, c) -> predicate.test(a, b, c);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        Variable<C> cVariable = ruleStructure.getC();
        DroolsPatternBuilder<Object> newTargetPattern = ruleStructure.getPrimaryPatternBuilder()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, cVariable, filter));
        DroolsTriRuleStructure<A, B, C> newRuleStructure = new DroolsTriRuleStructure<>(aVariable, bVariable, cVariable,
                newTargetPattern, ruleStructure.getShelvedRuleItems(), ruleStructure.getPrerequisites(),
                ruleStructure.getDependents(), ruleStructure.getVariableIdSupplier());
        return new DroolsTriCondition<>(newRuleStructure);
    }

    public <D> DroolsQuadCondition<A, B, C, D> andJoin(DroolsUniCondition<D> dCondition,
            AbstractQuadJoiner<A, B, C, D> quadJoiner) {
        DroolsUniRuleStructure<D> dRuleStructure = dCondition.getRuleStructure();
        Variable<D> dVariable = dRuleStructure.getA();
        UnaryOperator<PatternDSL.PatternDef<Object>> expander =
                p -> p.expr("Filter using " + quadJoiner, ruleStructure.getA(), ruleStructure.getB(),
                        ruleStructure.getC(), dVariable, (__, a, b, c, d) -> matches(quadJoiner, a, b, c, d));
        DroolsUniRuleStructure<D> newDRuleStructure = dRuleStructure.amend(expander);
        return new DroolsQuadCondition<>(new DroolsQuadRuleStructure<>(ruleStructure, newDRuleStructure,
                ruleStructure.getVariableIdSupplier()));
    }

    public <NewA, __> DroolsUniCondition<NewA> andCollect(TriConstraintCollector<A, B, C, __, NewA> collector) {
        DroolsTriAccumulateFunctionBridge<A, B, C, __, NewA> bridge =
                new DroolsTriAccumulateFunctionBridge<>(collector);
        return collect(bridge, (pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(),
                ruleStructure.getB(), (c, a, b) -> new TriTuple<>(a, b, (C) c)));
    }

    public <NewA> DroolsUniCondition<NewA> andGroup(TriFunction<A, B, C, NewA> groupKeyMapping) {
        return super.group((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                (c, a, b) -> groupKeyMapping.apply(a, b, (C) c)));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB> andGroupWithCollect(TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ?, NewB> collector) {
        return groupWithCollect(() -> new DroolsTriToBiGroupByInvoker<>(groupKeyMapping, collector, getRuleStructure().getA(),
                        getRuleStructure().getB(), getRuleStructure().getC()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB> andGroupBi(TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping) {
        return groupBi((pattern, tuple) -> pattern.bind(tuple, ruleStructure.getA(), ruleStructure.getB(),
                (c, a, b) -> {
                    final NewA newA = groupKeyAMapping.apply(a, b, (C) c);
                    final NewB newB = groupKeyBMapping.apply(a, b, (C) c);
                    return new BiTuple<>(newA, newB);
                }));
    }

    public <NewA, NewB, NewC> DroolsTriCondition<NewA, NewB, NewC> andGroupBiWithCollect(
            TriFunction<A, B, C, NewA> groupKeyAMapping, TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ?, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsTriGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping, collector,
                getRuleStructure().getA(), getRuleStructure().getB(), getRuleStructure().getC()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD> andGroupBiWithCollectBi(
            TriFunction<A, B, C, NewA> groupKeyAMapping, TriFunction<A, B, C, NewB> groupKeyBMapping,
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

    private static <A, B, C, D> boolean matches(AbstractQuadJoiner<A, B, C, D> joiner, A a, B b, C c, D d) {
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            Object leftMapping = joiner.getLeftMapping(i).apply(a, b, c);
            Object rightMapping = joiner.getRightMapping(i).apply(d);
            if (!joinerType.matches(leftMapping, rightMapping)) {
                return false;
            }
        }
        return true;
    }

}
