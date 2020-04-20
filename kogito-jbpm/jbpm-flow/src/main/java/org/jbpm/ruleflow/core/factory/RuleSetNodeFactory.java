/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.RuleUnitFactory;
import org.kie.api.runtime.KieRuntime;
import org.kie.kogito.decision.DecisionModel;

import java.util.function.Supplier;

public class RuleSetNodeFactory extends StateBasedNodeFactory implements MappableNodeFactory {

    public static final String METHOD_DECISION = "decision";

    public RuleSetNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new RuleSetNode();
    }

    protected RuleSetNode getRuleSetNode() {
        return (RuleSetNode) getNode();
    }

    @Override
    public RuleSetNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public RuleSetNodeFactory timer(String delay, String period, String dialect, String action) {
        super.timer(delay, period, dialect, action);
        return this;
    }

    @Override
    public Mappable getMappableNode() {
        return getRuleSetNode();
    }

    @Override
    public RuleSetNodeFactory inMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.inMapping(parameterName, variableName);
        return this;
    }

    @Override
    public RuleSetNodeFactory outMapping(String parameterName, String variableName) {
        MappableNodeFactory.super.outMapping(parameterName, variableName);
        return this;
    }

    public RuleSetNodeFactory ruleUnit(String unit, RuleUnitFactory<?> ruleUnit) {
        getRuleSetNode().setRuleType(RuleSetNode.RuleType.ruleUnit(unit));
        getRuleSetNode().setLanguage(RuleSetNode.RULE_UNIT_LANG);
        getRuleSetNode().setRuleUnitFactory(ruleUnit);
        return this;
    }

    public RuleSetNodeFactory ruleFlowGroup(String ruleFlowGroup, Supplier<KieRuntime> supplier) {
        getRuleSetNode().setRuleType(RuleSetNode.RuleType.ruleFlowGroup(ruleFlowGroup));
        getRuleSetNode().setLanguage(RuleSetNode.DRL_LANG);
        getRuleSetNode().setKieRuntime(supplier);
        return this;
    }

    public RuleSetNodeFactory decision(String namespace, String model, String decision, Supplier<DecisionModel> supplier) {
        getRuleSetNode().setRuleType(RuleSetNode.RuleType.decision(namespace, model, decision));
        getRuleSetNode().setLanguage(RuleSetNode.DMN_LANG);
        getRuleSetNode().setDecisionModel(supplier);
        return this;
    }
}
