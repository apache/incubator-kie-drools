/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.impl;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;

public abstract class ReteEvaluatorBasedRuleUnitInstance<T extends RuleUnitData> extends AbstractRuleUnitInstance<ReteEvaluator, T> {

    protected ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator) {
        super(unit, unitMemory, evaluator);
    }

    protected ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator, RuleConfig ruleConfig) {
        super(unit, unitMemory, evaluator, ruleConfig);
    }

    @Override
    protected void addEventListeners() {
        ruleConfig.getAgendaEventListeners().stream().forEach(l -> evaluator.getAgendaEventSupport().addEventListener(l));
        ruleConfig.getRuleRuntimeListeners().stream().forEach(l -> evaluator.getRuleRuntimeEventSupport().addEventListener(l));
        ruleConfig.getRuleEventListeners().stream().forEach(l -> evaluator.getRuleEventSupport().addEventListener(l));
    }

    @Override
    public int fire() {
        return evaluator.fireAllRules();
    }

    @Override
    public int fire(AgendaFilter agendaFilter) {
        return evaluator.fireAllRules(agendaFilter);
    }

    @Override
    public void close() {
        evaluator.dispose();
    }

    @Override
    public QueryResults executeQuery(String query, Object... arguments) {
        fire();
        return evaluator.getQueryResults(query, arguments);
    }

    @Override
    public <C extends SessionClock> C getClock() {
        return (C) evaluator.getSessionClock();
    }
}
