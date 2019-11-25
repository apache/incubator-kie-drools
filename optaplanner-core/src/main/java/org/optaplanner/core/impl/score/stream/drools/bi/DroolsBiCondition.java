/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block4;
import org.drools.model.functions.Predicate3;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

import static org.drools.model.DSL.on;

public final class DroolsBiCondition<A, B> extends DroolsCondition<DroolsBiRuleStructure<A, B>> {

    public DroolsBiCondition(DroolsBiRuleStructure<A, B> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsBiCondition<A, B> andFilter(BiPredicate<A, B> predicate) {
        Predicate3<Object, A, B> filter = (__, a, b) -> predicate.test(a, b);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        DroolsPatternBuilder<Object> newTargetPattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, filter));
        DroolsBiRuleStructure<A, B> newRuleStructure = new DroolsBiRuleStructure<>(aVariable, bVariable,
                newTargetPattern, ruleStructure.getSupportingRuleItems(), ruleStructure.getVariableIdSupplier());
        return new DroolsBiCondition<>(newRuleStructure);
    }

    public <C> DroolsTriCondition<A, B, C> andJoin(DroolsUniCondition<C> cCondition,
            AbstractTriJoiner<A, B, C> triJoiner) {
        DroolsUniRuleStructure<C> cRuleStructure = cCondition.getRuleStructure();
        Variable<C> cVariable = cRuleStructure.getA();
        DroolsPatternBuilder<Object> cPattern = cRuleStructure.getPrimaryPattern()
                .expand(p -> p.expr("Filter using " + triJoiner, ruleStructure.getA(), ruleStructure.getB(),
                        cVariable, (__, a, b, c) -> matches(triJoiner, a, b, c)));
        DroolsUniRuleStructure<C> newCRuleStructure = new DroolsUniRuleStructure<>(cVariable, cPattern,
                cRuleStructure.getSupportingRuleItems(), ruleStructure.getVariableIdSupplier());
        return new DroolsTriCondition<>(new DroolsTriRuleStructure<>(ruleStructure, newCRuleStructure,
                ruleStructure.getVariableIdSupplier()));
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
        return ruleStructure.rebuildSupportingRuleItems(ruleStructure.getPrimaryPattern().build(), consequence);
    }

    private static <A, B, C> boolean matches(AbstractTriJoiner<A, B, C> triJoiner, A a, B b, C c) {
        JoinerType[] joinerTypes = triJoiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            Object leftMapping = triJoiner.getLeftMapping(i).apply(a, b);
            Object rightMapping = triJoiner.getRightMapping(i).apply(c);
            if (!joinerType.matches(leftMapping, rightMapping)) {
                return false;
            }
        }
        return true;
    }

}
