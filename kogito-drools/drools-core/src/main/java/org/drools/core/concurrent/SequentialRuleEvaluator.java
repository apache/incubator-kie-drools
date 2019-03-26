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

import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.rule.AgendaFilter;

public class SequentialRuleEvaluator extends AbstractRuleEvaluator implements RuleEvaluator {

    private final boolean sequential;

    private final KnowledgeHelper knowledgeHelper;

    public SequentialRuleEvaluator( DefaultAgenda agenda ) {
        super(agenda);
        sequential = agenda.getWorkingMemory().getKnowledgeBase().getConfiguration().isSequential();
        knowledgeHelper = newKnowledgeHelper();
    }

    @Override
    public int evaluateAndFire( AgendaFilter filter,
                                int fireCount,
                                int fireLimit,
                                InternalAgendaGroup group ) {
        RuleAgendaItem item = sequential ? (RuleAgendaItem) group.remove() : (RuleAgendaItem) group.peek();
        return item != null ? internalEvaluateAndFire( filter, fireCount, fireLimit, item ) : 0;
    }

    public KnowledgeHelper getKnowledgeHelper() {
        return knowledgeHelper;
    }
}
