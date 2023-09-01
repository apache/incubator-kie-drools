/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.scenariosimulation.backend.fluent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.internal.definition.rule.InternalRule;

import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.prettyFullyQualifiedName;

public class CoverageAgendaListener extends DefaultAgendaEventListener {

    protected Map<String, Integer> ruleExecuted = new HashMap<>();
    protected List<String> auditsMessages = new ArrayList<>();

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {
        InternalRule rule = (InternalRule) beforeMatchFiredEvent.getMatch().getRule();
        String ruleKey = prettyFullyQualifiedName(rule);
        ruleExecuted.compute(ruleKey, (r, counter) -> counter == null ? 1 : counter + 1);
        auditsMessages.add(ruleKey);
    }

    public Map<String, Integer> getRuleExecuted() {
        return Collections.unmodifiableMap(ruleExecuted);
    }

    public List<String> getAuditsMessages() {
        return Collections.unmodifiableList(auditsMessages);
    }
}
