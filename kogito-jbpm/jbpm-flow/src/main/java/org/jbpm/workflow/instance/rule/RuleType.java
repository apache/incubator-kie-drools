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

package org.jbpm.workflow.instance.rule;

import org.jbpm.workflow.instance.node.RuleSetNodeInstance;

public interface RuleType {

    String UNIT_RULEFLOW_PREFIX = "unit:";
    String DRL_LANG = "http://www.jboss.org/drools/rule";
    String RULE_UNIT_LANG = "http://www.jboss.org/drools/rule-unit";
    String DMN_LANG = "http://www.jboss.org/drools/dmn";

    String getName();

    void evaluate(RuleSetNodeInstance rsni);

    boolean isRuleFlowGroup();

    boolean isRuleUnit();

    boolean isDecision();

    static RuleType of(String name, String language) {
        if (DRL_LANG.equals(language)) {
            return parseRuleFlowGroup(name);
        } else if (RULE_UNIT_LANG.equals(language)) {
            return ruleUnit(name);
        } else {
            throw new IllegalArgumentException("Unsupported language " + language);
        }
    }

    private static RuleType parseRuleFlowGroup(String name) {
        if (name.startsWith(UNIT_RULEFLOW_PREFIX)) {
            String unitId = name.substring(UNIT_RULEFLOW_PREFIX.length());
            return ruleUnit(unitId);
        }
        return ruleFlowGroup(name);
    }

    static RuleFlowGroupRuleType ruleFlowGroup(String name) {
        return new RuleFlowGroupRuleType(name);
    }

    static RuleUnitRuleType ruleUnit(String name) {
        return new RuleUnitRuleType(name);
    }

    static DecisionRuleType decision(String namespace, String model, String decision) {
        return new DecisionRuleType(namespace, model, decision);
    }

}
