/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.holder;

import java.util.Collections;
import java.util.List;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.AgendaItemImpl;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.RuleRuntime;

import static org.mockito.Mockito.*;

public abstract class AbstractScoreHolderTest {

    protected RuleContext mockRuleContext(String ruleName) {
        RuleContext kcontext = mock(RuleContext.class);
        AgendaItem agendaItem = new AgendaItemImpl() {
            @Override
            public List<Object> getObjects() {
                return Collections.emptyList();
            }
        };
        when(kcontext.getMatch()).thenReturn(agendaItem);
        Rule rule = mock(Rule.class);
        when(rule.getPackageName()).thenReturn(getClass().getPackage().getName());
        when(rule.getName()).thenReturn(ruleName);
        when(kcontext.getRule()).thenReturn(rule);
        return kcontext;
    }

    protected void callUnMatch(RuleContext ruleContext) {
        AgendaItem agendaItem = (AgendaItem) ruleContext.getMatch();
        agendaItem.getActivationUnMatchListener().unMatch(mock(RuleRuntime.class), agendaItem);
    }

}
