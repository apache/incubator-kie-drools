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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.math.BigDecimal;

import org.drools.model.Drools;
import org.drools.model.PatternDSL;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * Encapsulates the low-level rule creation and manipulation operations via the Drools executable model DSL
 * (see {@link PatternDSL}.
 *
 * @param <T> type of Drools rule that we operate on
 */
public abstract class DroolsCondition<T extends DroolsRuleStructure> {

    protected final T ruleStructure;

    protected DroolsCondition(T ruleStructure) {
        this.ruleStructure = ruleStructure;
    }

    public T getRuleStructure() {
        return ruleStructure;
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(Drools drools, H scoreHolder) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(Drools drools, H scoreHolder,
            int impact) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext, impact);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(Drools drools, H scoreHolder,
            long impact) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext, impact);
    }

    protected <S extends Score<S>, H extends AbstractScoreHolder<S>> void impactScore(Drools drools, H scoreHolder,
            BigDecimal impact) {
        RuleContext kcontext = (RuleContext) drools;
        scoreHolder.impactScore(kcontext, impact);
    }

}
