/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.fluent;

import java.util.function.Function;

import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;

public interface RuleScenarioExecutableBuilder {

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
                              Function<Object, ResultWrapper> checkFunction,
                              ScenarioResult scenarioResult);

    void setActiveAgendaGroup(String agendaGroup);

    void setActiveRuleFlowGroup(String ruleFlowGroup);

    void insert(Object element);

    RequestContext run();
}
