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
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;

import static org.drools.model.DSL.on;

public final class DroolsQuadCondition<A, B, C, D> extends DroolsCondition<DroolsQuadRuleStructure<A, B, C, D>> {

    public DroolsQuadCondition(DroolsQuadRuleStructure<A, B, C, D> ruleStructure) {
        super(ruleStructure);
    }

    public DroolsQuadCondition<A, B, C, D> andFilter(QuadPredicate<A, B, C, D> predicate) {
        Predicate5<Object, A, B, C, D> filter = (__, a, b, c, d) -> predicate.test(a, b, c, (D) d);
        Variable<A> aVariable = ruleStructure.getA();
        Variable<B> bVariable = ruleStructure.getB();
        Variable<C> cVariable = ruleStructure.getC();
        Variable<D> dVariable = ruleStructure.getD();
        DroolsPatternBuilder<Object> newTargetPattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.expr("Filter using " + predicate, aVariable, bVariable, cVariable, dVariable, filter));
        DroolsQuadRuleStructure<A, B, C, D> newRuleStructure = new DroolsQuadRuleStructure<>(aVariable, bVariable,
                cVariable, dVariable, newTargetPattern, ruleStructure.getSupportingRuleItems(),
                ruleStructure.getVariableIdSupplier());
        return new DroolsQuadCondition<>(newRuleStructure);
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
        ConsequenceBuilder._5<ScoreHolder, A, B, C, D> consequence =
                on(scoreHolderGlobal, ruleStructure.getA(), ruleStructure.getB(), ruleStructure.getC(),
                        ruleStructure.getD())
                        .execute(consequenceImpl);
        return ruleStructure.rebuildSupportingRuleItems(ruleStructure.getPrimaryPattern().build(), consequence);
    }

}
