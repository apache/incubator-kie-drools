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
package org.drools.scenariosimulation.backend.runner;

import java.util.ArrayList;
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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;
import static org.drools.scenariosimulation.api.model.FactMappingValueStatus.FAILED_WITH_EXCEPTION;
import static org.drools.scenariosimulation.api.model.FactMappingValueStatus.SUCCESS;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.COVERAGE_LISTENER;
import static org.drools.scenariosimulation.backend.fluent.RuleScenarioExecutableBuilder.RULES_AVAILABLE;
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
    public void extractGivenValues_scenario1() {
        List<InstanceGiven> scenario1Inputs = extractGivenValuesForScenario1();
        
        assertThat(scenario1Inputs).hasSize(1);

    }

    @Test
    public void extractGivenValues_scenario2() {
        List<InstanceGiven> scenario2Inputs = extractGivenValuesForScenario2();
        
        assertThat(scenario2Inputs).hasSize(2);

    }
    
    @Test
    public void extractGivenValues_scenario2_addOrUpdateMappingValue() {
        // add expression
        scenario2.addOrUpdateMappingValue(disputeFactIdentifier, expressionGivenExpressionIdentifier, "# new org.drools.scenariosimulation.backend.model.Dispute(\"dispute description\", 10)");

        List<InstanceGiven> scenario2Inputs = extractGivenValuesForScenario2();
        
        assertThat(scenario2Inputs).hasSize(2);
        Optional<Dispute> disputeGivenOptional = scenario2Inputs.stream()
                .filter(elem -> elem.getValue() instanceof Dispute)
                .map(elem -> (Dispute) elem.getValue())
                .findFirst();
        assertThat(disputeGivenOptional).isPresent();
        assertThat(disputeGivenOptional.get().getDescription()).isEqualTo("dispute description");
    }
    
    @Test
    public void extractGivenValues_scenario2_addOrUpdateMappingValue_wrongValue() {
        scenario2.addOrUpdateMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, "WrongValue");

        assertThatThrownBy(() -> extractGivenValuesForScenario2())
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Error in GIVEN data");
    }    


    @Test
    public void extractExpectedValues_scenario1() {
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertThat(scenario1Outputs).hasSize(1);
    }
    
    @Test
    public void extractExpectedValues_scenario2_addOrUpdateMappingValue() {
        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.EXPECT),
                                          "TEST");
        
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
    
        assertThat(scenario2Outputs).hasSize(3);
        assertThat(scenario2Outputs.stream().filter(ScenarioExpect::isNewFact).count()).isEqualTo(1);
    }    
    
    @Test
    public void extractExpectedValues_scenario2_addOrUpdateMappingValue_givenAndExpect() {
        /* A Given "TEST" fact with null rawValue should works as the previous case, i.e. to not consider the GIVEN fact with empty data */
        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.EXPECT),
                                          "TEST");
        scenario2.addOrUpdateMappingValue(FactIdentifier.create("TEST", String.class.getCanonicalName()),
                                          ExpressionIdentifier.create("TEST", FactMappingType.GIVEN),
                                          null);

        List<ScenarioExpect> scenario2aOutputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());
        
        assertThat(scenario2aOutputs).hasSize(3);
        assertThat(scenario2aOutputs.stream().filter(ScenarioExpect::isNewFact).count()).isEqualTo(1);
    }

    @Test
    public void verifyConditions_scenario1() {
        List<InstanceGiven> scenario1Inputs = extractGivenValuesForScenario1();
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData1 = new ScenarioRunnerData();
        scenario1Inputs.forEach(scenarioRunnerData1::addGiven);
        scenario1Outputs.forEach(scenarioRunnerData1::addExpect);

        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData1,
                                      expressionEvaluatorFactory,
                                      null);
        assertThat(scenarioRunnerData1.getResults()).hasSize(1);
    }
    
    @Test
    public void verifyConditions_scenario2() {
        List<InstanceGiven> scenario2Inputs = extractGivenValuesForScenario2();
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        ScenarioRunnerData scenarioRunnerData2 = new ScenarioRunnerData();
        scenario2Inputs.forEach(scenarioRunnerData2::addGiven);
        scenario2Outputs.forEach(scenarioRunnerData2::addExpect);

        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData2,
                                      expressionEvaluatorFactory,
                                      null);

        assertThat(scenarioRunnerData2.getResults()).hasSize(2);
    }

    @Test
    public void getScenarioResultsTest() {
        List<InstanceGiven> scenario1Inputs = extractGivenValuesForScenario1();
        List<ScenarioExpect> scenario1Outputs = runnerHelper.extractExpectedValues(scenario1.getUnmodifiableFactMappingValues());

        assertThat(scenario1Inputs).isNotEmpty();

        InstanceGiven input1 = scenario1Inputs.get(0);

        scenario1Outputs = scenario1Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input1.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario1Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario1Outputs, input1, expressionEvaluatorFactory);

        assertThat(scenario1Results).hasSize(1);
        assertThat(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus()).isEqualTo(SUCCESS);

        List<InstanceGiven> scenario2Inputs = extractGivenValuesForScenario2();
        List<ScenarioExpect> scenario2Outputs = runnerHelper.extractExpectedValues(scenario2.getUnmodifiableFactMappingValues());

        assertThat(scenario2Inputs).isNotEmpty();

        InstanceGiven input2 = scenario2Inputs.get(0);

        scenario2Outputs = scenario2Outputs.stream().filter(elem -> elem.getFactIdentifier().equals(input2.getFactIdentifier())).collect(toList());
        List<ScenarioResult> scenario2Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario2Outputs, input2, expressionEvaluatorFactory);

        assertThat(scenario2Results).hasSize(1);
        assertThat(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus()).isEqualTo(SUCCESS);

        List<ScenarioExpect> newFact = List.of(new ScenarioExpect(personFactIdentifier, List.of(), true));
        List<ScenarioResult> scenario2NoResults = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), newFact, input2, expressionEvaluatorFactory);

        assertThat(scenario2NoResults).hasSize(0);

        Person person = new Person();
        person.setFirstName("ANOTHER STRING");
        InstanceGiven newInput = new InstanceGiven(personFactIdentifier, person);

        List<ScenarioResult> scenario3Results = runnerHelper.getScenarioResultsFromGivenFacts(simulation.getScesimModelDescriptor(), scenario1Outputs, newInput, expressionEvaluatorFactory);
        assertThat(scenario1Outputs.get(0).getExpectedResult().get(0).getStatus()).isEqualTo(FactMappingValueStatus.FAILED_WITH_ERROR);

        assertThat(scenario3Results).hasSize(1);
        assertThat(scenario3Results.get(0).getResultValue().get()).isEqualTo(person.getFirstName());
        assertThat(scenario3Results.get(0).getFactMappingValue().getRawValue()).isEqualTo("NAME");
    }
 
    @Test
    public void validateAssertionTest() {
        List<ScenarioResult> scenarioFailResult = new ArrayList<>();
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail("Unexpected execution path");
        } catch (IllegalStateException exception) {
            assertThat(exception.getMessage()).isEqualTo("Illegal FactMappingValue status");
        }

        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setErrorValue("Error");
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail("Unexpected execution path");
        } catch (ScenarioException exception) {
            assertThat(exception.isFailedAssertion()).isTrue();
            assertThat(exception.getMessage()).isEqualTo(ScenarioSimulationServerMessages.getFactWithWrongValueExceptionMessage("Fact 2.amount",
                    amountNameExpectedFactMappingValue.getRawValue(),
                    amountNameExpectedFactMappingValue.getErrorValue()));
        }

        String exceptionMessage = "Message";
        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setExceptionMessage(exceptionMessage);
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail("Unexpected execution path");
        } catch (ScenarioException exception) {
            assertThat(exception.isFailedAssertion()).isFalse();
            assertThat(exception.getMessage()).isEqualTo(ScenarioSimulationServerMessages.getGenericScenarioExceptionMessage(exceptionMessage));
        }

        List<String> pathToValue = List.of("Item #2");
        amountNameExpectedFactMappingValue.resetStatus();
        amountNameExpectedFactMappingValue.setCollectionPathToValue(pathToValue);
        scenarioFailResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, "SOMETHING_ELSE"));
        try {
            runnerHelper.validateAssertion(scenarioFailResult, simulation.getScesimModelDescriptor());
            fail("Unexpected execution path");
        } catch (ScenarioException exception) {
            assertThat(exception.isFailedAssertion()).isTrue();
            assertThat(exception.getMessage()).isEqualTo(ScenarioSimulationServerMessages.getCollectionFactExceptionMessage("Fact 2.amount",
                    pathToValue,
                    amountNameExpectedFactMappingValue.getErrorValue()));
        }

        List<ScenarioResult> scenarioSuccessResult = new ArrayList<>();
        scenarioSuccessResult.add(new ScenarioResult(amountNameExpectedFactMappingValue, amountNameExpectedFactMappingValue.getRawValue()).setResult(true));
        runnerHelper.validateAssertion(scenarioSuccessResult, simulation.getScesimModelDescriptor());
    }

    @Test
    public void groupByFactIdentifierAndFilterTest() {
        Map<FactIdentifier, List<FactMappingValue>> scenario1Given = runnerHelper.groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        assertThat(scenario1Given).hasSize(1);
        assertThat(scenario1Given.get(personFactIdentifier)).hasSize(1);

        Map<FactIdentifier, List<FactMappingValue>> scenario1Expected = runnerHelper.groupByFactIdentifierAndFilter(scenario1.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);
        assertThat(scenario1Expected).hasSize(1);
        assertThat(scenario1Expected.get(personFactIdentifier)).hasSize(1);

        Map<FactIdentifier, List<FactMappingValue>> scenario2Given = runnerHelper.groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN);
        assertThat(scenario2Given).hasSize(2);
        assertThat(scenario2Given.get(disputeFactIdentifier)).hasSize(1);
        
        Map<FactIdentifier, List<FactMappingValue>> scenario2Expected = runnerHelper.groupByFactIdentifierAndFilter(scenario2.getUnmodifiableFactMappingValues(), FactMappingType.EXPECT);
        assertThat(scenario2Expected).hasSize(2);
        assertThat(scenario2Expected.get(disputeFactIdentifier)).hasSize(1);

        Scenario scenario = new Scenario();
        scenario.addMappingValue(FactIdentifier.EMPTY, ExpressionIdentifier.DESCRIPTION, null);
        assertThat(runnerHelper.groupByFactIdentifierAndFilter(scenario.getUnmodifiableFactMappingValues(), FactMappingType.GIVEN)).hasSize(0);
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
    public void createExtractorFunction() {
        String personName = "Test";
        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, personName);
        Function<Object, ValueWrapper> extractorFunction = runnerHelper.createExtractorFunction(expressionEvaluator, factMappingValue, simulation.getScesimModelDescriptor());
        Person person = new Person();

        person.setFirstName(personName);
        assertThat(extractorFunction.apply(person).isValid()).isTrue();

        person.setFirstName("OtherString");
        assertThat(extractorFunction.apply(person).isValid()).isFalse();

        Function<Object, ValueWrapper> extractorFunction1 = runnerHelper.createExtractorFunction(expressionEvaluator,
                                                                                                  new FactMappingValue(personFactIdentifier,
                                                                                                                       firstNameGivenExpressionIdentifier,
                                                                                                                       null),
                                                                                                  simulation.getScesimModelDescriptor());
        ValueWrapper nullValue = extractorFunction1.apply(new Person());
        assertThat(nullValue.isValid()).isTrue();
        assertThat(nullValue.getValue()).isNull();
    }

    @Test
    public void getParamsForBean() {
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
        
        assertThat(factMappingValues).extracting(x -> x.getStatus()).containsExactly(FAILED_WITH_EXCEPTION, FAILED_WITH_EXCEPTION, SUCCESS);
    }

    @Test
    public void getDirectMapping() {
        assertThat(runnerHelper.getDirectMapping(Map.of(List.of(), 1)).getValue()).isEqualTo(1);

        Map<List<String>, Object> paramsToSet = new HashMap<>();
        paramsToSet.put(List.of(), null);

        assertThat(runnerHelper.getDirectMapping(paramsToSet).getValue()).isNull();

        ValueWrapper<Object> directMapping = runnerHelper.getDirectMapping(Map.of());
        assertThat(directMapping.isValid()).isFalse();
        assertThat(directMapping.getErrorMessage().get()).isEqualTo("No direct mapping available");
    }

    @Test
    public void createObject() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(List.of("firstName"), "TestName");
        params.put(List.of("age"), 10);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(initialInstance, Person.class.getCanonicalName(), params, getClass().getClassLoader());
        assertThat(objectRaw).isInstanceOf(Person.class);

        Person object = (Person) objectRaw;
        assertThat(object.getAge()).isEqualTo(10);
        assertThat(object.getFirstName()).isEqualTo("TestName");
    }

    @Test
    public void createObjectDirectMappingSimpleType() {
        Map<List<String>, Object> params = new HashMap<>();
        String directMappingSimpleTypeValue = "TestName";
        params.put(List.of(), directMappingSimpleTypeValue);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(initialInstance, String.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isInstanceOf(String.class).isEqualTo(directMappingSimpleTypeValue);
    }

    @Test
    public void createObjectDirectMappingSimpleTypeNull() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(List.of(), null);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject( initialInstance, String.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isNull();
    }
    
    @Test
    public void createObjectDirectMappingComplexType() {
        Map<List<String>, Object> params = new HashMap<>();
        Person directMappingComplexTypeValue = new Person();
        directMappingComplexTypeValue.setFirstName("TestName");
        params.put(List.of(), directMappingComplexTypeValue);
        params.put(List.of("age"), 10);

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject( initialInstance, Map.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isInstanceOf(Person.class);

        Person object = (Person) objectRaw;
        assertThat(object.getAge()).isEqualTo(10);
        assertThat(object.getFirstName()).isEqualTo("TestName");
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

        assertThat(scenarioResultMetadata.getScenarioWithIndex()).isEqualTo(scenarioWithIndexMock);
        assertThat(scenarioResultMetadata.getAvailable()).hasSize(2);
        assertThat(scenarioResultMetadata.getExecuted()).hasSize(2);
        assertThat(scenarioResultMetadata.getExecutedWithCounter()).containsEntry("rule1", 2).containsEntry("rule2", 2);
        List<String> expectedMessages = new ArrayList<>();
        commonAddMessageString(List.of("rule1", "rule2"), expectedMessages);

        final List<AuditLogLine> auditLogLines = scenarioResultMetadata.getAuditLogLines();
        assertThat(auditLogLines).hasSameSizeAs(expectedMessages);
        for (int i = 0; i < expectedMessages.size(); i++) {
            commonCheckAuditLogLine(auditLogLines.get(i), expectedMessages.get(i), ConstantsHolder.EXECUTED);
        }
    }

    @Test
    public void extractBackgroundValues() {
        // TEST 0 - empty background
        Background emptyBackground = new Background();
        List<InstanceGiven> emptyBackgroundGivens = runnerHelper.extractBackgroundValues(emptyBackground,
                                                                                         classLoader,
                                                                                         expressionEvaluatorFactory);
        assertThat(emptyBackgroundGivens).hasSize(0);

        emptyBackground.addData();
        emptyBackgroundGivens = runnerHelper.extractBackgroundValues(emptyBackground,
                                                                     classLoader,
                                                                     expressionEvaluatorFactory);
        assertThat(emptyBackgroundGivens).hasSize(0);

        // TEST 1 - background correct
        List<InstanceGiven> backgroundGivens = runnerHelper.extractBackgroundValues(this.background,
                                                                                    classLoader,
                                                                                    expressionEvaluatorFactory);
        assertThat(backgroundGivens).hasSize(3);
        for (InstanceGiven backgroundGiven : backgroundGivens) {
            if (backgroundGiven.getFactIdentifier().equals(personFactIdentifier)) {
                assertThat(backgroundGiven.getFactIdentifier()).isEqualTo(personFactIdentifier);
                Person person = (Person) backgroundGiven.getValue();
                assertThat(person.getFirstName()).isEqualTo(NAME);
            } else if (backgroundGiven.getFactIdentifier().equals(disputeFactIdentifier)) {
                assertThat(backgroundGiven.getFactIdentifier()).isEqualTo(disputeFactIdentifier);
                Dispute dispute = (Dispute) backgroundGiven.getValue();
                double parsedAmount = Double.parseDouble(AMOUNT);
                assertThat(dispute.getAmount()).isCloseTo(parsedAmount, within(0.1));
            } else {
                fail("Unexpected execution path");
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

        assertThat(notValid1.getStatus()).isEqualTo(FAILED_WITH_EXCEPTION);
        assertThat(notValid1.getExceptionMessage()).startsWith("Impossible to parse");
        assertThat(notValid2.getStatus()).isEqualTo(FAILED_WITH_EXCEPTION);
        assertThat(notValid2.getExceptionMessage()).startsWith("Impossible to parse");
    }

    @Test
    public void executeScenario() {
        ArgumentCaptor<Object> insertCaptor = ArgumentCaptor.forClass(Object.class);

        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addBackground(new InstanceGiven(personFactIdentifier, new Person()));
        scenarioRunnerData.addBackground(new InstanceGiven(disputeFactIdentifier, new Dispute()));
        scenarioRunnerData.addGiven(new InstanceGiven(personFactIdentifier, new Person()));
        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(factMappingValue), false));
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(factMappingValue), true));

        int inputObjects = scenarioRunnerData.getBackgrounds().size() + scenarioRunnerData.getGivens().size();

        String ruleFlowGroup = "ruleFlowGroup";
        settings.setRuleFlowGroup(ruleFlowGroup);

        runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings);

        verify(ruleScenarioExecutableBuilderMock, times(1)).setActiveRuleFlowGroup(ruleFlowGroup);

        verify(ruleScenarioExecutableBuilderMock, times(inputObjects)).insert(insertCaptor.capture());
        for (Object value : insertCaptor.getAllValues()) {
            assertThat(value instanceof Person || value instanceof Dispute).isTrue();
        }

        verify(ruleScenarioExecutableBuilderMock, times(1)).addInternalCondition(eq(Person.class), any(), any());
        verify(ruleScenarioExecutableBuilderMock, times(1)).run();

        assertThat(scenarioRunnerData.getResults()).hasSize(1);

        // test not rule error
        settings.setType(ScenarioSimulationModel.Type.DMN);
        assertThatThrownBy(() -> runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to run");
    }

    private void commonAddMessageString(List<String> ruleNames, List<String> expectedMessages) {
        ruleNames.forEach(ruleName -> IntStream.range(1, 3).forEach(index -> expectedMessages.add(ruleName)));
    }
    

	private List<InstanceGiven> extractGivenValuesForScenario1() {
		return runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                              scenario1.getUnmodifiableFactMappingValues(),
                                                                              classLoader,
                                                                              expressionEvaluatorFactory);
	}

	private List<InstanceGiven> extractGivenValuesForScenario2() {
		return runnerHelper.extractGivenValues(simulation.getScesimModelDescriptor(),
                                                                 scenario2.getUnmodifiableFactMappingValues(),
                                                                 classLoader,
                                                                 expressionEvaluatorFactory);
	}
    
}