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

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.expression.DMNFeelExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluator;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.fluent.DMNScenarioExecutableBuilder;
import org.drools.scenariosimulation.backend.runner.model.InstanceGiven;
import org.drools.scenariosimulation.backend.runner.model.ScenarioExpect;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResultMetadata;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.impl.DMNMessageImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.IMPORTED_PREFIX;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.drools.scenariosimulation.backend.TestUtils.getRandomlyGeneratedDMNMessageList;
import static org.kie.dmn.api.core.DMNMessage.Severity.ERROR;
import static org.kie.dmn.api.core.DMNMessage.Severity.WARN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNScenarioRunnerHelperTest {

    private static final String NAME = "NAME";
    private static final String FEEL_EXPRESSION_NAME = "\"" + NAME + "\"";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(10);
    private static final String DMN_FILE_PATH = "dmnFilePath";
    private static final String TEST_DESCRIPTION = "Test description";
    private static final ClassLoader classLoader = RuleScenarioRunnerHelperTest.class.getClassLoader();
    private static final ExpressionEvaluatorFactory expressionEvaluatorFactory = ExpressionEvaluatorFactory.create(classLoader, ScenarioSimulationModel.Type.DMN);
    private static final ExpressionEvaluator expressionEvaluator = new DMNFeelExpressionEvaluator(classLoader);
    private final DMNScenarioRunnerHelper runnerHelper = new DMNScenarioRunnerHelper() {
        @Override
        protected DMNScenarioExecutableBuilder createBuilderWrapper(KieContainer kieContainer) {
            return dmnScenarioExecutableBuilderMock;
        }
    };
    @Mock
    protected Map<String, Object> requestContextMock;
    @Mock
    protected DMNResult dmnResultMock;
    @Mock
    protected DMNDecisionResult dmnDecisionResultMock;
    @Mock
    protected DMNModel dmnModelMock;
    @Mock
    protected DMNScenarioExecutableBuilder dmnScenarioExecutableBuilderMock;
    @Mock
    protected KieContainer kieContainerMock;
    @Captor
    private ArgumentCaptor<Object> valueCaptor;
    @Captor
    private ArgumentCaptor<String> keyCaptor;

    private Simulation simulation;
    private Settings settings;
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
    private FactMappingValue firstNameExpectedValue;

    @Before
    public void init() {
        when(dmnScenarioExecutableBuilderMock.run()).thenReturn(mock(RequestContext.class));

        simulation = new Simulation();
        settings = new Settings();
        settings.setType(ScenarioSimulationModel.Type.DMN);
        settings.setDmnFilePath(DMN_FILE_PATH);
        personFactIdentifier = FactIdentifier.create("Fact 1", "Fact 1");
        firstNameGivenExpressionIdentifier = ExpressionIdentifier.create("First Name Given", FactMappingType.GIVEN);
        firstNameGivenFactMapping = simulation.getScesimModelDescriptor().addFactMapping(personFactIdentifier, firstNameGivenExpressionIdentifier);
        firstNameGivenFactMapping.addExpressionElement("Fact 1", String.class.getCanonicalName());
        firstNameGivenFactMapping.addExpressionElement("firstName", String.class.getCanonicalName());

        disputeFactIdentifier = FactIdentifier.create("Fact 2", "Fact 2");
        amountGivenExpressionIdentifier = ExpressionIdentifier.create("Amount Given", FactMappingType.GIVEN);
        amountNameGivenFactMapping = simulation.getScesimModelDescriptor().addFactMapping(disputeFactIdentifier, amountGivenExpressionIdentifier);
        amountNameGivenFactMapping.addExpressionElement("Fact 2", BigDecimal.class.getCanonicalName());
        amountNameGivenFactMapping.addExpressionElement("amount", BigDecimal.class.getCanonicalName());

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
        scenario1.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, FEEL_EXPRESSION_NAME);
        firstNameExpectedValue = scenario1.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, FEEL_EXPRESSION_NAME);

        scenario2 = simulation.addData();
        scenario2.setDescription(TEST_DESCRIPTION);
        scenario2.addMappingValue(personFactIdentifier, firstNameGivenExpressionIdentifier, FEEL_EXPRESSION_NAME);
        scenario2.addMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, FEEL_EXPRESSION_NAME);
        scenario2.addMappingValue(disputeFactIdentifier, amountGivenExpressionIdentifier, AMOUNT);
        amountNameExpectedFactMappingValue = scenario2.addMappingValue(disputeFactIdentifier, amountExpectedExpressionIdentifier, AMOUNT);

        when(requestContextMock.get(DMNScenarioExecutableBuilder.DMN_RESULT)).thenReturn(dmnResultMock);
        when(requestContextMock.get(DMNScenarioExecutableBuilder.DMN_MODEL)).thenReturn(dmnModelMock);
    }

    @Test
    public void verifyConditions_noDecisionGeneratedForSpecificName() {
        // test 1 - no decision generated for specific decisionName
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));

        assertThatThrownBy(() -> runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData, expressionEvaluatorFactory, requestContextMock))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("DMN execution has not generated a decision result with name Fact 1");
    }
    
    @Test
    public void verifyConditions_decisionResultContainsANull() {
        // test 2 - when decisionResult contains a null value skip the steps and just do the comparison (that should be false in this case)
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));

        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);

        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData, expressionEvaluatorFactory, requestContextMock);

        assertThat(scenarioRunnerData.getResults()).hasSize(1);
        assertThat(scenarioRunnerData.getResults().get(0).getResult()).isFalse();

    }
    
    @Test
    public void verifyConditions_decisionResultIsNotNullButMalformed() {
        // test 3 - now result is not null but data structure is wrong (expected steps but data is a simple string)
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));
        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);
        when(dmnDecisionResultMock.getResult()).thenReturn("");

        assertThatThrownBy(() -> runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData, expressionEvaluatorFactory, requestContextMock))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Wrong resultRaw structure because it is not a complex type as expected");
    }

    @Test
    public void verifyConditions_checksArePerformed_fail() {
        // test 4 - check are performed (but fail)
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));
        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("firstName", "WrongValue");
        when(dmnDecisionResultMock.getResult()).thenReturn(resultMap);


        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData, expressionEvaluatorFactory, requestContextMock);

        assertThat(scenarioRunnerData.getResults()).hasSize(1);
        assertThat(scenarioRunnerData.getResults().get(0).getResult()).isFalse();
    }
    
    @Test
    public void verifyConditions_checksArePerformed_success() {
        // test 5 - check are performed (but success)
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));

        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("firstName", NAME);

        when(dmnDecisionResultMock.getResult()).thenReturn(resultMap);


        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData, expressionEvaluatorFactory, requestContextMock);

        assertThat(scenarioRunnerData.getResults()).hasSize(1);
        assertThat(scenarioRunnerData.getResults().get(0).getResult()).isTrue();
    }
    
    
    @Test
    public void verifyConditions_errorsAreReported() {
        // test 6 - verify that when expression evaluation fails the corresponding expression is marked as error
        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(firstNameExpectedValue)));
        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("firstName", NAME);

        when(dmnDecisionResultMock.getResult()).thenReturn(resultMap);

        ExpressionEvaluatorFactory expressionEvaluatorFactoryMock = mock(ExpressionEvaluatorFactory.class);
        when(expressionEvaluatorFactoryMock.getOrCreate(any())).thenReturn(mock(ExpressionEvaluator.class));
        
        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData,
                                      expressionEvaluatorFactoryMock,
                                      requestContextMock);

        assertThat(scenarioRunnerData.getResults().get(0).getFactMappingValue().getStatus()).isNotEqualTo(FactMappingValueStatus.SUCCESS);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createObject() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(List.of("creator", "name"), "TestName");
        params.put(List.of("creator", "surname"), "TestSurname");
        params.put(List.of("age"), BigDecimal.valueOf(10));
        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        
        Object objectRaw = runnerHelper.createObject(initialInstance, String.class.getCanonicalName(), params, getClass().getClassLoader());
        
        assertThat(objectRaw).isInstanceOf(Map.class);

        Map<String, Object> object = (Map<String, Object>) objectRaw;
        assertThat(object).containsEntry("age", BigDecimal.valueOf(10));
        assertThat(object.get("creator")).isInstanceOf(Map.class);

        Map<String, Object> creator = (Map<String, Object>) object.get("creator");
        assertThat(creator).containsEntry("name", "TestName").containsEntry("surname", "TestSurname");
    }

    @Test
    public void createObject_directMappingSimpleType() {
        Map<List<String>, Object> params = new HashMap<>();
        String directMappingSimpleTypeValue = "TestName";
        params.put(List.of(), directMappingSimpleTypeValue);
        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);

        Object objectRaw = runnerHelper.createObject(initialInstance, String.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isInstanceOf(String.class).isEqualTo(directMappingSimpleTypeValue);
    }

    @Test
    public void createObject_directMappingSimpleTypeNull() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(List.of(), null);
        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);

        Object objectRaw = runnerHelper.createObject(initialInstance, String.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createObject_directMappingComplexType() {
        Map<List<String>, Object> params = new HashMap<>();
        Map<String, Object> directMappingComplexTypeValue = new HashMap<>();
        directMappingComplexTypeValue.put("key1", "value1");
        params.put(List.of(), directMappingComplexTypeValue);
        params.put(List.of("key2"), "value2");

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        
        Object objectRaw = runnerHelper.createObject(initialInstance, Map.class.getCanonicalName(), params, getClass().getClassLoader());

        assertThat(objectRaw).isInstanceOf(Map.class);

        Map<String, Object> object = (Map<String, Object>) objectRaw;

        assertThat(object).containsEntry("key1","value1").containsEntry("key2", "value2");
    }

    @Test
    public void extractResultMetadata_noDecisionResultMessages() {
        commonExtractResultMetadata();
    }

    @Test
    public void extractResultMetadata_decisionResultMessages() {
        List<DMNMessage> messages = getRandomlyGeneratedDMNMessageList();
        commonExtractResultMetadata(messages);
    }

    @Test
    public void getSingleFactValueResult_failDecision() {
        DMNDecisionResult failedDecision = createDecisionResultMock("Test", false, new ArrayList<>());
        
        ValueWrapper<?> failedResult = runnerHelper.getSingleFactValueResult(null,
                                                                              null,
                                                                              failedDecision,
                                                                              null,
                                                                              expressionEvaluator);
        assertThat(failedResult.isValid()).isFalse();
        assertThat(failedResult.getErrorMessage().get()).isEqualTo("The decision \"" +
                failedDecision.getDecisionName() +
                "\" has not been successfully evaluated: " +
                failedDecision.getEvaluationStatus());
    }

    @Test
    public void getSingleFactValueResult_failDecisionWithMessages() {
        DMNMessage errorMessage = new DMNMessageImpl(ERROR, "DMN Internal Error", DMNMessageType.FEEL_EVALUATION_ERROR, null);
        DMNMessage warnMessage = new DMNMessageImpl(WARN, "DMN Internal Warn", DMNMessageType.FEEL_EVALUATION_ERROR, null);

        DMNDecisionResult failedDecision = createDecisionResultMock("Test", false, new ArrayList<>());
        
        ValueWrapper<?> failedResult = runnerHelper.getSingleFactValueResult(null,
                                                                             null,
                                                                             failedDecision,
                                                                             List.of(warnMessage, errorMessage),
                                                                             expressionEvaluator);
        assertThat(failedResult.isValid()).isFalse();
        assertThat(failedResult.getErrorMessage().get()).isEqualTo("The decision \"" +
                failedDecision.getDecisionName() +
                "\" has not been successfully evaluated: DMN Internal Error");
    }

    @Test
    public void executeScenario() {
        FactIdentifier bookFactIdentifier = FactIdentifier.create("Book", "Book");
        FactIdentifier importedPersonFactIdentifier = FactIdentifier.create(IMPORTED_PREFIX + ".Person", IMPORTED_PREFIX + ".Person", IMPORTED_PREFIX);
        FactIdentifier importedDisputeFactIdentifier = FactIdentifier.create(IMPORTED_PREFIX + ".Dispute", IMPORTED_PREFIX + ".Dispute", IMPORTED_PREFIX);
        FactIdentifier importedBookFactIdentifier = FactIdentifier.create(IMPORTED_PREFIX + ".Book", IMPORTED_PREFIX + ".Book", IMPORTED_PREFIX);
        FactIdentifier importedWrBookFactIdentifier = FactIdentifier.create(IMPORTED_PREFIX + ".wr.Book", IMPORTED_PREFIX + ".wr.Book", IMPORTED_PREFIX);

        AbstractMap.SimpleEntry<String, Object> backgroundDisputeFactData = new AbstractMap.SimpleEntry<>("description", "Nice");
        AbstractMap.SimpleEntry<String, Object> backgroundPersonFactData = new AbstractMap.SimpleEntry<>("name", "Carl");
        AbstractMap.SimpleEntry<String, Object> backgroundPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 2);
        AbstractMap.SimpleEntry<String, Object> backgroundImportedDisputeFactData = new AbstractMap.SimpleEntry<>("description", "Bad");
        AbstractMap.SimpleEntry<String, Object> backgroundImportedPersonFactData = new AbstractMap.SimpleEntry<>("name", "Max");
        AbstractMap.SimpleEntry<String, Object> backgroundImportedPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 34);

        AbstractMap.SimpleEntry<String, Object> givenPersonFactData = new AbstractMap.SimpleEntry<>("surname", "Brown");
        AbstractMap.SimpleEntry<String, Object> givenPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 23);
        AbstractMap.SimpleEntry<String, Object> givenBookFactData = new AbstractMap.SimpleEntry<>("Author", "Resey Rema");
        AbstractMap.SimpleEntry<String, Object> givenBookFactData2 = new AbstractMap.SimpleEntry<>("Name", "The mighty Test Scenario!");
        AbstractMap.SimpleEntry<String, Object> givenImportedBookFactData = new AbstractMap.SimpleEntry<>("Author", "Mr Y");
        AbstractMap.SimpleEntry<String, Object> givenImportedBookFactData2 = new AbstractMap.SimpleEntry<>("Title", "The awesome Test Scenario!");
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData = new AbstractMap.SimpleEntry<>("surname", "White");
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 67);
        AbstractMap.SimpleEntry<String, Object> givenImportedWrBookFactData = new AbstractMap.SimpleEntry<>("Title", "I hate name with multi dots");

        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        scenarioRunnerData.addBackground(new InstanceGiven(disputeFactIdentifier, Map.ofEntries(backgroundDisputeFactData)));
        scenarioRunnerData.addBackground(new InstanceGiven(personFactIdentifier, Map.ofEntries(backgroundPersonFactData, backgroundPersonFactData2)));
        scenarioRunnerData.addBackground(new InstanceGiven(importedPersonFactIdentifier, Map.ofEntries(backgroundImportedPersonFactData, backgroundImportedPersonFactData2)));
        scenarioRunnerData.addBackground(new InstanceGiven(importedDisputeFactIdentifier, Map.ofEntries(backgroundImportedDisputeFactData)));

        scenarioRunnerData.addGiven(new InstanceGiven(personFactIdentifier, Map.ofEntries(givenPersonFactData, givenPersonFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedPersonFactIdentifier, Map.ofEntries(givenImportedPersonFactData, givenImportedPersonFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(bookFactIdentifier, Map.ofEntries(givenBookFactData, givenBookFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedBookFactIdentifier, Map.ofEntries(givenImportedBookFactData, givenImportedBookFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedWrBookFactIdentifier, Map.ofEntries(givenImportedWrBookFactData)));

        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(factMappingValue), false));
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, List.of(factMappingValue), true));

        List<String> expectedInputDataToLoad = List.of(personFactIdentifier.getName(), disputeFactIdentifier.getName(), bookFactIdentifier.getName(), IMPORTED_PREFIX);
        int inputObjects = expectedInputDataToLoad.size();

        runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings);

        verify(dmnScenarioExecutableBuilderMock, times(1)).setActiveModel(DMN_FILE_PATH);
        verify(dmnScenarioExecutableBuilderMock, times(inputObjects)).setValue(keyCaptor.capture(), valueCaptor.capture());
        assertThat(keyCaptor.getAllValues()).containsAll(expectedInputDataToLoad);
        for (int i = 0; i < inputObjects; i++) {
            String key = keyCaptor.getAllValues().get(i);
            Map<String, Object> value = (Map<String, Object>) valueCaptor.getAllValues().get(i);
            if (personFactIdentifier.getName().equals(key)) {
                assertThat(value).hasSize(3).contains(backgroundPersonFactData, givenPersonFactData, givenPersonFactData2);
            } else if (disputeFactIdentifier.getName().equals(key)) {
                assertThat(value).hasSize(1).contains(backgroundDisputeFactData);
            } else if (bookFactIdentifier.getName().equals(key)) {
                assertThat(value).hasSize(2).contains(givenBookFactData, givenBookFactData2);
            } else if (IMPORTED_PREFIX.equals(key)) {
                assertThat(value).hasSize(4);
                
                Map<String, Object> subValueDispute = (Map<String, Object>) value.get("Dispute");
                assertThat(subValueDispute).hasSize(1).contains(backgroundImportedDisputeFactData);
                
                Map<String, Object> subValueBook = (Map<String, Object>) value.get("Book");
                assertThat(subValueBook).hasSize(2).contains(givenImportedBookFactData, givenImportedBookFactData2);
                
                Map<String, Object> subValuePerson = (Map<String, Object>) value.get("Person");
                assertThat(subValuePerson).hasSize(3).contains(backgroundImportedPersonFactData, givenImportedPersonFactData, givenImportedPersonFactData2);
                
                Map<String, Object> subValueWrBook = (Map<String, Object>) value.get("wr.Book");
                assertThat(subValueWrBook).hasSize(1).contains(givenImportedWrBookFactData);
            } else {
                fail("Unexpected key: " + key);
            }
        }

        verify(dmnScenarioExecutableBuilderMock, times(1)).run();

        // test not rule error
        settings.setType(ScenarioSimulationModel.Type.RULE);
        assertThatThrownBy(() -> runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings))
                .isInstanceOf(ScenarioException.class)
                .hasMessageStartingWith("Impossible to run");
    }

    @Test
    public void validateScenario_wrongImportPrefix() {
        String wrongPrefix = "WrongPrefix";
        FactIdentifier importedPersonFactIdentifier = FactIdentifier.create(IMPORTED_PREFIX + ".Person", IMPORTED_PREFIX + ".Person", wrongPrefix);

        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData = new AbstractMap.SimpleEntry<>("surname", "White");
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 67);
        scenarioRunnerData.addGiven(new InstanceGiven(importedPersonFactIdentifier, Map.ofEntries(givenImportedPersonFactData, givenImportedPersonFactData2)));

        assertThatThrownBy(() -> runnerHelper.executeScenario(kieContainerMock,
                                                              scenarioRunnerData,
                                                              expressionEvaluatorFactory,
                                                              simulation.getScesimModelDescriptor(),
                                                              settings))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fact name: " + IMPORTED_PREFIX + ".Person has defined an invalid import prefix: " + wrongPrefix);
    }

    /* If a malicious user injects a regex as a prefix in both importPrefix and as a part of fact name, the injected
       regex shouldn't be applied, but it should be used as a plain string */
    @Test
    public void validateUnSecureImportPrefix() {
        String injectedPrefix = "/.(a+)+$/";
        FactIdentifier importedPersonFactIdentifier = FactIdentifier.create(injectedPrefix + ".Person", injectedPrefix + ".Person", injectedPrefix);

        ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData = new AbstractMap.SimpleEntry<>("surname", "White");
        AbstractMap.SimpleEntry<String, Object> givenImportedPersonFactData2 = new AbstractMap.SimpleEntry<>("age", 67);
        scenarioRunnerData.addGiven(new InstanceGiven(importedPersonFactIdentifier, Map.ofEntries(givenImportedPersonFactData, givenImportedPersonFactData2)));

        List<String> expectedInputDataToLoad = List.of(injectedPrefix);
        int inputObjects = expectedInputDataToLoad.size();

        runnerHelper.executeScenario(kieContainerMock,
                                     scenarioRunnerData,
                                     expressionEvaluatorFactory,
                                     simulation.getScesimModelDescriptor(),
                                     settings);

        verify(dmnScenarioExecutableBuilderMock, times(inputObjects)).setValue(keyCaptor.capture(), valueCaptor.capture());
        assertThat(keyCaptor.getAllValues()).containsAll(expectedInputDataToLoad);

        String key = keyCaptor.getAllValues().get(0);
        Map<String, Object> value = (Map<String, Object>) valueCaptor.getAllValues().get(0);
        assertThat(key).isEqualTo(injectedPrefix);

        Map<String, Object> subValuePerson = (Map<String, Object>) value.get("Person");
        assertThat(subValuePerson).hasSize(2).contains(givenImportedPersonFactData, givenImportedPersonFactData2);
    }

    public void commonExtractResultMetadata(List<DMNMessage> messages) {
        Set<DecisionNode> decisions = new HashSet<>();
        IntStream.range(0, 5).forEach(index -> decisions.add(createDecisionMock("decision" + index)));
        when(dmnModelMock.getDecisions()).thenReturn(decisions);

        List<DMNDecisionResult> decisionResults = new ArrayList<>();
        decisionResults.add(createDecisionResultMock("decision2", true, messages));
        decisionResults.add(createDecisionResultMock("decision3", false, messages));

        when(dmnResultMock.getDecisionResults()).thenReturn(decisionResults);

        ScenarioWithIndex scenarioWithIndex = new ScenarioWithIndex(1, scenario1);
        ScenarioResultMetadata scenarioResultMetadata = runnerHelper.extractResultMetadata(requestContextMock, scenarioWithIndex);

        assertThat(scenarioResultMetadata.getScenarioWithIndex()).isEqualTo(scenarioWithIndex);
        assertThat(scenarioResultMetadata.getAvailable()).hasSize(5).contains("decision1");
        assertThat(scenarioResultMetadata.getExecuted()).hasSize(1).contains("decision2").doesNotContain("decision3");
        
        final List<AuditLogLine> auditLogLines = scenarioResultMetadata.getAuditLogLines();
        assertThat(auditLogLines).isNotNull();

        List<String> expectedDecisions = List.of("decision2", "decision3");
        List<String> expectedResults = List.of(DecisionEvaluationStatus.SUCCEEDED.toString(), DecisionEvaluationStatus.FAILED.toString());
        int expectedLines = messages.size() * expectedDecisions.size();
        assertThat(auditLogLines).hasSize(expectedLines);
        for (int i = 0; i < auditLogLines.size(); i++) {
            int messagesIndex = i < messages.size() ? i : i - messages.size();
            String decisionName = i < messages.size() ? expectedDecisions.get(0) : expectedDecisions.get(1);
            String expectedResultName = i < messages.size() ? expectedResults.get(0) : expectedResults.get(1);
            commonCheckAuditLogLine(auditLogLines.get(i), decisionName, expectedResultName, messages.get(messagesIndex).getLevel().name() + ": " + messages.get(messagesIndex).getText());
        }
    }

    public void commonExtractResultMetadata() {
        Set<DecisionNode> decisions = new HashSet<>();
        IntStream.range(0, 5).forEach(index -> decisions.add(createDecisionMock("decision" + index)));
        when(dmnModelMock.getDecisions()).thenReturn(decisions);

        List<DMNDecisionResult> decisionResults = new ArrayList<>();
        decisionResults.add(createDecisionResultMock("decision2", true, null));
        decisionResults.add(createDecisionResultMock("decision3", false, null));

        when(dmnResultMock.getDecisionResults()).thenReturn(decisionResults);

        ScenarioWithIndex scenarioWithIndex = new ScenarioWithIndex(1, scenario1);
        ScenarioResultMetadata scenarioResultMetadata = runnerHelper.extractResultMetadata(requestContextMock, scenarioWithIndex);

        assertThat(scenarioResultMetadata.getScenarioWithIndex()).isEqualTo(scenarioWithIndex);
        assertThat(scenarioResultMetadata.getAvailable()).hasSize(5).contains("decision1");
        assertThat(scenarioResultMetadata.getExecuted()).hasSize(1).contains("decision2").doesNotContain("decision3");
        
        final List<AuditLogLine> auditLogLines = scenarioResultMetadata.getAuditLogLines();
        assertThat(auditLogLines).isNotNull().hasSameSizeAs(decisionResults);
        for (int i = 0; i < decisionResults.size(); i++) {
            commonCheckAuditLogLine(auditLogLines.get(i), decisionResults.get(i).getDecisionName(), decisionResults.get(i).getEvaluationStatus().name());
        }
    }
    
    
    private DecisionNode createDecisionMock(String decisionName) {
        DecisionNode decisionMock = mock(DecisionNode.class);
        when(decisionMock.getName()).thenReturn(decisionName);
        return decisionMock;
    }

    private DMNDecisionResult createDecisionResultMock(String decisionName, boolean success, List<DMNMessage> messages) {
        DMNDecisionResult decisionResultMock = mock(DMNDecisionResult.class);
        when(decisionResultMock.getDecisionName()).thenReturn(decisionName);
        when(decisionResultMock.getEvaluationStatus())
                .thenReturn(success ? DecisionEvaluationStatus.SUCCEEDED :
                                    DecisionEvaluationStatus.FAILED);
        if (messages != null) {
            when(decisionResultMock.getMessages()).thenReturn(messages);
        }
        return decisionResultMock;
    }
}