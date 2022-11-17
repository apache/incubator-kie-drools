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

import java.util.EventListener;
import java.util.List;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;
import org.kie.internal.event.rule.RuleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReteEvaluatorBasedRuleUnitInstance<T extends RuleUnitData> extends AbstractRuleUnitInstance<ReteEvaluator, T> {

    private static final Logger LOG = LoggerFactory.getLogger(ReteEvaluatorBasedRuleUnitInstance.class);

    public ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator) {
        super(unit, unitMemory, evaluator);
    }

    public ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator, List<EventListener> eventListenerList) {
        super(unit, unitMemory, evaluator, eventListenerList);
    }

    @Override
    protected void addEventListeners() {
        for (EventListener eventListener : eventListenerList) {
            if (eventListener instanceof AgendaEventListener) {
                evaluator.getAgendaEventSupport().addEventListener((AgendaEventListener)eventListener);
            } else if (eventListener instanceof RuleRuntimeEventListener) {
                evaluator.getRuleRuntimeEventSupport().addEventListener((RuleRuntimeEventListener)eventListener);
            } else if (eventListener instanceof RuleEventListener) {
                evaluator.getRuleEventSupport().addEventListener((RuleEventListener)eventListener);
            } else {
                LOG.warn("{} is not supported EventListener. Ignored.", eventListener.getClass());
            }
        }
    }

    @Override
    public int fire() {
        return evaluator.fireAllRules();
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
