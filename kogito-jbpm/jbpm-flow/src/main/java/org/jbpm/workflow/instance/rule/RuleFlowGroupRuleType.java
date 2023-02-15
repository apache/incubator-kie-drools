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

import java.util.Optional;
import java.util.ServiceLoader;

import org.jbpm.workflow.instance.node.RuleSetNodeInstance;

public class RuleFlowGroupRuleType extends AbstractRuleType {

    static Optional<RuleFlowGroupRuleTypeEngine> ruleTypeEngineProvider;

    static {
        ServiceLoader<RuleFlowGroupRuleTypeEngine> providers = ServiceLoader.load(RuleFlowGroupRuleTypeEngine.class);
        ruleTypeEngineProvider = providers.findFirst();
    }

    protected RuleFlowGroupRuleType(String name) {
        super(name);
    }

    @Override
    public boolean isRuleFlowGroup() {
        return true;
    }

    @Override
    public void evaluate(RuleSetNodeInstance rsni) {
        ruleTypeEngineProvider.orElseThrow(() -> new IllegalArgumentException("Engine not found for executing RuleFlow rules")).evaluate(rsni, getName());
    }

    @Override
    public String toString() {
        return "RuleFlowGroupRuleType{" +
                "name='" + name + '\'' +
                '}';
    }

}
