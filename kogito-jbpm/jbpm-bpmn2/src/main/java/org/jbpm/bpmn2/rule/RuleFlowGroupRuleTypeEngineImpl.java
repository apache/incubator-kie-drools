/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.rule;

import java.util.Map;

import org.drools.core.common.InternalAgenda;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.RuleSetNodeInstance;
import org.jbpm.workflow.instance.rule.RuleFlowGroupRuleTypeEngine;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;

public class RuleFlowGroupRuleTypeEngineImpl implements RuleFlowGroupRuleTypeEngine {

    private static final String FIRE_RULE_LIMIT_PARAMETER = "FireRuleLimit";
    private static final String FIRE_RULE_LIMIT_PROPERTY = "org.jbpm.rule.task.firelimit";
    private static final int DEFAULT_FIRE_RULE_LIMIT = Integer.parseInt(System.getProperty(FIRE_RULE_LIMIT_PROPERTY, "10000"));

    public void evaluate(RuleSetNodeInstance rsni, String ruleFlowGroup) {
        rsni.setRuleFlowGroup(ruleFlowGroup);

        KieRuntime kruntime = getKieRuntime(rsni);
        Map<String, Object> inputs = getInputs(rsni);
        //proceed
        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            if (FIRE_RULE_LIMIT_PARAMETER.equals(entry.getKey())) {
                // don't put control parameter for fire limit into working memory
                continue;
            }

            String inputKey = rsni.getRuleFlowGroup() + "_" + rsni.getProcessInstance().getStringId() + "_" + entry.getKey();

            rsni.addFact(inputKey, kruntime.insert(entry.getValue()));
        }

        if (rsni.actAsWaitState()) {
            rsni.addRuleSetListener();
            ((InternalAgenda) kruntime.getAgenda())
                    .activateRuleFlowGroup(rsni.getRuleFlowGroup(), rsni.getProcessInstance().getStringId(), rsni.getUniqueId());
        } else {
            int fireLimit = DEFAULT_FIRE_RULE_LIMIT;
            WorkflowProcessInstance processInstance = rsni.getProcessInstance();

            if (inputs.containsKey(FIRE_RULE_LIMIT_PARAMETER)) {
                fireLimit = Integer.parseInt(inputs.get(FIRE_RULE_LIMIT_PARAMETER).toString());
            }
            ((InternalAgenda) kruntime.getAgenda())
                    .activateRuleFlowGroup(rsni.getRuleFlowGroup(), processInstance.getStringId(), rsni.getUniqueId());

            int fired = ((KieSession) kruntime).fireAllRules(processInstance.getAgendaFilter(), fireLimit);
            if (fired == fireLimit) {
                throw new RuntimeException("Fire rule limit reached " + fireLimit + ", limit can be set via system property " + FIRE_RULE_LIMIT_PROPERTY
                        + " or via data input of business rule task named " + FIRE_RULE_LIMIT_PARAMETER);
            }

            rsni.removeEventListeners();
            rsni.retractFacts(kruntime);
            rsni.triggerCompleted();
        }
    }

}
