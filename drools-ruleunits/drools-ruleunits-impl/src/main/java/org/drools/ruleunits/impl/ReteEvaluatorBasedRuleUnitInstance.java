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

import java.util.List;
import java.util.Map;

import org.drools.core.common.ReteEvaluator;
import org.kie.api.time.SessionClock;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;

public abstract class ReteEvaluatorBasedRuleUnitInstance<T extends RuleUnitData> extends AbstractRuleUnitInstance<ReteEvaluator, T> {

    public ReteEvaluatorBasedRuleUnitInstance(RuleUnit<T> unit, T unitMemory, ReteEvaluator evaluator) {
        super(unit, unitMemory, evaluator);
    }

    @Override
    public int fire() {
        return evaluator.fireAllRules();
    }

    @Override
    public void dispose() {
        evaluator.dispose();
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query, Object... arguments) {
        fire();
        return evaluator.getQueryResults(query, arguments).toList();
    }

    @Override
    public <C extends SessionClock> C getClock() {
        return (C) evaluator.getSessionClock();
    }
}
