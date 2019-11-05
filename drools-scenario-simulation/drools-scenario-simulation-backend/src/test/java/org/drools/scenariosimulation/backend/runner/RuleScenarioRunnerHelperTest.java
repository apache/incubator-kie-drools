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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.expression.BaseExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.COVERAGE_LISTENER;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.RULES_AVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RuleScenarioRunnerHelperTest extends AbstractRuleCoverageTest {

    private static final String NAME = "NAME";
    private static final double AMOUNT = 10;
    private static final String TEST_DESCRIPTION = "Test description";
    private static final ClassLoader classLoader = RuleScenarioRunnerHelperTest.class.getClassLoader();
    private static final ExpressionEvaluatorFactory expressionEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE);
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
        firstNameGivenFactMapping = simulation.getScesimModelDescriptor().addFactMapping(personFactIdentifier, firstNameGivenExpressionIdentifier);
        firstNameGivenFactMapping.addExpressionElement("Fact 1", String.class.getCanonicalName());
        firstNameGivenFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        disputeFactIdentifier = FactIdentifier.create("Fact 2", Dispute.class.getCanonicalName());
        amountGivenExpressionIdentifier = ExpressionIdentifier.create("Amount Given", FactMappingType.GIVEN);
        amountNameGivenFactMapping = simulation.getScesimModelDescriptor().addFactMapping(disputeFactIdentifier, amountGivenExpressionIdentifier);
        amountNameGivenFactMapping.addExpressionElement("Fact 2", Double.class.getCanonicalName());
        amountNameGivenFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        firstNameExpectedExpressionIdentifier = ExpressionIdentifier.create("First Name Expected", FactMappingType.EXPECT);
        firstNameExpectedFactMapping = simulation.getScesimModelDescriptor().addFactMapping(personFactIdentifier, firstNameExpectedExpressionIdentifier);
        firstNameExpectedFactMapping.addExpressionElement("Fact 1", String.class.getCanonicalName());
        firstNameExpectedFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        amountExpectedExpressionIdentifier = ExpressionIdentifier.create("Amount Expected", FactMappingType.EXPECT);
        amountNameExpectedFactMapping = simulation.getScesimModelDescriptor().addFactMapping(disputeFactIdentifier, amountExpectedExpressionIdentifier);
        amountNameExpectedFactMapping.addExpressionElement("Fact 2", Double.class.getCanonicalName());
        amountNameExpectedFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        scenario1 = simulation.addData();
        scenario1.setDescription(TEST_DESCRIPTION);
        scenario1.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario1.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);

        scenario2 = simulation.addData();
        scenario2.setDescription(TEST_DESCRIPTION);
        scenario2.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        scenario2.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenario2.addMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, AMOUNT);
        amountNameExpectedFactMappingValue = scenario2.addMappingValue(disputeFactIdentifier, amountExpectedExpressionIdentifier, AMOUNT);
    }

    @Test
    public void extractGivenValuesTest() {
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        assertEquals(1, scenario1Inputs.size());

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        assertEquals(2, scenario2Inputs.size());

        scenario2.addOrUpdateMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "WrongValue");
        assertThatThrownBy(() -> runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                    scenario2.getUnmodifiableFactMappingValues(),
                                                                    classLoader,
                                                                    expressionEvaluatorFactory))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Error in GIVEN data");
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
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData1 = new ScenarioRunnerData();
        scenario1Inputs.forEach(scenarioRunnerData1::addGiven);
        scenario1Outputs.forEach(scenarioRunnerData1::addExpect);

        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData1,
                                      expressionEvaluatorFactory,
                                      null);
        assertEquals(1, scenarioRunnerData1.getResults().size());

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData2 = new ScenarioRunnerData();
        scenario2Inputs.forEach(scenarioRunnerData2::addGiven);
        scenario2Outputs.forEach(scenarioRunnerData2::addExpect);

        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData2,
                                      expressionEvaluatorFactory,
                                      null);
        assertEquals(2, scenarioRunnerData2.getResults().size());
    }

    @Test
    public void getScenarioResultsTest() {
        List<ScenarioGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertTrue(scenario1Inputs.size() > 0);

        ScenarioGiven input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario1Outputs, input1, expressionEvaluatorFactory);

        assertEquals(1, scenario1Results.size());
        assertEquals(FactMappingValueStatus.SUCCESS, scenario1Outputs.get(0).getExpectedResult().get(0).getStatus());

        List<ScenarioGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertTrue(scenario2Inputs.size() > 0);

        ScenarioGiven input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario2Outputs, input2, expressionEvaluatorFactory);

        assertEquals(1, scenario2Results.size());
        assertEquals(FactMappingValueStatus.SUCCESS, scenario1Outputs.get(0).getExpectedResult().get(0).getStatus());

        List<ScenarioExpect> newFact = Collections.singletonList(new ScenarioExpect(personFactIdentifier, Collections.emptyList(), true));
        List<ScenarioResult> scenario2NoResults = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), newFact, input2, expressionEvaluatorFactory);

        assertEquals(0, scenario2NoResults.size());

        Person person = new Person();
        person.setFirstName("ANOTHER STRING");
        ScenarioGiven newInput = new ScenarioGiven(personFactIdentifier, person);

        List<ScenarioResult> scenario3Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario1Outputs, newInput, expressionEvaluatorFactory);
        assertEquals(FactMappingValueStatus.FAILED_WITH_ERROR, scenario1Outputs.get(0).getExpectedResult().get(0).getStatus());

        assertEquals(1, scenario3Results.size());
        assertEquals(person.getFirstName(), scenario3Results.get(0).getResultValue().get());
        assertEquals("NAME", scenario3Results.get(0).getFactMappingValue().getRawValue());
    }

    @Test
    public void validateAssertionTest() {

        List<ScenarioResult> scenarioFailResult = new ArrayList<>();
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, scenario2);
            fail();
        } catch (ScenarioException ignored) {
        }

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue()).setResult(true));
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
        Function<Object, ResultWrapper> extractorFunction = runnerHelper.createExtractorFunction(expressionEvaluator, factMappingValue, simulation.getScesimModelDescriptor());
        Person person = new Person();

        person.setFirstName(personName);
        assertTrue(extractorFunction.apply(person).isSatisfied());

        person.setFirstName("OtherString");
        assertFalse(extractorFunction.apply(person).isSatisfied());

        Function<Object, ResultWrapper> extractorFunction1 = runnerHelper.createExtractorFunction(expressionEvaluator,
                                                                                                  new FactMappingValue(personFactIdentifier,
                                                                                                                       firstNameGivenExpressionIdentifier,
                                                                                                                       null),
                                                                                                  simulation.getScesimModelDescriptor());
        ResultWrapper nullValue = extractorFunction1.apply(new Person());
        assertTrue(nullValue.isSatisfied());
        assertNull(nullValue.getResult());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void getParamsForBeanTest() {
        List<FactMappingValue> factMappingValues = new ArrayList<>();
        FactMappingValue factMappingValue1 = spy(new FactMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "NOT PARSABLE"));
        FactMappingValue factMappingValue2 = spy(new FactMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "NOT PARSABLE"));
        FactMappingValue factMappingValue3 = spy(new FactMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "1"));
        factMappingValues.add(factMappingValue1);
        factMappingValues.add(factMappingValue2);
        factMappingValues.add(factMappingValue3);

        assertThatThrownBy(() -> runnerHelper.getParamsForBean(simulation.getScesimModelDescriptor(), disputeFactIdentifier, factMappingValues, expressionEvaluatorFactory))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Error in one or more input values");

        factMappingValues.forEach(fmv -> verify(fmv, times(2)).getRawValue());

        assertEquals(FactMappingValueStatus.FAILED_WITH_EXCEPTION, factMappingValue1.getStatus());
        assertEquals(FactMappingValueStatus.FAILED_WITH_EXCEPTION, factMappingValue2.getStatus());
        assertEquals(FactMappingValueStatus.SUCCESS, factMappingValue3.getStatus());
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
        Map<String, Integer> coverageData = new LinkedHashMap<>();
        coverageData.put("rule1", 2);
        coverageData.put("rule2", 2);
        CoverageAgendaListener coverageAgendaListenerMock = createCoverageAgendaListenerWithData(coverageData);

        ScenarioWithIndex scenarioWithIndexMock = mock(ScenarioWithIndex.class);
        Scenario scenarioMock = mock(Scenario.class);
        when(scenarioMock.getDescription()).thenReturn("DESCRIPTION");
        when(scenarioWithIndexMock.getScesimData()).thenReturn(scenarioMock);

        Map<String, Object> requestContext = new LinkedHashMap<>();
        requestContext.put(COVERAGE_LISTENER, coverageAgendaListenerMock);
        requestContext.put(RULES_AVAILABLE, coverageData.keySet());

        ScenarioResultMetadata scenarioResultMetadata = runnerHelper.extractResultMetadata(requestContext, scenarioWithIndexMock);

        assertEquals(scenarioWithIndexMock, scenarioResultMetadata.getScenarioWithIndex());
        assertEquals(2, scenarioResultMetadata.getAvailable().size());
        assertEquals(2, scenarioResultMetadata.getExecuted().size());
        assertEquals((Integer) 2, scenarioResultMetadata.getExecutedWithCounter().get("rule1"));
        assertEquals((Integer) 2, scenarioResultMetadata.getExecutedWithCounter().get("rule2"));
        List<String> expectedMessages = new ArrayList<>();
        commonAddMessageString(Arrays.asList("rule1", "rule2"), expectedMessages);

        final List<AuditLogLine> auditLogLines = scenarioResultMetadata.getAuditLogLines();
        assertEquals(expectedMessages.size(), auditLogLines.size());
        for (int i = 0; i < expectedMessages.size(); i++) {
            commonCheckAuditLogLine(auditLogLines.get(i), expectedMessages.get(i), "INFO");
        }
    }

    private void commonAddMessageString(List<String> ruleNames, List<String> expectedMessages) {
        ruleNames.forEach(ruleName ->
                                  IntStream.range(1, 3).forEach(index -> expectedMessages.add(CoverageAgendaListener.generateAuditMessage(ruleName))));
    }
}