/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.trusty.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.kie.kogito.trusty.service.mocks.StorageImplMock;
import org.kie.kogito.trusty.service.models.MatchedExecutionHeaders;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrustyServiceTest {

    private TrustyStorageService trustyStorageServiceMock;
    private TrustyService trustyService;

    @BeforeEach
    void setup() {
        trustyStorageServiceMock = mock(TrustyStorageService.class);
        trustyService = new TrustyServiceImpl(trustyStorageServiceMock);
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
        String executionId = "executionId";
        Decision decision = new Decision();
        decision.setExecutionId(executionId);

        Query queryMock = mock(Query.class);
        when(queryMock.filter(any(List.class))).thenReturn(queryMock);
        when(queryMock.offset(any(Integer.class))).thenReturn(queryMock);
        when(queryMock.sort(any(List.class))).thenReturn(queryMock);
        when(queryMock.execute()).thenReturn(List.of(decision));

        Storage storageMock = mock(Storage.class);
        when(storageMock.put(eq(executionId), any(Object.class))).thenReturn(decision);
        when(storageMock.containsKey(eq(executionId))).thenReturn(false);
        when(storageMock.query()).thenReturn(queryMock);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        trustyService.storeDecision("executionId", decision);

        MatchedExecutionHeaders result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 100, 0, "");

        Assertions.assertEquals(1, result.getExecutions().size());
        Assertions.assertEquals(decision.getExecutionId(), result.getExecutions().get(0).getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenManyExecutionsThenPaginationWorksProperly() {
        List<Decision> decisions = new ArrayList<>();
        IntStream.range(0,10).forEach(x -> {
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

        Assertions.assertEquals(3, result.getExecutions().size());
        Assertions.assertEquals(decisions.size(), result.getAvailableResults());

        result = trustyService.getExecutionHeaders(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), 100, 5, "");

        Assertions.assertEquals(5, result.getExecutions().size());
        Assertions.assertEquals(decisions.size(), result.getAvailableResults());
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

        Assertions.assertEquals(0, result.getExecutions().size());
        Assertions.assertEquals(0, result.getAvailableResults());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenADecisionWhenADecisionIsStoredAndRetrievedByIdThenTheOriginalObjectIsReturned() {
        String executionId = "executionId";
        Decision decision = new Decision();
        decision.setExecutionId(executionId);

        Storage storageMock = new StorageImplMock(Decision.class);

        when(trustyStorageServiceMock.getDecisionsStorage()).thenReturn(storageMock);

        trustyService.storeDecision(executionId, decision);

        Decision result = trustyService.getDecisionById(executionId);

        Assertions.assertEquals(executionId, result.getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenStoreModelIsCalledThenNoExceptionsAreThrown() {
        String model = "definition";
        Storage storageMock = mock(Storage.class);

        when(storageMock.put(any(Object.class), any(Object.class))).thenReturn(model);
        when(trustyStorageServiceMock.getModelStorage()).thenReturn(storageMock);

        Assertions.assertDoesNotThrow(() -> trustyService.storeModel("groupId", "artifactId", "version", "name", "namespace", model));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenStoreModelIsCalledMoreThanOnceForSameModelThenExceptionIsThrown() {
        String modelId = "name:namespace";
        String model = "definition";
        Storage storageMock = mock(Storage.class);

        when(storageMock.containsKey(modelId)).thenReturn(true);
        when(storageMock.put(any(Object.class), any(Object.class))).thenReturn(model);
        when(trustyStorageServiceMock.getModelStorage()).thenReturn(storageMock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> trustyService.storeModel("groupId", "artifactId", "version", "name", "namespace", model));
    }

    @Test
    @SuppressWarnings("unchecked")
    void givenAModelWhenAModelIsStoredAndRetrievedByIdThenTheOriginalObjectIsReturned() {
        String modelId = "name:namespace";
        String model = "definition";
        Storage storageMock = new StorageImplMock(String.class);

        when(trustyStorageServiceMock.getModelStorage()).thenReturn(storageMock);

        trustyService.storeModel("groupId", "artifactId", "version", "name", "namespace", model);

        String result = trustyService.getModelById(modelId);

        Assertions.assertEquals(model, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenAModelIsNotStoredAndRetrievedByIdThenExceptionIsThrown() {
        String modelId = "name:namespace";
        Storage storageMock = mock(Storage.class);

        when(storageMock.containsKey(modelId)).thenReturn(false);
        when(trustyStorageServiceMock.getModelStorage()).thenReturn(storageMock);

        Assertions.assertThrows(IllegalArgumentException.class, () -> trustyService.getModelById(modelId));
    }
}
