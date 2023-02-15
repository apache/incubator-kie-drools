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

public class RuleUnitRuleType extends AbstractRuleType {

    static Optional<RuleUnitRuleTypeEngine> ruleTypeEngineProvider;

    static {
        ServiceLoader<RuleUnitRuleTypeEngine> providers = ServiceLoader.load(RuleUnitRuleTypeEngine.class);
        ruleTypeEngineProvider = providers.findFirst();
    }

    protected RuleUnitRuleType(String name) {
        super(name);
    }

    @Override
    public void evaluate(RuleSetNodeInstance rsni) {
        ruleTypeEngineProvider.orElseThrow(() -> new IllegalArgumentException("Engine not found for executing RuleUnit rules")).evaluate(rsni);
    }

    @Override
    public boolean isRuleUnit() {
        return true;
    }

    @Override
    public String toString() {
        return "RuleUnitRuleType{" +
                "name='" + name + '\'' +
                '}';
    }
}
