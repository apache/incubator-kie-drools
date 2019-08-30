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

import java.util.function.Supplier;

import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.RuleUnitFactory;
import org.kie.api.runtime.KieRuntime;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.rules.RuleUnit;

/**
 *
 */
public class RuleSetNodeFactory extends NodeFactory {

    public RuleSetNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new RuleSetNode();
    }
    
    protected RuleSetNode getRuleSetNode() {
    	return (RuleSetNode) getNode();
    }

    public RuleSetNodeFactory name(String name) {
        getNode().setName(name);
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
    
    public RuleSetNodeFactory dmnGroup(String namespace, String model, String decision, Supplier<DMNRuntime> supplier) {
        getRuleSetNode().setRuleType(RuleSetNode.RuleType.decision(namespace, model, decision));
        getRuleSetNode().setLanguage(RuleSetNode.DMN_LANG);
        getRuleSetNode().setDmnRuntime(supplier);
        return this;
    }
    
    public RuleSetNodeFactory timer(String delay, String period, String dialect, String action) {
    	Timer timer = new Timer();
    	timer.setDelay(delay);
    	timer.setPeriod(period);
    	getRuleSetNode().addTimer(timer, new DroolsConsequenceAction(dialect, action));
    	return this;
    }
    
    public RuleSetNodeFactory inMapping(String parameterName, String variableName) {
        getRuleSetNode().addInMapping(parameterName, variableName);
        return this;
    }

    public RuleSetNodeFactory outMapping(String parameterName, String variableName) {
        getRuleSetNode().addOutMapping(parameterName, variableName);
        return this;
    }

}
