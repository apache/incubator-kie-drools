/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.drools.core.common;

import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.impl.RuleBase;
import org.drools.kiesession.agenda.DefaultAgenda;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class KogitoDefaultAgenda extends DefaultAgenda implements KogitoInternalAgenda {

    public KogitoDefaultAgenda(RuleBase kBase) {
        super(kBase);
    }

    public KogitoDefaultAgenda(RuleBase kBase, boolean initMain) {
        super(kBase, initMain);
    }

    @Override
    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, String processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, processInstanceId);
    }

    @Override
    public void activateRuleFlowGroup(String name, String processInstanceId, String nodeInstanceId) {
        InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) getRuleFlowGroup(name);
        activateRuleFlowGroup(ruleFlowGroup, processInstanceId, nodeInstanceId);
    }

    @Override
    public boolean isRuleInstanceAgendaItem(String ruleflowGroupName, String ruleName, String processInstanceId) {
        return isRuleInstanceAgendaItem(ruleflowGroupName, ruleName, (Object) processInstanceId);
    }

    @Override
    protected boolean sameProcessInstance(Object processInstanceId, ProcessInstance value) {
        return processInstanceId.equals(((KogitoProcessInstance) value).getStringId());
    }
}
