/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.event.AgendaEventSupport;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;

public interface ActivationsManager {
    ReteEvaluator getReteEvaluator();

    AgendaGroupsManager getAgendaGroupsManager();

    AgendaEventSupport getAgendaEventSupport();

    ActivationsFilter getActivationsFilter();

    void addEagerRuleAgendaItem(RuleAgendaItem item);
    void removeEagerRuleAgendaItem(RuleAgendaItem item);

    void addQueryAgendaItem(final RuleAgendaItem item);
    void removeQueryAgendaItem(final RuleAgendaItem item);

    void registerExpiration(PropagationContext expirationContext);

    void clearAndCancelActivationGroup(InternalActivationGroup activationGroup);

    RuleAgendaItem createRuleAgendaItem(int salience, PathMemory pathMemory, TerminalNode rtn);

    AgendaItem createAgendaItem(RuleTerminalNodeLeftTuple rtnLeftTuple,
                                int salience,
                                PropagationContext context,
                                RuleAgendaItem ruleAgendaItem,
                                InternalAgendaGroup agendaGroup);

    void cancelActivation(final Activation activation);

    void modifyActivation(final AgendaItem activation, boolean previouslyActive);

    void insertAndStageActivation(AgendaItem activation);
    void addItemToActivationGroup(AgendaItem activation);

    RuleAgendaItem peekNextRule();

    void flushPropagations();

    boolean isFiring();

    void evaluateEagerList();
    void evaluateQueriesForRule(RuleAgendaItem item);

    KnowledgeHelper getKnowledgeHelper();

    default void handleException(Activation activation, Exception e) {
        throw new RuntimeException(e);
    }

    void executeTask(ExecutableEntry executableEntry);
}
