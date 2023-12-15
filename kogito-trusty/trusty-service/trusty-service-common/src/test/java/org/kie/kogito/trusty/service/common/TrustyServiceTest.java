/*
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
package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualDomain;
import org.kie.kogito.explainability.api.CounterfactualDomainRange;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainValue;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.service.common.handlers.CounterfactualExplainabilityResultsManagerDuplicates;
import org.kie.kogito.trusty.service.common.handlers.CounterfactualExplainabilityResultsManagerSlidingWindow;
import org.kie.kogito.trusty.service.common.handlers.CounterfactualExplainerServiceHandler;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandler;
import org.kie.kogito.trusty.service.common.handlers.ExplainerServiceHandlerRegistry;
import org.kie.kogito.trusty.service.common.handlers.LIMEExplainerServiceHandler;
import org.kie.kogito.trusty.service.common.messaging.outgoing.ExplainabilityRequestProducer;
import org.kie.kogito.trusty.service.common.mocks.StorageImplMock;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import jakarta.enterprise.inject.Instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrustyServiceTest {

    private static final String TEST_EXECUTION_ID = UUID.randomUUID().toString();
    private static final String TEST_COUNTERFACTUAL_ID = UUID.randomUUID().toString();
    private static final String TEST_MODEL = "definition";
    private static final String TEST_SOURCE_URL = "http://localhost:8080/model/service";
    private static final String TEST_SERVICE_URL = "http://localhost:8080/model";
    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ExplainabilityRequestProducer explainabilityRequestProducerMock;
    private TrustyStorageService trustyStorageServiceMock;
    private TrustyServiceImpl trustyService;
    private LIMEExplainerServiceHandler limeExplainerServiceHandler;
    private CounterfactualExplainerServiceHandler counterfactualExplainerServiceHandler;
    private Instance<ExplainerServiceHandler<?>> explanationHandlers;

    private static JsonNode toJsonNode(String jsonString) throws JsonProcessingException {
        return MAPPER.reader().readTree(jsonString);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        explainabilityRequestProducerMock = mock(ExplainabilityRequestProducer.class);
        trustyStorageServiceMock = mock(TrustyStorageService.class);
        limeExplainerServiceHandler = new LIMEExplainerServiceHandler(trustyStorageServiceMock);
        counterfactualExplainerServiceHandler = new CounterfactualExplainerServiceHandler(trustyStorageServiceMock,
                mock(CounterfactualExplainabilityResultsManagerSlidingWindow.class),
                mock(CounterfactualExplainabilityResultsManagerDuplicates.class));
        explanationHandlers = mock(Instance.class);
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler,
                counterfactualExplainerServiceHandler));

        trustyService = new TrustyServiceImpl(false,
                explainabilityRequestProducerMock,
                trustyStorageServiceMock,
                new ExplainerServiceHandlerRegistry(explanationHandlers),
                MAX_RUNNING_TIME_SECONDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionWhenStoreDecisionIsCalledThenNoExceptionsAreThrown() {
        Decision decision = new Decision();
        Storage storageMock = mock(Storage.class);
        when(storageMock.put(any(Object.class), any(Object.class))).thenReturn(decision);
        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        Assertions.assertDoesNotThrow(() -> trustyService.storeDecision("test", decision));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionWhenADecisionIsStoredAndRetrievedThenTheOriginalObjectIsReturned() {
        Decision decision = new Decision();
        decision.setExecutionId(TEST_EXECUTION_ID);

        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.offset(any(Integer.class))).thenReturn(queryMock);
        when(queryMock.sort(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(decision));

        Storage storageMock = mock(Storage.class);
        when(storageMock.put(eq(TEST_EXECUTION_ID), any(Object.class))).thenReturn(decision);
        when(storageMock.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(false);
        when(storageMock.query()).thenReturn(queryMock);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        trustyService.storeDecision(TEST_EXECUTION_ID, decision);

        MatchedExecutionHeaders result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 100, 0, "");

        assertEquals(1, result.getExecutions().size());
        assertEquals(decision.getExecutionId(), result.getExecutions().get(0).getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenManyExecutionsThenPaginationWorksProperly() {
        List<Decision> decisions = new ArrayList<>();
        IntStream.range(0, 10).forEach(x -> {
            Decision d = new Decision();
            d.setExecutionId(String.valueOf(x));
            decisions.add(d);
        });

        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.sort(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(decisions);

        Storage storageMock = mock(Storage.class);
        decisions.forEach(x -> {
            when(storageMock.put(eq(x.getExecutionId()), any(Object.class))).thenReturn(x);
            when(storageMock.containsKey(eq(x.getExecutionId()))).thenReturn(false);
        });
        when(storageMock.query()).thenReturn(queryMock);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        decisions.forEach(x -> trustyService.storeDecision(x.getExecutionId(), x));

        MatchedExecutionHeaders result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 3, 5, "");

        assertEquals(3, result.getExecutions().size());
        assertEquals(decisions.size(), result.getAvailableResults());

        result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 100, 5, "");

        assertEquals(5, result.getExecutions().size());
        assertEquals(decisions.size(), result.getAvailableResults());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNoExecutionsNoExceptionsAreRaised() {
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.sort(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(new ArrayList<>());

        Storage storageMock = mock(Storage.class);
        when(storageMock.query()).thenReturn(queryMock);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        MatchedExecutionHeaders result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 100, 0, "");

        assertEquals(0, result.getExecutions().size());
        assertEquals(0, result.getAvailableResults());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionWhenADecisionIsStoredAndRetrievedByIdThenTheOriginalObjectIsReturned() {
        Decision decision = new Decision();
        decision.setExecutionId(TEST_EXECUTION_ID);

        @SuppressWarnings("unchecked")
        Storage storageMock = new StorageImplMock(Decision.class);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        trustyService.storeDecision(TEST_EXECUTION_ID, decision);

        Decision result = trustyService.getDecisionById(TEST_EXECUTION_ID);

        assertEquals(TEST_EXECUTION_ID, result.getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionToProcessWhenExplainabilityIsEnabledThenRequestIsSent() throws JsonProcessingException {
        trustyService.enableExplainability();

        Decision decision = new Decision(
                TEST_EXECUTION_ID, TEST_SOURCE_URL, TEST_SERVICE_URL, 1591692950000L, true,
                null, "model", "modelNamespace",
                List.of(
                        new DecisionInput("1", "Input1",
                                new CollectionValue("string",
                                        List.of(
                                                new UnitValue("string", "string", toJsonNode("\"ONE\"")),
                                                new UnitValue("string", "string", toJsonNode("\"TWO\""))))),
                        new DecisionInput("2", "Input2",
                                new StructureValue("Person",
                                        Map.of(
                                                "Name", new UnitValue("string", "string", toJsonNode("\"George Orwell\"")),
                                                "Age", new UnitValue("number", "number", toJsonNode("45")))))),
                List.of(
                        new DecisionOutcome(
                                "OUT1", "Result", "SUCCEEDED",
                                new UnitValue("string", "string", toJsonNode("\"YES\"")),
                                Collections.emptyList(), Collections.emptyList())));

        Storage<String, Decision> decisionStorageMock = mock(Storage.class);
        when(decisionStorageMock.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(false);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorageMock);

        trustyService.processDecision(TEST_EXECUTION_ID, decision);

        verify(explainabilityRequestProducerMock).sendEvent(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionToProcessThatAlreadyExistsWhenExplainabilityIsEnabledThenExceptionIsThrown() {
        trustyService.enableExplainability();

        Decision decision = new Decision();
        decision.setExecutionId(TEST_EXECUTION_ID);

        Storage<String, Decision> decisionStorageMock = mock(Storage.class);
        when(decisionStorageMock.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(true);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorageMock);

        assertThrows(IllegalArgumentException.class, () -> trustyService.processDecision(TEST_EXECUTION_ID, decision));

        verify(explainabilityRequestProducerMock, never()).sendEvent(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenStoreModelIsCalledThenNoExceptionsAreThrown() {
        String model = TEST_MODEL;
        Storage storageMock = mock(Storage.class);

        when(storageMock.put(any(Object.class), any(Object.class))).thenReturn(model);
        when(trustyStorageServiceMock.getModelStorage(DMNModelWithMetadata.class)).thenReturn(storageMock);

        Assertions.assertDoesNotThrow(() -> trustyService.storeModel(buildDmnModel(model)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenStoreModelIsCalledMoreThanOnceForSameModelThenExceptionIsThrown() {
        String model = TEST_MODEL;
        Storage storageMock = mock(Storage.class);

        DMNModelWithMetadata dmnModelWithMetadata = buildDmnModel(model);
        DMNModelMetadata modelIdentifier = dmnModelWithMetadata.getModelMetaData();

        when(storageMock.containsKey(modelIdentifier.getIdentifier())).thenReturn(true);
        when(storageMock.put(any(Object.class), any(Object.class))).thenReturn(model);
        when(trustyStorageServiceMock.getModelStorage(DMNModelWithMetadata.class)).thenReturn(storageMock);

        assertThrows(IllegalArgumentException.class, () -> trustyService.storeModel(dmnModelWithMetadata));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenAModelIsStoredAndRetrievedByIdThenTheOriginalObjectIsReturned() {
        String model = TEST_MODEL;
        Storage storageMock = new StorageImplMock(String.class);

        when(trustyStorageServiceMock.getModelStorage(DMNModelWithMetadata.class)).thenReturn(storageMock);

        DMNModelWithMetadata dmnModelWithMetadata = buildDmnModel(model);
        DMNModelMetadata modelIdentifier = dmnModelWithMetadata.getModelMetaData();

        trustyService.storeModel(buildDmnModel(model));

        DMNModelWithMetadata result = trustyService.getModelById(modelIdentifier, DMNModelWithMetadata.class);

        assertEquals(model, result.getModel());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenAModelIsNotStoredAndRetrievedByIdThenExceptionIsThrown() {
        DMNModelMetadata modelIdentifier = buildDmnModelIdentifier();
        Storage storageMock = mock(Storage.class);

        when(storageMock.containsKey(modelIdentifier.getIdentifier())).thenReturn(false);
        when(trustyStorageServiceMock.getModelStorage(DMNModelWithMetadata.class)).thenReturn(storageMock);

        assertThrows(IllegalArgumentException.class, () -> trustyService.getModelById(modelIdentifier,
                DMNModelWithMetadata.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAnExplainabilityResultWhenStoreModelIsCalledThenNoExceptionsAreThrown() {
        LIMEExplainabilityResult result = new LIMEExplainabilityResult(TEST_EXECUTION_ID, ExplainabilityStatus.SUCCEEDED, null, Collections.emptyList());
        Storage<String, LIMEExplainabilityResult> storageMock = mock(Storage.class);

        when(storageMock.put(eq(TEST_EXECUTION_ID), any(LIMEExplainabilityResult.class))).thenReturn(result);
        when(trustyStorageServiceMock.getLIMEResultStorage()).thenReturn(storageMock);

        Assertions.assertDoesNotThrow(() -> trustyService.storeExplainabilityResult(TEST_EXECUTION_ID, result));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAnExplainabilityResultWhenStoreModelIsCalledMoreThanOnceForSameModelThenExceptionIsThrown() {
        LIMEExplainabilityResult result = new LIMEExplainabilityResult(TEST_EXECUTION_ID, ExplainabilityStatus.SUCCEEDED, null, Collections.emptyList());
        Storage<String, LIMEExplainabilityResult> storageMock = mock(Storage.class);

        when(storageMock.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(true);
        when(storageMock.put(eq(TEST_EXECUTION_ID), any(LIMEExplainabilityResult.class))).thenReturn(result);
        when(trustyStorageServiceMock.getLIMEResultStorage()).thenReturn(storageMock);

        assertThrows(IllegalArgumentException.class, () -> trustyService.storeExplainabilityResult(TEST_EXECUTION_ID, result));
    }

    @Test
    void givenAnExplainabilityResultWhenAnExplainabilityResultIsStoredAndRetrievedByIdThenTheOriginalObjectIsReturned() {
        LIMEExplainabilityResult result = new LIMEExplainabilityResult(TEST_EXECUTION_ID, ExplainabilityStatus.SUCCEEDED, null, Collections.emptyList());
        Storage<String, LIMEExplainabilityResult> storageMock = new StorageImplMock<>(String.class);

        when(trustyStorageServiceMock.getLIMEResultStorage()).thenReturn(storageMock);

        trustyService.storeExplainabilityResult(TEST_EXECUTION_ID, result);

        //The mocked stream needs to be recreated for subsequent invocations
        when(explanationHandlers.stream()).thenReturn(Stream.of(limeExplainerServiceHandler, counterfactualExplainerServiceHandler));
        assertEquals(result, trustyService.getExplainabilityResultById(TEST_EXECUTION_ID, LIMEExplainabilityResult.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAnExplainabilityResultNotStoredWhenRetrievedByIdThenExceptionIsThrown() {
        Storage<String, LIMEExplainabilityResult> storageMock = mock(Storage.class);

        when(storageMock.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(false);
        when(trustyStorageServiceMock.getLIMEResultStorage()).thenReturn(storageMock);

        assertThrows(IllegalArgumentException.class, () -> trustyService.getExplainabilityResultById(TEST_EXECUTION_ID, LIMEExplainabilityResult.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenNoStoredExecutionWhenCounterfactualRequestIsMadeThenExceptionIsThrown() {
        Storage<String, Decision> decisionStorage = mock(Storage.class);

        when(decisionStorage.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(false);
        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorage);

        assertThrows(IllegalArgumentException.class, () -> trustyService.requestCounterfactuals(TEST_EXECUTION_ID, Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    void givenStoredExecutionWhenCounterfactualRequestIsMadeThenRequestIsStored() {
        doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenRequestIsStoredTest(new CounterfactualDomainRange(new IntNode(10), new IntNode(20)));
    }

    @Test
    void givenStoredExecutionWhenCounterfactualRequestIsMadeThenRequestIsStoredWithNullDomains() {
        doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenRequestIsStoredTest(null);
    }

    @SuppressWarnings("unchecked")
    void doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenRequestIsStoredTest(CounterfactualDomain domain) {
        Storage<String, Decision> decisionStorage = mock(Storage.class);
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        ArgumentCaptor<CounterfactualExplainabilityRequest> counterfactualArgumentCaptor = ArgumentCaptor.forClass(CounterfactualExplainabilityRequest.class);

        when(decisionStorage.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(true);
        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorage);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);
        when(decisionStorage.get(eq(TEST_EXECUTION_ID))).thenReturn(TrustyServiceTestUtils.buildCorrectDecision(TEST_EXECUTION_ID));

        // The Goals structures must be comparable to the original decisions outcomes.
        // The Search Domain structures must be identical those of the original decision inputs.
        trustyService.requestCounterfactuals(TEST_EXECUTION_ID,
                List.of(new NamedTypedValue("Fine",
                        new StructureValue("tFine",
                                Map.of("Amount", new UnitValue("number", "number", new IntNode(0)),
                                        "Points", new UnitValue("number", "number", new IntNode(0))))),
                        new NamedTypedValue("Should the driver be suspended?",
                                new UnitValue("string", "string", new TextNode("No")))),
                List.of(new CounterfactualSearchDomain("Violation",
                        new CounterfactualSearchDomainStructureValue("tViolation",
                                Map.of("Type", new CounterfactualSearchDomainUnitValue("string", "string", true, domain),
                                        "Actual Speed", new CounterfactualSearchDomainUnitValue("number", "number", true, domain),
                                        "Speed Limit", new CounterfactualSearchDomainUnitValue("number", "number", true, domain)))),
                        new CounterfactualSearchDomain("Driver",
                                new CounterfactualSearchDomainStructureValue("tDriver",
                                        Map.of("Age", new CounterfactualSearchDomainUnitValue("number", "number", true, domain),
                                                "Points", new CounterfactualSearchDomainUnitValue("number", "number", true, domain))))));

        verify(counterfactualStorage).put(anyString(), counterfactualArgumentCaptor.capture());
        CounterfactualExplainabilityRequest counterfactual = counterfactualArgumentCaptor.getValue();
        assertNotNull(counterfactual);
        assertEquals(TEST_EXECUTION_ID, counterfactual.getExecutionId());
    }

    @Test
    void givenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventIsEmitted() {
        doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventIsEmittedTest(new CounterfactualDomainRange(new IntNode(10), new IntNode(20)));
    }

    @Test
    void givenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventIsEmittedWithNullDomains() {
        doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventIsEmittedTest(null);
    }

    @SuppressWarnings("unchecked")
    void doGivenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventIsEmittedTest(CounterfactualDomain domain) {
        Storage<String, Decision> decisionStorage = mock(Storage.class);
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        ArgumentCaptor<BaseExplainabilityRequest> explainabilityEventArgumentCaptor = ArgumentCaptor.forClass(BaseExplainabilityRequest.class);

        when(decisionStorage.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(true);
        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorage);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);
        when(decisionStorage.get(eq(TEST_EXECUTION_ID))).thenReturn(TrustyServiceTestUtils.buildCorrectDecision(TEST_EXECUTION_ID));

        // The Goals structures must be comparable to the original decisions outcomes.
        // The Search Domain structures must be identical those of the original decision inputs.
        trustyService.requestCounterfactuals(TEST_EXECUTION_ID,
                List.of(new NamedTypedValue("Fine",
                        new StructureValue("tFine",
                                Map.of("Amount", new UnitValue("number", "number", new IntNode(0)),
                                        "Points", new UnitValue("number", "number", new IntNode(0))))),
                        new NamedTypedValue("Should the driver be suspended?",
                                new UnitValue("string", "string", new TextNode("No")))),
                List.of(new CounterfactualSearchDomain("Violation",
                        new CounterfactualSearchDomainStructureValue("tViolation",
                                Map.of("Type",
                                        new CounterfactualSearchDomainUnitValue("string", "string", true, domain),
                                        "Actual Speed",
                                        new CounterfactualSearchDomainUnitValue("number", "number", true, domain),
                                        "Speed Limit",
                                        new CounterfactualSearchDomainUnitValue("number", "number", true, domain)))),
                        new CounterfactualSearchDomain("Driver",
                                new CounterfactualSearchDomainStructureValue("tDriver",
                                        Map.of("Age",
                                                new CounterfactualSearchDomainUnitValue("number", "number", true, domain),
                                                "Points",
                                                new CounterfactualSearchDomainUnitValue("number", "number", true, domain))))));

        verify(explainabilityRequestProducerMock).sendEvent(explainabilityEventArgumentCaptor.capture());
        BaseExplainabilityRequest event = explainabilityEventArgumentCaptor.getValue();
        assertNotNull(event);
        assertTrue(event instanceof CounterfactualExplainabilityRequest);
        CounterfactualExplainabilityRequest request = (CounterfactualExplainabilityRequest) event;
        assertEquals(TEST_EXECUTION_ID, request.getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenStoredExecutionWhenCounterfactualRequestIsMadeThenExplainabilityEventHasCorrectPayload() {
        Storage<String, Decision> decisionStorage = mock(Storage.class);
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        ArgumentCaptor<BaseExplainabilityRequest> explainabilityEventArgumentCaptor = ArgumentCaptor.forClass(BaseExplainabilityRequest.class);

        Decision decision = new Decision(
                TEST_EXECUTION_ID, TEST_SOURCE_URL, TEST_SERVICE_URL, 0L, true,
                null, "model", "modelNamespace",
                List.of(
                        new DecisionInput("IN1", "yearsOfService", new UnitValue("integer", "integer", new IntNode(10)))),
                List.of(
                        new DecisionOutcome(
                                "OUT1", "salary", "SUCCEEDED",
                                new UnitValue("integer", "integer", new IntNode(1000)),
                                Collections.emptyList(), Collections.emptyList())));

        when(decisionStorage.containsKey(eq(TEST_EXECUTION_ID))).thenReturn(true);
        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(decisionStorage);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);
        when(decisionStorage.get(eq(TEST_EXECUTION_ID))).thenReturn(decision);

        trustyService.requestCounterfactuals(TEST_EXECUTION_ID,
                List.of(new NamedTypedValue("salary",
                        new UnitValue("integer", "integer", new IntNode(2000)))),
                List.of(new CounterfactualSearchDomain("yearsOfService",
                        new CounterfactualSearchDomainUnitValue("integer", "integer", false,
                                new CounterfactualDomainRange(new IntNode(10), new IntNode(30))))));

        verify(explainabilityRequestProducerMock).sendEvent(explainabilityEventArgumentCaptor.capture());
        BaseExplainabilityRequest event = explainabilityEventArgumentCaptor.getValue();
        CounterfactualExplainabilityRequest request = (CounterfactualExplainabilityRequest) event;
        assertEquals(TEST_EXECUTION_ID, request.getExecutionId());
        assertEquals(TEST_SERVICE_URL, request.getServiceUrl());

        //Check original input value has been copied into CF request
        assertEquals(1, request.getOriginalInputs().size());
        assertTrue(request.getOriginalInputs().stream().anyMatch(i -> i.getName().equals("yearsOfService")));
        //It is safe to use the iterator unchecked as the collection only contains one item
        assertEquals(decision.getInputs().iterator().next().getValue().toUnit().getValue().asInt(),
                request.getOriginalInputs().iterator().next().getValue().toUnit().getValue().asInt());

        //Check CF goals have been copied into CF request
        assertEquals(1, request.getGoals().size());
        assertTrue(request.getGoals().stream().anyMatch(g -> g.getName().equals("salary")));
        //It is safe to use the iterator unchecked as the collection only contains one item
        assertEquals(2000, request.getGoals().iterator().next().getValue().toUnit().getValue().asInt());

        //Check CF search domains have been copied into CF request
        assertEquals(1, request.getSearchDomains().size());
        assertTrue(request.getSearchDomains().stream().anyMatch(sd -> sd.getName().equals("yearsOfService")));
        //It is safe to use the iterator unchecked as the collection only contains one item
        CounterfactualSearchDomainValue searchDomain = request.getSearchDomains().iterator().next().getValue();
        assertTrue(searchDomain instanceof CounterfactualSearchDomainUnitValue);

        CounterfactualSearchDomainUnitValue unit = (CounterfactualSearchDomainUnitValue) searchDomain;
        assertFalse(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainRange);

        CounterfactualDomainRange range = (CounterfactualDomainRange) unit.getDomain();
        assertEquals(10, range.getLowerBound().asInt());
        assertEquals(30, range.getUpperBound().asInt());

        //Check Max Running Time Seconds
        assertEquals(MAX_RUNNING_TIME_SECONDS, request.getMaxRunningTimeSeconds());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenStoredCounterfactualRequestsWhenGetCounterfactualRequestsThenRequestsAreReturned() {
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        CounterfactualExplainabilityRequest request1 = mock(CounterfactualExplainabilityRequest.class);
        CounterfactualExplainabilityRequest request2 = mock(CounterfactualExplainabilityRequest.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(request1, request2));
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);

        assertTrue(trustyService.getCounterfactualRequests(TEST_EXECUTION_ID).containsAll(List.of(request1, request2)));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenNoStoredCounterfactualRequestWhenGetCounterfactualRequestThenIllegalArgumentExceptionIsThrown() {
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(new ArrayList<>());
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);

        assertThrows(IllegalArgumentException.class, () -> trustyService.getCounterfactualRequest(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenMultipleStoredCounterfactualRequestsWhenGetCounterfactualRequestThenIllegalArgumentExceptionIsThrown() {
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        CounterfactualExplainabilityRequest request1 = mock(CounterfactualExplainabilityRequest.class);
        CounterfactualExplainabilityRequest request2 = mock(CounterfactualExplainabilityRequest.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(request1, request2));
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);

        assertThrows(IllegalArgumentException.class, () -> trustyService.getCounterfactualRequest(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenSingleStoredCounterfactualRequestWhenGetCounterfactualRequestThenRequestIsReturned() {
        Storage<String, CounterfactualExplainabilityRequest> counterfactualStorage = mock(Storage.class);
        CounterfactualExplainabilityRequest request = mock(CounterfactualExplainabilityRequest.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(request));
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualRequestStorage()).thenReturn(counterfactualStorage);

        assertEquals(request, trustyService.getCounterfactualRequest(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenStoredCounterfactualResultsWhenGetCounterfactualResultsThenResultsAreReturned() {
        Storage<String, CounterfactualExplainabilityResult> counterfactualStorage = mock(Storage.class);
        CounterfactualExplainabilityResult result1 = mock(CounterfactualExplainabilityResult.class);
        CounterfactualExplainabilityResult result2 = mock(CounterfactualExplainabilityResult.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(result1, result2));
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualResultStorage()).thenReturn(counterfactualStorage);

        assertTrue(trustyService.getCounterfactualResults(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID).containsAll(List.of(result1, result2)));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void givenNoStoredCounterfactualResultsWhenGetCounterfactualResultsThenEmptyCollectionIsReturned() {
        Storage<String, CounterfactualExplainabilityResult> counterfactualStorage = mock(Storage.class);
        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(new ArrayList());
        when(counterfactualStorage.query()).thenReturn(queryMock);
        when(trustyStorageServiceMock.getCounterfactualResultStorage()).thenReturn(counterfactualStorage);

        assertTrue(trustyService.getCounterfactualResults(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID).isEmpty());
    }

    private DMNModelWithMetadata buildDmnModel(String model) {
        return new DMNModelWithMetadata(buildDmnModelIdentifier(), model);
    }

    private DMNModelMetadata buildDmnModelIdentifier() {
        return new DMNModelMetadata("groupId", "artifactId", "version", "dmnVersion", "name", "namespace");
    }
}
