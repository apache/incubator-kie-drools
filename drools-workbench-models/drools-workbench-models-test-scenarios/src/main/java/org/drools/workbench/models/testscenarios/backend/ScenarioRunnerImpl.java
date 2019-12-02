/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.addon.ClassTypeResolver;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.workbench.models.testscenarios.backend.populators.FactPopulator;
import org.drools.workbench.models.testscenarios.backend.populators.FactPopulatorFactory;
import org.drools.workbench.models.testscenarios.shared.ActivateRuleFlowGroup;
import org.drools.workbench.models.testscenarios.shared.CallMethod;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Expectation;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.RetractFact;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.api.runtime.KieSession;
import org.mvel2.MVEL;

/**
 * This actually runs the test scenarios.
 */
public class ScenarioRunnerImpl
        implements ScenarioRunner {

    private final KieSession ksession;
    private final int maximumAmountOfRuleFirings;
    private TestScenarioKSessionWrapper workingMemoryWrapper;
    private FactPopulatorFactory factPopulatorFactory;
    private FactPopulator factPopulator;

    /**
     * @param ksession A populated type resolved to be used to resolve the types in
     * the scenario.
     * @param maximumAmountOfRuleFirings Limit for amount of rules that can fire. To prevent infinite loops.
     * @throws ClassNotFoundException
     */
    public ScenarioRunnerImpl(final KieSession ksession,
                              final int maximumAmountOfRuleFirings) {
        this.ksession = ksession;
        this.maximumAmountOfRuleFirings = maximumAmountOfRuleFirings;
    }

    static Set<String> getImports(final Scenario scenario) {
        final Set<String> imports = new HashSet<String>();
        imports.addAll(scenario.getImports().getImportStrings());
        if (scenario.getPackageName() != null && !scenario.getPackageName().isEmpty()) {
            imports.add(scenario.getPackageName() + ".*");
        }
        return imports;
    }

    public void run(final Scenario scenario) throws Exception {

        // This looks safe!
        final InternalKnowledgeBase kieBase = (InternalKnowledgeBase) ksession.getKieBase();

        final Map<String, Object> populatedData = new HashMap<>();
        final Map<String, Object> globalData = new HashMap<>();

        final ClassLoader classloader2 = kieBase.getRootClassLoader();

        final ClassTypeResolver resolver = new ClassTypeResolver(getImports(scenario),
                                                                 classloader2);

        this.workingMemoryWrapper = new TestScenarioKSessionWrapper(ksession,
                                                                    resolver,
                                                                    populatedData,
                                                                    globalData,
                                                                    scenarioUsesTimeWalk(scenario));
        this.factPopulatorFactory = new FactPopulatorFactory(populatedData,
                                                             globalData,
                                                             resolver);
        this.factPopulator = new FactPopulator(ksession,
                                               populatedData);

        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        scenario.setLastRunResult(new Date());

        populateGlobals(scenario.getGlobals());

        applyFixtures(scenario.getFixtures(),
                      createScenarioSettings(scenario));
    }

    private boolean scenarioUsesTimeWalk(Scenario scenario) {
        for (Fixture fixture : scenario.getFixtures()) {
            if (fixture instanceof ExecutionTrace) {
                if (((ExecutionTrace) fixture).getScenarioSimulatedDate() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private ScenarioSettings createScenarioSettings(final Scenario scenario) {
        final ScenarioSettings scenarioSettings = new ScenarioSettings();
        scenarioSettings.setRuleList(scenario.getRules());
        scenarioSettings.setInclusive(scenario.isInclusive());
        scenarioSettings.setMaxRuleFirings(getMaxRuleFirings(scenario));
        return scenarioSettings;
    }

    private int getMaxRuleFirings(final Scenario scenario) {
        if (maximumAmountOfRuleFirings <= 0) {
            return scenario.getMaxRuleFirings();
        } else {
            return maximumAmountOfRuleFirings;
        }
    }

    private void applyFixtures(final List<Fixture> fixtures,
                               final ScenarioSettings scenarioSettings)
            throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException, InvalidClockTypeException {

        for (Iterator<Fixture> iterator = fixtures.iterator(); iterator.hasNext(); ) {
            Fixture fixture = iterator.next();

            if (fixture instanceof FactData) {

                factPopulator.add(factPopulatorFactory.createFactPopulator((FactData) fixture));
            } else if (fixture instanceof RetractFact) {

                factPopulator.retractFact(((RetractFact) fixture).getName());
            } else if (fixture instanceof CallMethod) {

                workingMemoryWrapper.executeMethod((CallMethod) fixture);
            } else if (fixture instanceof ActivateRuleFlowGroup) {

                workingMemoryWrapper.activateRuleFlowGroup(((ActivateRuleFlowGroup) fixture).getName());
            } else if (fixture instanceof ExecutionTrace) {

                factPopulator.populate();

                workingMemoryWrapper.executeSubScenario((ExecutionTrace) fixture,
                                                        scenarioSettings);
            } else if (fixture instanceof Expectation) {

                factPopulator.populate();

                workingMemoryWrapper.verifyExpectation((Expectation) fixture);
            } else {
                throw new IllegalArgumentException("Not sure what to do with " + fixture);
            }
        }

        factPopulator.populate();
    }

    private void populateGlobals(final List<FactData> globals) throws Exception {

        for (final FactData fact : globals) {
            factPopulator.add(
                    factPopulatorFactory.createGlobalFactPopulator(fact));
        }

        factPopulator.populate();
    }
}
