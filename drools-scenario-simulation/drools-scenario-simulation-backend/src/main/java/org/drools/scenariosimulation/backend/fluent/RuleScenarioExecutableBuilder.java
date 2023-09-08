/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.scenariosimulation.backend.fluent;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.kie.api.KieBase;
import org.kie.api.definition.KieDefinition;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.definition.rule.InternalRule;

public interface RuleScenarioExecutableBuilder {

    String COVERAGE_LISTENER = "COVERAGE_LISTENER";
    String RULES_AVAILABLE = "RULES_AVAILABLE";

    static RuleScenarioExecutableBuilder createBuilder(KieContainer kieContainer, String kieSessionName, boolean stateless) {
        if (stateless) {
            return new RuleStatelessScenarioExecutableBuilder(kieContainer, kieSessionName);
        } else {
            return new RuleStatefulScenarioExecutableBuilder(kieContainer, kieSessionName);
        }
    }

    static RuleScenarioExecutableBuilder createBuilder(KieContainer kieContainer) {
        return new RuleStatefulScenarioExecutableBuilder(kieContainer);
    }

    void addInternalCondition(Class<?> clazz,
                              Function<Object, ValueWrapper> checkFunction,
                              ScenarioResult scenarioResult);

    void setActiveAgendaGroup(String agendaGroup);

    void setActiveRuleFlowGroup(String ruleFlowGroup);

    void insert(Object element);

    Map<String, Object> run();

    /**
     * Method to calculate actual number of available rules filtered by active agenda group
     * @param kieBase
     * @param activeAgendaGroup name of the active agenda group. Use <code>null</code> if default one
     * @return
     */
    default Set<String> getAvailableRules(KieBase kieBase, String activeAgendaGroup) {
        Set<String> toReturn = new HashSet<>();
        for (KiePackage kiePackage : kieBase.getKiePackages()) {
            kiePackage.getRules().stream()
                    .filter(rule -> KieDefinition.KnowledgeType.RULE.equals(rule.getKnowledgeType()))
                    .map(rule -> (InternalRule) rule)
                    .forEach(internalRule -> {
                        // main agenda group is always executed after the active one
                        if (internalRule.isMainAgendaGroup() || Objects.equals(activeAgendaGroup, internalRule.getAgendaGroup())) {
                            toReturn.add(prettyFullyQualifiedName(internalRule));
                        }
                    });
        }

        return toReturn;
    }

    static String prettyFullyQualifiedName(Rule rule) {
        String packageName = rule.getPackageName();
        return rule.getName() + (packageName != null && !packageName.isEmpty() ? " (" + packageName + ")" : "");
    }
}
