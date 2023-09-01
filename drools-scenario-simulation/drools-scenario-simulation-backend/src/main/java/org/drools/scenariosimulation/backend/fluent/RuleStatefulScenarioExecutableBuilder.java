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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.backend.runner.ScenarioException;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieSessionFluent;
import org.kie.internal.command.RegistryContext;

public class RuleStatefulScenarioExecutableBuilder implements RuleScenarioExecutableBuilder {

    private final KieSessionFluent kieSessionFluent;
    private final ExecutableBuilder executableBuilder;
    private final Map<FactIdentifier, List<FactCheckerHandle>> internalConditions = new HashMap<>();
    private String agendaGroupName = null;

    private static final String DEFAULT_APPLICATION = "defaultApplication";

    protected static final BiFunction<String, KieContainer, KieSessionConfiguration> forcePseudoClock = (sn, kc) -> {
        KieSessionConfiguration kieSessionConfiguration = kc.getKieSessionConfiguration(sn);
        if (kieSessionConfiguration == null) {
            throw new ScenarioException("Impossible to find a KieSession with name " + sn);
        }
        kieSessionConfiguration.setOption(ClockTypeOption.PSEUDO);
        return kieSessionConfiguration;
    };

    protected RuleStatefulScenarioExecutableBuilder(KieContainer kieContainer, String kieSessionName) {
        executableBuilder = createExecutableBuilder();

        kieSessionFluent = executableBuilder
                .newApplicationContext(DEFAULT_APPLICATION)
                .setKieContainer(kieContainer)
                .newSessionCustomized(kieSessionName, forcePseudoClock);
    }

    protected RuleStatefulScenarioExecutableBuilder(KieContainer kieContainer) {
        this(kieContainer, null);
    }

    @Override
    public void addInternalCondition(Class<?> clazz,
                                     Function<Object, ValueWrapper> checkFunction,
                                     ScenarioResult scenarioResult) {
        internalConditions.computeIfAbsent(scenarioResult.getFactIdentifier(), key -> new ArrayList<>())
                .add(new FactCheckerHandle(clazz, checkFunction, scenarioResult));
    }

    @Override
    public void setActiveAgendaGroup(String agendaGroupName) {
        this.agendaGroupName = agendaGroupName;
        kieSessionFluent.setActiveAgendaGroup(agendaGroupName);
    }

    @Override
    public void setActiveRuleFlowGroup(String ruleFlowGroup) {
        this.agendaGroupName = ruleFlowGroup;
        kieSessionFluent.setActiveRuleFlowGroup(ruleFlowGroup);
    }

    @Override
    public void insert(Object element) {
        kieSessionFluent.insert(element);
    }

    @Override
    public Map<String, Object> run() {
        Objects.requireNonNull(executableBuilder, "Executable builder is null, please invoke create(KieContainer, )");

        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();

        kieSessionFluent.addCommand(new AddCoverageListenerCommand(coverageAgendaListener));

        kieSessionFluent.addCommand(context -> {
            KieSession kieSession = ((RegistryContext) context).lookup(KieSession.class);
            return getAvailableRules(kieSession.getKieBase(), agendaGroupName);
        }).out(RULES_AVAILABLE);

        kieSessionFluent.fireAllRules();
        internalConditions.values()
                .forEach(factToCheck -> kieSessionFluent.addCommand(new ValidateFactCommand(factToCheck)));

        kieSessionFluent.dispose().end();

        RequestContext execute = createExecutableRunner().execute(executableBuilder.getExecutable());
        Map<String, Object> toReturn = new HashMap<>(execute.getOutputs());

        toReturn.put(COVERAGE_LISTENER, coverageAgendaListener);
        return toReturn;
    }

    protected ExecutableBuilder createExecutableBuilder() {
        return ExecutableBuilder.create();
    }

    protected ExecutableRunner<RequestContext> createExecutableRunner() {
        return ExecutableRunner.create();
    }
}
