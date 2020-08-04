/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

public interface KogitoInternalAgenda
    extends
    InternalAgenda {

    boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName, String ruleName, String processInstanceId);

    /**
     * Activates the <code>RuleFlowGroup</code> with the given name.
     * All activations in the given <code>RuleFlowGroup</code> are added to the agenda.
     * As long as the <code>RuleFlowGroup</code> remains active,
     * its activations are automatically added to the agenda.
     * The given processInstanceId and nodeInstanceId define the process context
     * in which this <code>RuleFlowGroup</code> is used.
     */
    void activateRuleFlowGroup(String name, String processInstanceId, String nodeInstanceId);

    /**
     * Returns true if there is at least one activation of the given rule name
     * in the given ruleflow group name
     */
    boolean isRuleInstanceAgendaItem(String ruleflowGroupName,
                                     String ruleName,
                                     String processInstanceId);
}
