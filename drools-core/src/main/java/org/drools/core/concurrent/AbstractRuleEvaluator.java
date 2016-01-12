/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.concurrent;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.rule.AgendaFilter;

public class AbstractRuleEvaluator {
    private final DefaultAgenda agenda;

    public AbstractRuleEvaluator( DefaultAgenda agenda ) {
        this.agenda = agenda;
    }

    protected int internalEvaluateAndFire( KnowledgeHelper knowledgeHelper, AgendaFilter filter, int fireCount, int fireLimit, RuleAgendaItem item ) {
        agenda.evaluateQueriesForRule( item );
        RuleExecutor ruleExecutor = item.getRuleExecutor();
        ruleExecutor.setKnowledgeHelper(knowledgeHelper);
        return ruleExecutor.evaluateNetworkAndFire(agenda, filter, fireCount, fireLimit);
    }

    protected KnowledgeHelper newKnowledgeHelper() {
        RuleBaseConfiguration rbc = agenda.getWorkingMemory().getKnowledgeBase().getConfiguration();
        return rbc.getComponentFactory().getKnowledgeHelperFactory().newStatefulKnowledgeHelper( agenda.getWorkingMemory() );
    }
}
