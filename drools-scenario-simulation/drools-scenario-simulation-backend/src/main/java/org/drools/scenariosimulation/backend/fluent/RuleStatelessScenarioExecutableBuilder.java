/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

public class RuleStatelessScenarioExecutableBuilder implements RuleScenarioExecutableBuilder {

    private final KieContainer kieContainer;
    private final String sessionName;
    private final List<Object> inputs = new ArrayList<>();
    private final Map<FactIdentifier, List<FactCheckerHandle>> internalConditions = new HashMap<>();
    private final KieCommands commands = KieServices.get().getCommands();

    private String agendaGroup;

    protected RuleStatelessScenarioExecutableBuilder(KieContainer kieContainer, String sessionName) {
        this.kieContainer = kieContainer;
        this.sessionName = sessionName;
    }

    @Override
    public void addInternalCondition(Class<?> clazz,
                                     Function<Object, ResultWrapper> checkFunction,
                                     ScenarioResult scenarioResult) {
        internalConditions.computeIfAbsent(scenarioResult.getFactIdentifier(), key -> new ArrayList<>())
                .add(new FactCheckerHandle(clazz, checkFunction, scenarioResult));
    }

    @Override
    public void setActiveAgendaGroup(String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    @Override
    public void setActiveRuleFlowGroup(String ruleFlowGroup) {
        this.agendaGroup = ruleFlowGroup;
    }

    @Override
    public void insert(Object element) {
        inputs.add(element);
    }

    @Override
    public RequestContext run() {
        KieSessionModel kieSessionModel = kieContainer.getKieSessionModel(sessionName);
        if (kieSessionModel == null) {
            throw new ScenarioException("Impossible to find a KieSession with name " + sessionName);
        }
        kieSessionModel.setClockType(ClockTypeOption.get("pseudo"));

        StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession(sessionName);

        statelessKieSession.execute(generateCommands());

        return null;
    }

    protected Command<ExecutionResults> generateCommands() {

        List<Command<?>> toReturn = new ArrayList<>();
        if (agendaGroup != null) {
            toReturn.add(commands.newAgendaGroupSetFocus(agendaGroup));
        }

        if (!inputs.isEmpty()) {
            toReturn.add(commands.newInsertElements(inputs));
        }
        toReturn.add(commands.newFireAllRules());
        internalConditions.values()
                .forEach(factToCheck -> toReturn.add(new ValidateFactCommand(factToCheck)));

        return commands.newBatchExecution(toReturn);
    }
}
