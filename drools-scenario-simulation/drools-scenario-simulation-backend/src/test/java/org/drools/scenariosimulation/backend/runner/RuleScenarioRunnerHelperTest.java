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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.drools.scenariosimulation.api.model.AuditLogLine;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.FactMappingValueStatus;
import org.drools.scenariosimulation.api.model.FactMappingValueType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.utils.ConstantsHolder;
import org.drools.scenariosimulation.backend.expression.BaseExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.fluent.AbstractRuleCoverageTest;
import org.drools.scenariosimulation.backend.fluent.CoverageAgendaListener;
import org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder;
import org.drools.scenariosimulation.backend.model.Dispute;
import org.drools.scenariosimulation.backend.model.Person;
import org.drools.scenariosimulation.backend.runner.model.InstanceGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationServerMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleScenarioRunnerHelperTest extends AbstractRuleCoverageTest {

    @Mock
    protected RuleScenarioExecutableBuilder ruleScenarioExecutableBuilderMock;

    @Mock
    protected KieContainer kieContainerMock;

    private static final String NAME = "NAME";
    private static final String AMOUNT = "10";
    private static final String TEST_DESCRIPTION = "Test description";
    private static final ClassLoader classLoader = RuleScenarioRunnerHelperTest.class.getClassLoader();
    private static final ExpressionEvaluatorFactory expressionEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.RULE);
    private static final ExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);
    private final RuleScenarioRunnerHelper runnerHelper = new RuleScenarioRunnerHelper() {
        @Override
        protected RuleScenarioExecutableBuilder createBuilderWrapper(KieContainer kieContainer, Settings settings) {
            return ruleScenarioExecutableBuilderMock;
        }
    };

    private Simulation simulation;
    private Background background;
    private Settings settings;
    private FactIdentifier personFactIdentifier;
    private ExpressionIdentifier firstNameGivenExpressionIdentifier;
    private FactMapping firstNameGivenFactMapping;
    private FactMapping backgroundFirstNameGivenFactMapping;
    private Scenario scenario1;
    private Scenario scenario2;
    private BackgroundData backgroundData1;
    private BackgroundData backgroundData2;
    private ExpressionIdentifier firstNameExpectedExpressionIdentifier;
    private FactMapping firstNameExpectedFactMapping;
    private FactIdentifier disputeFactIdentifier;
    private ExpressionIdentifier amountGivenExpressionIdentifier;
    private FactMapping amountNameGivenFactMapping;
    private FactMapping backgroundAmountNameGivenFactMapping;
    private ExpressionIdentifier amountExpectedExpressionIdentifier;
    private FactMapping amountNameExpectedFactMapping;
    private FactMappingValue amountNameExpectedFactMappingValue;
    private FactMapping disputeExpressionGivenFactMapping;
    private ExpressionIdentifier expressionGivenExpressionIdentifier;

    @Before
    public void setup() {
        when(kieContainerMock.getClassLoader()).thenReturn(classLoader);

        simulation = new Simulation();
        background = new Background();
        settings = new Settings();
        settings.setType(ScenarioSimulationModel.Type.RULE);
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

        expressionGivenExpressionIdentifier = ExpressionIdentifier.create("directMapping", FactMappingType.GIVEN);
        disputeExpressionGivenFactMapping = simulation.getScesimModelDescriptor().addFactMapping(disputeFactIdentifier, expressionGivenExpressionIdentifier);
        disputeExpressionGivenFactMapping.setFactMappingValueType(FactMappingValueType.EXPRESSION);
        disputeExpressionGivenFactMapping.addExpressionElement("Dispute", Dispute.class.getCanonicalName());

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

        backgroundFirstNameGivenFactMapping = background.getScesimModelDescriptor().addFactMapping(personFactIdentifier, firstNameGivenExpressionIdentifier);
        backgroundFirstNameGivenFactMapping.addExpressionElement("Person", String.class.getCanonicalName());
        backgroundFirstNameGivenFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        backgroundAmountNameGivenFactMapping = background.getScesimModelDescriptor().addFactMapping(disputeFactIdentifier, amountGivenExpressionIdentifier);
        backgroundAmountNameGivenFactMapping.addExpressionElement("Dispute", Double.class.getCanonicalName());
        backgroundAmountNameGivenFactMapping.addExpressionElement("amount", Double.class.getCanonicalName());

        backgroundData1 = background.addData();
        backgroundData1.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
        backgroundData1.addMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, AMOUNT);
        backgroundData2 = background.addData();
        backgroundData2.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, NAME);
    }

    @Test
    public void extractGivenValuesTest() {
        List<InstanceGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        assertEquals(1, scenario1Inputs.size());

        List<InstanceGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        assertEquals(2, scenario2Inputs.size());

        // add expression
        scenario2.addOrUpdateMappingValue(disputeFactIdentifier, expressionGivenExpressionIdentifier, "# new org.drools.scenariosimulation.backend.model.Dispute(\"dispute description\", 10)");

        scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                          scenario2.getUnmodifiableFactMappingValues(),
                                                          classLoader,
                                                          expressionEvaluatorFactory);
        assertEquals(2, scenario2Inputs.size());
        Optional<Dispute> disputeGivenOptional = scenario2Inputs.stream()
                .filter(elem -> elem.getValue() instanceof Dispute)
                .map(elem -> (Dispute) elem.getValue())
                .findFirst();
        assertTrue(disputeGivenOptional.isPresent());
        assertEquals("dispute description", disputeGivenOptional.get().getDescription());

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

        /* A Given "TEST" fact with null rawValue should works as the previous case, i.e. to not consider the GIVEN fact with empty data */
        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.GIVEN),
                                          null);
        List<ScenarioExpect> scenario2aOutputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
        assertEquals(3, scenario2aOutputs.size());
        assertEquals(1, scenario2aOutputs.stream().filter(ScenarioExpect::isNewFact).count());
    }

    @Test
    public void verifyConditionsTest() {
        List<InstanceGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
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

        List<InstanceGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
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
        List<InstanceGiven> scenario1Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertTrue(scenario1Inputs.size() > 0);

        InstanceGiven input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario1Outputs, input1, expressionEvaluatorFactory);

        assertEquals(1, scenario1Results.size());
        assertEquals(FactMappingValueStatus.SUCCESS, scenario1Outputs.get(0).getExpectedResult().get(0).getStatus());

        List<InstanceGiven> scenario2Inputs = runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario2.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertTrue(scenario2Inputs.size() > 0);

        InstanceGiven input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario2Outputs, input2, expressionEvaluatorFactory);

        assertEquals(1, scenario2Results.size());
        assertEquals(FactMappingValueStatus.SUCCESS, scenario1Outputs.get(0).getExpectedResult().get(0).getStatus());

        List<ScenarioExpect> newFact = singletonList(new ScenarioExpect(personFactIdentifier, emptyList(), true));
        List<ScenarioResult> scenario2NoResults = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), newFact, input2, expressionEvaluatorFactory);

        assertEquals(0, scenario2NoResults.size());

        Person person = new Person();
        person.setFirstName("ANOTHER STRING");
        InstanceGiven newInput = new InstanceGiven(personFactIdentifier, person);

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
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail();
        } catch (IllegalStateException exception) {
            assertEquals("Illegal FactMappingValue status", exception.getMessage());
        }

        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setErrorValue("Error");
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail();
        } catch (ScenarioException exception) {
            assertTrue(exception.isFailedAssertion());
            assertEquals(ScenarioSimulationServerMessages.getFactWithWrongValueExceptionMessage("Fact 2.amount",
                                                                                                amountNameExpectedFactMappingValue.getRawValue(),
                                                                                                amountNameExpectedFactMappingValue.getErrorValue()),
                         exception.getMessage());
        }

        String exceptionMessage = "Message";
        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setExceptionMessage(exceptionMessage);
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail();
        } catch (ScenarioException exception) {
            assertFalse(exception.isFailedAssertion());
            assertEquals(ScenarioSimulationServerMessages.getGenericScenarioExceptionMessage(exceptionMessage),
                         exception.getMessage());
        }

        List<String> pathToValue = Arrays.asList("Item #2");
        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setCollectionPathToValue(pathToValue);
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail();
        } catch (ScenarioException exception) {
            assertTrue(exception.isFailedAssertion());
            assertEquals(ScenarioSimulationServerMessages.getCollectionFactExceptionMessage("Fact 2.amount",
                                                                                            pathToValue,
                                                                                            amountNameExpectedFactMappingValue.getErrorValue()),
                         exception.getMessage());
        }

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue()).setResult(true));
        runnerHelper.validateAssertion(scenarioSuccessResult, simulation.getScesimModelDescriptor());
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
        Function<Object, ValueWrapper> extractorFunction = runnerHelper.createExtractorFunction(expressionEvaluator, factMappingValue, simulation.getScesimModelDescriptor());
        Person person = new Person();

        person.setFirstName(personName);
        assertTrue(extractorFunction.apply(person).isValid());

        person.setFirstName("OtherString");
        assertFalse(extractorFunction.apply(person).isValid());

        Function<Object, ValueWrapper> extractorFunction1 = runnerHelper.createExtractorFunction(expressionEvaluator,
                                                                                                  new FactMappingValue(personFactIdentifier,
                                                                                                                       firstNameGivenExpressionIdentifier,
                                                                                                                       null),
                                                                                                  simulation.getScesimModelDescriptor());
        ValueWrapper nullValue = extractorFunction1.apply(new Person());
        assertTrue(nullValue.isValid());
        assertNull(nullValue.getValue());
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
        paramsToSet.put(emptyList(), "Test");

        assertEquals("Test", runnerHelper.getDirectMapping(paramsToSet).getValue());

        paramsToSet.clear();
        paramsToSet.put(emptyList(), 1);

        assertEquals(1, runnerHelper.getDirectMapping(paramsToSet).getValue());

        paramsToSet.clear();
        paramsToSet.put(emptyList(), null);

        assertNull(runnerHelper.getDirectMapping(paramsToSet).getValue());

        paramsToSet.clear();

        ValueWrapper<Object> directMapping = runnerHelper.getDirectMapping(paramsToSet);
        assertFalse(directMapping.isValid());
        assertEquals("No direct mapping available", directMapping.getErrorMessage().get());
    }

    @Test
    public void createObject() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(singletonList("firstName"), "TestName");
        params.put(singletonList("age"), 10);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                Person.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());
        assertTrue(objectRaw instanceof Person);

        Person object = (Person) objectRaw;
        assertEquals(10, object.getAge());
        assertEquals("TestName", object.getFirstName());
    }

    @Test
    public void createObjectDirectMappingSimpleType() {
        Map<List<String>, Object> params = new HashMap<>();
        String directMappingSimpleTypeValue = "TestName";
        params.put(Collections.emptyList(), directMappingSimpleTypeValue);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                String.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());

        assertTrue(objectRaw instanceof String);

        assertEquals(directMappingSimpleTypeValue, objectRaw);
    }

    @Test
    public void createObjectDirectMappingSimpleTypeNull() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(emptyList(), null);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                String.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());

        assertNull(objectRaw);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createObjectDirectMappingComplexType() {
        Map<List<String>, Object> params = new HashMap<>();
        Person directMappingComplexTypeValue = new Person();
        directMappingComplexTypeValue.setFirstName("TestName");
        params.put(emptyList(), directMappingComplexTypeValue);
        params.put(singletonList("age"), 10);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                Map.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());

        assertTrue(objectRaw instanceof Person);

        Person object = (Person) objectRaw;
        assertEquals(10, object.getAge());
        assertEquals("TestName", object.getFirstName());
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
            commonCheckAuditLogLine(auditLogLines.get(i), expectedMessages.get(i), ConstantsHolder.EXECUTED, null);
        }
    }

    @Test
    public void extractBackgroundValues() {
        // TEST 0 - empty background
        Background emptyBackground = new Background();
        List<InstanceGiven> emptyBackgroundGivens = runnerHelper.extractBackgroundValues(emptyBackground,
                                                                                         classLoader,
                                                                                         expressionEvaluatorFactory);
        assertEquals(0, emptyBackgroundGivens.size());

        emptyBackground.addData();
        emptyBackgroundGivens = runnerHelper.extractBackgroundValues(emptyBackground,
                                                                     classLoader,
                                                                     expressionEvaluatorFactory);
        assertEquals(0, emptyBackgroundGivens.size());

        // TEST 1 - background correct
        List<InstanceGiven> backgroundGivens = runnerHelper.extractBackgroundValues(this.background,
                                                                                    classLoader,
                                                                                    expressionEvaluatorFactory);
        assertEquals(3, backgroundGivens.size());
        for (InstanceGiven backgroundGiven : backgroundGivens) {
            if (backgroundGiven.getFactIdentifier().equals(personFactIdentifier)) {
                assertEquals(personFactIdentifier, backgroundGiven.getFactIdentifier());
                Person person = (Person) backgroundGiven.getValue();
                assertEquals(NAME, person.getFirstName());
            } else if (backgroundGiven.getFactIdentifier().equals(disputeFactIdentifier)) {
                assertEquals(disputeFactIdentifier, backgroundGiven.getFactIdentifier());
                Dispute dispute = (Dispute) backgroundGiven.getValue();
                double parsedAmount = Double.parseDouble(AMOUNT);
                assertEquals(parsedAmount, dispute.getAmount(), 0.1);
            } else {
                fail();
            }
        }

        // TEST 2 - broken background
        String notValid = "notValid";
        FactMappingValue notValid1 = backgroundData1.addOrUpdateMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, notValid);
        FactMappingValue notValid2 = backgroundData2.addOrUpdateMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, notValid);

        assertThatThrownBy(() -> runnerHelper.extractBackgroundValues(this.background,
                                                                      classLoader,
                                                                      expressionEvaluatorFactory))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Error in BACKGROUND data");

        assertEquals(FactMappingValueStatus.FAILED_WITH_EXCEPTION, notValid1.getStatus());
        assertTrue(notValid1.getExceptionMessage().startsWith("Impossible to parse"));
        assertEquals(FactMappingValueStatus.FAILED_WITH_EXCEPTION, notValid2.getStatus());
        assertTrue(notValid2.getExceptionMessage().startsWith("Impossible to parse"));
    }

    @Test
    public void executeScenario() {
        ArgumentCaptor<Object> insertCaptor = ArgumentCaptor.forClass(Object.class);

        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addBackground(new InstanceGiven(personFactIdentifier, new Person()));
        scenarioRunnerData.addBackground(new InstanceGiven(disputeFactIdentifier, new Dispute()));
        scenarioRunnerData.addGiven(new InstanceGiven(personFactIdentifier, new Person()));
        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(factMappingValue), false));
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(factMappingValue), true));

        int inputObjects = scenarioRunnerData.getBackgrounds().size() + scenarioRunnerData.getGivens().size();

        String ruleFlowGroup = "ruleFlowGroup";
        settings.setRuleFlowGroup(ruleFlowGroup);

        runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings);

        verify(ruleScenarioExecutableBuilderMock, times(1)).setActiveRuleFlowGroup(ruleFlowGroup);

        verify(ruleScenarioExecutableBuilderMock, times(inputObjects)).insert(insertCaptor.capture());
        for (Object value : insertCaptor.getAllValues()) {
            assertTrue(value instanceof Person || value instanceof Dispute);
        }

        verify(ruleScenarioExecutableBuilderMock, times(1)).addInternalCondition(eq(Person.class), any(), any());
        verify(ruleScenarioExecutableBuilderMock, times(1)).run();

        assertEquals(1, scenarioRunnerData.getResults().size());

        // test not rule error
        settings.setType(ScenarioSimulationModel.Type.DMN);
        assertThatThrownBy(() -> runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to run");
    }

    private void commonAddMessageString(List<String> ruleNames, List<String> expectedMessages) {
        ruleNames.forEach(ruleName ->
                                  IntStream.range(1, 3).forEach(index -> expectedMessages.add(ruleName)));
    }
}