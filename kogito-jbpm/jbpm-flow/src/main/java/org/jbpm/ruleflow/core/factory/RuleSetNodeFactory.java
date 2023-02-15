/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.core.factory;

import java.util.function.Supplier;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.RuleUnitFactory;
import org.jbpm.workflow.instance.rule.RuleType;
import org.kie.api.runtime.KieRuntime;
import org.kie.kogito.decision.DecisionModel;

import static org.jbpm.workflow.instance.rule.RuleType.DMN_LANG;
import static org.jbpm.workflow.instance.rule.RuleType.DRL_LANG;
import static org.jbpm.workflow.instance.rule.RuleType.RULE_UNIT_LANG;

public class RuleSetNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends StateBasedNodeFactory<RuleSetNodeFactory<T>, T> implements MappableNodeFactory<RuleSetNodeFactory<T>> {

    public static final String METHOD_DECISION = "decision";
    public static final String METHOD_PARAMETER = "parameter";

    public RuleSetNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new RuleSetNode(), id);
    }

    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    @Override
    public Mappable getMappableNode() {
        return getRuleSetNode();
    }

    public RuleSetNodeFactory<T> ruleUnit(String unit, RuleUnitFactory<?> ruleUnit) {
        getRuleSetNode().setRuleType(RuleType.ruleUnit(unit));
        getRuleSetNode().setLanguage(RULE_UNIT_LANG);
        getRuleSetNode().setRuleUnitFactory(ruleUnit);
        return this;
    }

    public RuleSetNodeFactory<T> ruleFlowGroup(String ruleFlowGroup, Supplier<KieRuntime> supplier) {
        getRuleSetNode().setRuleType(RuleType.ruleFlowGroup(ruleFlowGroup));
        getRuleSetNode().setLanguage(DRL_LANG);
        getRuleSetNode().setKieRuntime(supplier);
        return this;
    }

    public RuleSetNodeFactory<T> decision(String namespace, String model, String decision, Supplier<DecisionModel> supplier) {
        getRuleSetNode().setRuleType(RuleType.decision(namespace, model, decision));
        getRuleSetNode().setLanguage(DMN_LANG);
        getRuleSetNode().setDecisionModel(supplier);
        return this;
    }

    public RuleSetNodeFactory<T> parameter(String name, Object value) {
        getRuleSetNode().setParameter(name, value);
        return this;
    }
}
