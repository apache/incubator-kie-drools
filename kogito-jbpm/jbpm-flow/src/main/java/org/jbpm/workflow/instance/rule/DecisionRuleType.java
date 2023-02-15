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

public class DecisionRuleType extends AbstractRuleType {

    static Optional<DecisionRuleTypeEngine> ruleTypeEngineProvider;

    static {
        ServiceLoader<DecisionRuleTypeEngine> providers = ServiceLoader.load(DecisionRuleTypeEngine.class);
        ruleTypeEngineProvider = providers.findFirst();
    }

    private String namespace;
    private String decision;

    protected DecisionRuleType(String namespace, String model, String decision) {
        super(model);
        this.namespace = namespace;
        this.decision = decision;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getModel() {
        return getName();
    }

    public String getDecision() {
        return decision;
    }

    @Override
    public boolean isDecision() {
        return true;
    }

    @Override
    public String toString() {
        return "DecisionRuleType{" +
                "namespace='" + namespace + '\'' +
                ", decision='" + decision + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public void evaluate(RuleSetNodeInstance rsni) {
        ruleTypeEngineProvider.orElseThrow(() -> new IllegalArgumentException("Engine not found for executing DMN rules")).evaluate(rsni, getNamespace(), getModel(), getDecision());
    }
}
