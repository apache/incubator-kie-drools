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

package org.drools.scenariosimulation.backend.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.expression.BaseExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.fluent.AbstractRuleCoverageTest;
import org.drools.scenariosimulation.backend.fluent.CoverageAgendaListener;
import org.drools.scenariosimulation.backend.model.Dispute;
import org.drools.scenariosimulation.backend.model.Person;
import org.drools.scenariosimulation.backend.runner.model.ResultWrapper;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.junit.Before;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.COVERAGE_LISTENER;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.RULES_AVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RuleScenarioRunnerHelperTest extends AbstractRuleCoverageTest {

    private static final String NAME = "NAME";
    private static final double AMOUNT = 10;
    private static final String TEST_DESCRIPTION = "Test description";
    private static final ClassLoader classLoader = RuleScenarioRunnerHelperTest.class.getClassLoader();
    private static final ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);
    private static final RuleScenarioRunnerHelper runnerHelper = new RuleScenarioRunnerHelper();

    private Simulation simulation;
    private FactIdentifier personFactIdentifier;
    private ExpressionIdentifier firstNameGivenExpressionIdentifier;
    private FactMapping firstNameGivenFactMapping;
    private Scenario scenario1;
    private Scenario scenario2;
    private ExpressionIdentifier firstNameExpectedExpressionIdentifier;
    private FactMapping firstNameExpectedFactMapping;
    private FactIdentifier disputeFactIdentifier;
    private ExpressionIdentifier amountGivenExpressionIdentifier;
    private FactMapping amountNameGivenFactMapping;
    private ExpressionIdentifier amountExpectedExpressionIdentifier;
    private FactMapping amountNameExpectedFactMapping;
    private FactMappingValue amountNameExpectedFactMappingValue;

    @Before
    public void setup() {
        simulation = new Simulation();
        personFactIdentifier = FactIdentifier.create("Fact 1", Person.class.getCanonicalName());
        firstNameGivenExpressionIdentifier = ExpressionIdentifier.create("First Name Given", FactMappingType.GIVEN);
        firstNameGivenFactMapping = simulation.getSimulationDescriptor().addFactMapping(personFactIdentifier, firstNameGivenExpressionIdentifier);
        firstNameGivenFactMapping.addExpressionElement("Fact 1", String.class.getCanonicalName());
        firstNameGivenFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        disputeFactIdentifier = FactIdentifier.create("Fact 2", Dispute.class.getCanonicalName());
        amountGivenExpressionIdentifier = ExpressionIdentifier.create("Amount Given", FactMappingType.GIVEN);
        amountNameGivenFactMapping = simulation.getSimulationDescriptor().addFactMapping(disputeFactIdentifier, amountGivenExpressionIdentifier);
        amountNameGivenFactMapping.addExpressionElement("Fact 2", Double.class.getCanonicalName());
        amountNameGivenFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        firstNameExpectedExpressionIdentifier = ExpressionIdentifier.create("First Name Expected", FactMappingType.EXPECT);
        firstNameExpectedFactMapping = simulation.getSimulationDescriptor().addFactMapping(personFactIdentifier, firstNameExpectedExpressionIdentifier);
        firstNameExpectedFactMapping.addExpressionElement("Fact 1", String.class.getCanonicalName());
        firstNameExpectedFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        amountExpectedExpressionIdentifier = ExpressionIdentifier.create("Amount Expected", FactMappingType.EXPECT);
        amountNameExpectedFactMapping = simulation.getSimulationDescriptor().addFactMapping(disputeFactIdentifier, amountExpectedExpressionIdentifier);
        amountNameExpectedFactMapping.addExpressionElement("Fact 2", Double.class.getCanonicalName());
        amountNameExpectedFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        scenario1 = simulation.addScenario();
        scenario1.setDescription(TEST_DESCRIPTION);
        scenario1.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario1.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);

        scenario2 = simulation.addScenario();
        scenario2.setDescription(TEST_DESCRIPTION);
        scenario2.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario2.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenario2.addMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, AMOUNT);
        amountNameExpectedFactMappingValue = scenario2.addMappingValue(disputeFactIdentifier, amountExpectedExpressionIdentifier, AMOUNT);
    }

    @Test
    public void extractGivenValuesTest() {
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        assertEquals(1, scenario1Inputs.size());

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        assertEquals(2, scenario2Inputs.size());
    }

    @Test
    public void extractExpectedValuesTest() {
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());
        assertEquals(1, scenario1Outputs.size());

        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.EXPECT),
                                          "TEST");
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
        assertEquals(3, scenario2Outputs.size());
        assertEquals(1, scenario2Outputs.stream().filter(ScenarioExpect::isNewFact).count());
    }

    @Test
    public void verifyConditionsTest() {
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData1 = new ScenarioRunnerData();
        scenario1Inputs.forEach(scenarioRunnerData1::addGiven);
        scenario1Outputs.forEach(scenarioRunnerData1::addExpect);

        runnerHelper.verifyConditions(simulation.getSimulationDescriptor(),
                                      scenarioRunnerData1,
                                      expressionEvaluator,
                                      null);
        assertEquals(1, scenarioRunnerData1.getResults().size());

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData2 = new ScenarioRunnerData();
        scenario2Inputs.forEach(scenarioRunnerData2::addGiven);
        scenario2Outputs.forEach(scenarioRunnerData2::addExpect);

        runnerHelper.verifyConditions(simulation.getSimulationDescriptor(),
                                      scenarioRunnerData2,
                                      expressionEvaluator,
                                      null);
        assertEquals(2, scenarioRunnerData2.getResults().size());
    }

    @Test
    public void getScenarioResultsTest() {
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertTrue(scenario1Inputs.size() > 0);

        ScenarioGiven input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario1Outputs, input1, expressionEvaluator);

        assertEquals(1, scenario1Results.size());
        assertEquals(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus(), FactMappingValueStatus.SUCCESS);

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getSimulationDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluator);
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertTrue(scenario2Inputs.size() > 0);

        ScenarioGiven input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario2Outputs, input2, expressionEvaluator);

        assertEquals(1, scenario2Results.size());
        assertEquals(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus(), FactMappingValueStatus.SUCCESS);

        List<ScenarioExpect> newFact = Collections.singletonList(new ScenarioExpect(personFactIdentifier, Collections.emptyList(), true));
        List<ScenarioResult> scenario2NoResults = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), newFact, input2, expressionEvaluator);

        assertEquals(0, scenario2NoResults.size());

        Person person = new Person();
        person.setFirstName("ANOTHER STRING");
        ScenarioGiven newInput = new ScenarioGiven(personFactIdentifier, person);

        List<ScenarioResult> scenario3Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getSimulationDescriptor(), scenario1Outputs, newInput, expressionEvaluator);
        assertEquals(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus(), FactMappingValueStatus.FAILED_WITH_ERROR);

        assertEquals(1, scenario3Results.size());
        assertEquals(person.getFirstName(), scenario3Results.get(0).getResultValue().get());
        assertEquals("NAME", scenario3Results.get(0).getFactMappingValue().getRawValue());
    }

    @Test
    public void validateAssertionTest() {

        List<ScenarioResult> scenarioFailResult = new ArrayList<>();
        scenarioFailResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, scenario2);
            fail();
        } catch (ScenarioException ignored) {
        }

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(disputeFactIdentifier, amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue()).setResult(true));
        runnerHelper.validateAssertion(scenarioSuccessResult, scenario2);
    }

    @Test
    public void groupByFactIdentifierAndFilterTest() {
        Map<FactIdentifier, List<FactMappingValue>> scenario1Given = runnerHelper.groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario1Expected = runnerHelper.groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Given = runnerHelper.groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        Map<FactIdentifier, List<FactMappingValue>> scenario2Expected = runnerHelper.groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);

        assertEquals(1, scenario1Given.keySet().size());
        assertEquals(1, scenario1Expected.keySet().size());
        assertEquals(2, scenario2Given.keySet().size());
        assertEquals(2, scenario2Expected.keySet().size());

        assertEquals(1, scenario1Given.get(personFactIdentifier).size());
        assertEquals(1, scenario1Expected.get(personFactIdentifier).size());
        assertEquals(1, scenario2Given.get(disputeFactIdentifier).size());
        assertEquals(1, scenario2Expected.get(disputeFactIdentifier).size());

        Scenario scenario = new Scenario();
        scenario.addMappingValue(FactIdentifier.EMPTY, ExpressionIdentifier.DESCRIPTION, null);
        assertEquals(0, runnerHelper.groupByFactIdentifierAndFilter(scenario.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void groupByFactIdentifierAndFilterFailTest() {
        List<FactMappingValue> fail = new ArrayList<>();
        FactMappingValue factMappingValue = new FactMappingValue();
        factMappingValue.setRawValue("TEST");
        fail.add(factMappingValue);
        runnerHelper.groupByFactIdentifierAndFilter(fail, FactMappingType.GIVEN);
    }

    @Test
    public void createExtractorFunctionTest() {
        String personName = "Test";
        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, personName);
        Function<Object, ResultWrapper> extractorFunction = runnerHelper.createExtractorFunction(expressionEvaluator, factMappingValue, simulation.getSimulationDescriptor());
        Person person = new Person();

        person.setFirstName(personName);
        assertTrue(extractorFunction.apply(person).isSatisfied());

        person.setFirstName("OtherString");
        assertFalse(extractorFunction.apply(person).isSatisfied());

        Function<Object, ResultWrapper> extractorFunction1 = runnerHelper.createExtractorFunction(expressionEvaluator,
                                                                                                  new FactMappingValue(personFactIdentifier,
                                                                                                                       firstNameGivenExpressionIdentifier,
                                                                                                                       null),
                                                                                                  simulation.getSimulationDescriptor());
        ResultWrapper nullValue = extractorFunction1.apply(new Person());
        assertTrue(nullValue.isSatisfied());
        assertNull(nullValue.getResult());
    }

    @Test
    public void getParamsForBeanTest() {
        List<FactMappingValue> factMappingValues = new ArrayList<>();
        FactMappingValue factMappingValue = new FactMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "NOT PARSABLE");
        factMappingValues.add(factMappingValue);

        try {
            runnerHelper.getParamsForBean(simulation.getSimulationDescriptor(), disputeFactIdentifier, factMappingValues, expressionEvaluator);
            fail();
        } catch (ScenarioException ignored) {

        }
        assertEquals(factMappingValue.getStatus(), FactMappingValueStatus.FAILED_WITH_EXCEPTION);
    }

    @Test
    public void directMappingTest() {
        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(Collections.emptyList(), "Test");

        assertEquals("Test", runnerHelper.getDirectMapping(paramsToSet).getResult());

        paramsToSet.clear();
        paramsToSet.put(Collections.emptyList(), 1);

        assertEquals(1, runnerHelper.getDirectMapping(paramsToSet).getResult());

        paramsToSet.clear();
        paramsToSet.put(Collections.emptyList(), null);

        assertNull(runnerHelper.getDirectMapping(paramsToSet).getResult());

        paramsToSet.clear();

        ResultWrapper<Object> directMapping = runnerHelper.getDirectMapping(paramsToSet);
        assertFalse(directMapping.isSatisfied());
        assertEquals("No direct mapping available", directMapping.getErrorMessage().get());
    }

    @Test
    public void extractResultMetadata() {
        Map<String, Integer> coverageData = new HashMap<>();
        coverageData.put("rule1", 2);
        coverageData.put("rule2", 2);
        CoverageAgendaListener coverageAgendaListenerMock = createCoverageAgendaListenerWithData(coverageData);

        ScenarioWithIndex scenarioWithIndexMock = mock(ScenarioWithIndex.class);

        Map<String, Object> requestContext = new HashMap<>();
        requestContext.put(COVERAGE_LISTENER, coverageAgendaListenerMock);
        requestContext.put(RULES_AVAILABLE, coverageData.keySet());

        ScenarioResultMetadata scenarioResultMetadata = runnerHelper.extractResultMetadata(requestContext, scenarioWithIndexMock);

        assertEquals(scenarioWithIndexMock, scenarioResultMetadata.getScenarioWithIndex());
        assertEquals(2, scenarioResultMetadata.getAvailable().size());
        assertEquals(2, scenarioResultMetadata.getExecuted().size());
        assertEquals((Integer) 2, scenarioResultMetadata.getExecutedWithCounter().get("rule1"));
        assertEquals((Integer) 2, scenarioResultMetadata.getExecutedWithCounter().get("rule2"));
    }
}