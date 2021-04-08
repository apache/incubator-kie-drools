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

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.IMPORTED_PREFIX;
import static org.drools.scenariosimulation.backend.TestUtils.commonCheckAuditLogLine;
import static org.drools.scenariosimulation.backend.TestUtils.getRandomlyGeneratedDMNMessageList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
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
    public void verifyConditions() {
        ScenarioRunnerData scenarioRunnerData1 = new ScenarioRunnerData();
        scenarioRunnerData1.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(firstNameExpectedValue)));

        // test 1 - no decision generated for specific decisionName
        assertThatThrownBy(() -> runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData1, expressionEvaluatorFactory, requestContextMock))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("DMN execution has not generated a decision result with name Fact 1");

        when(dmnResultMock.getDecisionResultByName(anyString())).thenReturn(dmnDecisionResultMock);
        when(dmnDecisionResultMock.getEvaluationStatus()).thenReturn(DecisionEvaluationStatus.SUCCEEDED);

        // test 2 - when decisionResult contains a null value skip the steps and just do the comparison (that should be false in this case)
        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData1, expressionEvaluatorFactory, requestContextMock);

        assertEquals(1, scenarioRunnerData1.getResults().size());
        assertFalse(scenarioRunnerData1.getResults().get(0).getResult());

        when(dmnDecisionResultMock.getResult()).thenReturn("");

        // test 3 - now result is not null but data structure is wrong (expected steps but data is a simple string)
        assertThatThrownBy(() -> runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData1, expressionEvaluatorFactory, requestContextMock))
                .isInstanceOf(ScenarioException.class)
                .hasMessage("Wrong resultRaw structure because it is not a complex type as expected");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("firstName", "WrongValue");

        when(dmnDecisionResultMock.getResult()).thenReturn(resultMap);

        ScenarioRunnerData scenarioRunnerData2 = new ScenarioRunnerData();
        scenarioRunnerData2.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(firstNameExpectedValue)));

        // test 4 - check are performed (but fail)
        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData2, expressionEvaluatorFactory, requestContextMock);

        assertEquals(1, scenarioRunnerData2.getResults().size());
        assertFalse(scenarioRunnerData2.getResults().get(0).getResult());

        ScenarioRunnerData scenarioRunnerData3 = new ScenarioRunnerData();
        scenarioRunnerData3.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(firstNameExpectedValue)));
        resultMap.put("firstName", NAME);

        // test 5 - check are performed (but success)
        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(), scenarioRunnerData3, expressionEvaluatorFactory, requestContextMock);

        assertEquals(1, scenarioRunnerData3.getResults().size());
        assertTrue(scenarioRunnerData3.getResults().get(0).getResult());

        // test 6 - verify that when expression evaluation fails the corresponding expression is marked as error
        ExpressionEvaluatorFactory expressionEvaluatorFactoryMock = mock(ExpressionEvaluatorFactory.class);
        when(expressionEvaluatorFactoryMock.getOrCreate(any())).thenReturn(mock(ExpressionEvaluator.class));
        runnerHelper.verifyConditions(simulation.getScesimModelDescriptor(),
                                      scenarioRunnerData3,
                                      expressionEvaluatorFactoryMock,
                                      requestContextMock);
        assertEquals(FactMappingValueStatus.FAILED_WITH_ERROR, scenarioRunnerData3.getResults().get(0).getFactMappingValue().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createObject() {
        Map<List<String>, Object> params = new HashMap<>();
        params.put(asList("creator", "name"), "TestName");
        params.put(asList("creator", "surname"), "TestSurname");
        params.put(singletonList("age"), BigDecimal.valueOf(10));

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                String.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());
        assertTrue(objectRaw instanceof Map);

        Map<String, Object> object = (Map<String, Object>) objectRaw;
        assertEquals(BigDecimal.valueOf(10), object.get("age"));
        assertTrue(object.get("creator") instanceof Map);

        Map<String, Object> creator = (Map<String, Object>) object.get("creator");
        assertEquals("TestName", creator.get("name"));
        assertEquals("TestSurname", creator.get("surname"));
    }

    @Test
    public void createObjectDirectMappingSimpleType() {
        Map<List<String>, Object> params = new HashMap<>();
        String directMappingSimpleTypeValue = "TestName";
        params.put(emptyList(), directMappingSimpleTypeValue);

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
        Map<String, Object> directMappingComplexTypeValue = new HashMap<>();
        directMappingComplexTypeValue.put("key1", "value1");
        params.put(emptyList(), directMappingComplexTypeValue);
        params.put(singletonList("key2"), "value2");

        ValueWrapper<Object> initialInstance = runnerHelper.getDirectMapping(params);
        Object objectRaw = runnerHelper.createObject(
                initialInstance,
                Map.class.getCanonicalName(),
                params,
                this.getClass().getClassLoader());

        assertTrue(objectRaw instanceof Map);

        Map<String, Object> object = (Map<String, Object>) objectRaw;

        assertEquals("value1", object.get("key1"));
        assertEquals("value2", object.get("key2"));
    }

    @Test
    public void extractResultMetadataNoDecisionResultMessages() {
        commonExtractResultMetadata(null);
    }

    @Test
    public void extractResultMetadataDecisionResultMessages() {
        List<DMNMessage> messages = getRandomlyGeneratedDMNMessageList();
        commonExtractResultMetadata(messages);
    }

    @Test
    public void getSingleFactValueResultFailDecision() {
        DMNDecisionResult failedDecision = createDecisionResultMock("Test", false, new ArrayList<>());
        ValueWrapper<?> failedResult = runnerHelper.getSingleFactValueResult(null,
                                                                              null,
                                                                              failedDecision,
                                                                              expressionEvaluator);
        assertFalse(failedResult.isValid());
        assertEquals("The decision " +
                             failedDecision.getDecisionName() +
                             " has not been successfully evaluated: " +
                             failedDecision.getEvaluationStatus(),
                     failedResult.getErrorMessage().get());
    }

    @Test
    public void executeScenario() {
        ArgumentCaptor<Object> setValueCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> setKeyCaptor = ArgumentCaptor.forClass(String.class);

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
        scenarioRunnerData.addBackground(new InstanceGiven(disputeFactIdentifier, instantiateMap(backgroundDisputeFactData)));
        scenarioRunnerData.addBackground(new InstanceGiven(personFactIdentifier, instantiateMap(backgroundPersonFactData, backgroundPersonFactData2)));
        scenarioRunnerData.addBackground(new InstanceGiven(importedPersonFactIdentifier, instantiateMap(backgroundImportedPersonFactData, backgroundImportedPersonFactData2)));
        scenarioRunnerData.addBackground(new InstanceGiven(importedDisputeFactIdentifier, instantiateMap(backgroundImportedDisputeFactData)));

        scenarioRunnerData.addGiven(new InstanceGiven(personFactIdentifier, instantiateMap(givenPersonFactData, givenPersonFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedPersonFactIdentifier, instantiateMap(givenImportedPersonFactData, givenImportedPersonFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(bookFactIdentifier, instantiateMap(givenBookFactData, givenBookFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedBookFactIdentifier, instantiateMap(givenImportedBookFactData, givenImportedBookFactData2)));
        scenarioRunnerData.addGiven(new InstanceGiven(importedWrBookFactIdentifier, instantiateMap(givenImportedWrBookFactData)));

        FactMappingValue factMappingValue = new FactMappingValue(personFactIdentifier, firstNameExpectedExpressionIdentifier, NAME);
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(factMappingValue), false));
        scenarioRunnerData.addExpect(new ScenarioExpect(personFactIdentifier, singletonList(factMappingValue), true));

        List<String> expectedInputDataToLoad = asList(personFactIdentifier.getName(), disputeFactIdentifier.getName(), bookFactIdentifier.getName(), IMPORTED_PREFIX);
        int inputObjects = expectedInputDataToLoad.size();

        runnerHelper.executeScenario(kieContainerMock, scenarioRunnerData, expressionEvaluatorFactory, simulation.getScesimModelDescriptor(), settings);

        verify(dmnScenarioExecutableBuilderMock, times(1)).setActiveModel(DMN_FILE_PATH);
        verify(dmnScenarioExecutableBuilderMock, times(inputObjects)).setValue(setKeyCaptor.capture(), setValueCaptor.capture());
        assertTrue(setKeyCaptor.getAllValues().containsAll(expectedInputDataToLoad));
        for (int i = 0; i < inputObjects; i++) {
            String key = setKeyCaptor.getAllValues().get(i);
            Map<String, Object> value = (Map<String, Object>) setValueCaptor.getAllValues().get(i);
            if (personFactIdentifier.getName().equals(key)) {
                assertEquals(backgroundPersonFactData.getValue(), value.get(backgroundPersonFactData.getKey()));
                assertNotEquals(backgroundPersonFactData2.getValue(), value.get(backgroundPersonFactData2.getKey()));
                assertEquals(givenPersonFactData.getValue(), value.get(givenPersonFactData.getKey()));
                assertEquals(givenPersonFactData2.getValue(), value.get(givenPersonFactData2.getKey()));
                assertEquals(3, value.size());
            } else if (disputeFactIdentifier.getName().equals(key)) {
                assertEquals(backgroundDisputeFactData.getValue(), value.get(backgroundDisputeFactData.getKey()));
                assertEquals(1, value.size());
            } else if (bookFactIdentifier.getName().equals(key)) {
                assertEquals(givenBookFactData.getValue(), value.get(givenBookFactData.getKey()));
                assertEquals(givenBookFactData2.getValue(), value.get(givenBookFactData2.getKey()));
                assertEquals(2, value.size());
            } else if (IMPORTED_PREFIX.equals(key)) {
                Map<String, Object> subValueDispute = (Map<String, Object>) value.get("Dispute");
                assertEquals(backgroundImportedDisputeFactData.getValue(), subValueDispute.get(backgroundImportedDisputeFactData.getKey()));
                assertEquals(1, subValueDispute.size());
                Map<String, Object> subValueBook = (Map<String, Object>) value.get("Book");
                assertEquals(givenImportedBookFactData.getValue(), subValueBook.get(givenImportedBookFactData.getKey()));
                assertEquals(givenImportedBookFactData2.getValue(), subValueBook.get(givenImportedBookFactData2.getKey()));
                assertEquals(2, subValueBook.size());
                Map<String, Object> subValuePerson = (Map<String, Object>) value.get("Person");
                assertEquals(backgroundImportedPersonFactData.getValue(), subValuePerson.get(backgroundImportedPersonFactData.getKey()));
                assertNotEquals(backgroundImportedPersonFactData2.getValue(), subValuePerson.get(backgroundImportedPersonFactData2.getKey()));
                assertEquals(givenImportedPersonFactData.getValue(), subValuePerson.get(givenImportedPersonFactData.getKey()));
                assertEquals(givenImportedPersonFactData2.getValue(), subValuePerson.get(givenImportedPersonFactData2.getKey()));
                assertEquals(3, subValuePerson.size());
                Map<String, Object> subValueWrBook = (Map<String, Object>) value.get("wr.Book");
                assertEquals(givenImportedWrBookFactData.getValue(), subValueWrBook.get(givenImportedWrBookFactData.getKey()));
                assertEquals(1, subValueWrBook.size());
                assertEquals(4, value.size());
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

    private Map<String, Object> instantiateMap(AbstractMap.SimpleEntry<String, Object> ... entries) {
        return Arrays.asList(entries).stream().collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
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

        assertEquals(scenarioWithIndex, scenarioResultMetadata.getScenarioWithIndex());
        assertEquals(5, scenarioResultMetadata.getAvailable().size());
        assertTrue(scenarioResultMetadata.getAvailable().contains("decision1"));
        assertEquals(1, scenarioResultMetadata.getExecuted().size());
        assertTrue(scenarioResultMetadata.getExecuted().contains("decision2"));
        assertFalse(scenarioResultMetadata.getExecuted().contains("decision3"));
        final List<AuditLogLine> auditLogLines = scenarioResultMetadata.getAuditLogLines();
        assertNotNull(auditLogLines);
        if (messages == null) {
            assertEquals(decisionResults.size(), auditLogLines.size());
            for (int i = 0; i < decisionResults.size(); i++) {
                commonCheckAuditLogLine(auditLogLines.get(i), decisionResults.get(i).getDecisionName(), decisionResults.get(i).getEvaluationStatus().name(), null);
            }
        } else {
            List<String> expectedDecisions = Arrays.asList("decision2", "decision3");
            List<String> expectedResults = Arrays.asList(DecisionEvaluationStatus.SUCCEEDED.toString(), DecisionEvaluationStatus.FAILED.toString());
            int expectedLines = messages.size() * expectedDecisions.size();
            assertEquals(expectedLines, auditLogLines.size());
            for (int i = 0; i < auditLogLines.size(); i++) {
                int messagesIndex = i < messages.size() ? i : i - messages.size();
                String decisionName = i < messages.size() ? expectedDecisions.get(0) : expectedDecisions.get(1);
                String expectedResultName = i < messages.size() ? expectedResults.get(0) : expectedResults.get(1);
                commonCheckAuditLogLine(auditLogLines.get(i), decisionName, expectedResultName, messages.get(messagesIndex).getLevel().name() + ": " + messages.get(messagesIndex).getText());
            }
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