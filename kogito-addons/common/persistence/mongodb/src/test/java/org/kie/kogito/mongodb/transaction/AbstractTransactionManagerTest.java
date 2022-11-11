/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.mongodb.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.uow.events.UnitOfWorkAbortEvent;
import org.kie.kogito.uow.events.UnitOfWorkEndEvent;
import org.kie.kogito.uow.events.UnitOfWorkStartEvent;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AbstractTransactionManagerTest {

    private static final int TEST_THREADS = 10;

    private MongoClient mongoClient;
    private ClientSession clientSession;
    private AbstractTransactionManager manager;

    @BeforeEach
    void setUp() {
        mongoClient = mock(MongoClient.class);
        clientSession = mock(ClientSession.class);
        when(mongoClient.startSession()).thenReturn(clientSession);

        manager = new AbstractTransactionManager(mongoClient, true) {
        };
    }

    @Test
    void test() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService service = Executors.newFixedThreadPool(TEST_THREADS);
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i < TEST_THREADS; i++) {
            results.add(service.submit(() -> {
                manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
                manager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            }));
        }

        for (Future<?> result : results) {
            result.get(2L, TimeUnit.SECONDS);
        }

        verify(mongoClient, times(TEST_THREADS)).startSession();
        verify(clientSession, times(TEST_THREADS)).startTransaction(any());
        verify(clientSession, times(TEST_THREADS)).commitTransaction();
        verify(clientSession, times(TEST_THREADS)).close();
    }

    @Test
    void enabled() {
        AbstractTransactionManager manager = new AbstractTransactionManager(mongoClient, false) {
        };

        manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        manager.onAfterAbortEvent(new UnitOfWorkAbortEvent(null));

        verify(mongoClient, never()).startSession();
    }

    @Test
    void onBeforeStartEvent() {
        manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        verify(clientSession, times(1)).startTransaction(any());
    }

    @Test
    void onAfterEndEvent() {
        manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        manager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
        verify(clientSession, times(1)).commitTransaction();
        verify(clientSession, times(1)).close();
    }

    @Test
    void onAfterAbortEvent() {
        manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        manager.onAfterAbortEvent(new UnitOfWorkAbortEvent(null));
        verify(clientSession, times(1)).abortTransaction();
        verify(clientSession, times(1)).close();
    }

    @Test
    void getClientSession() {
        assertThat(manager.getClientSession()).isNull();

        manager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));
        assertThat(manager.getClientSession()).isEqualTo(clientSession);
    }
}
