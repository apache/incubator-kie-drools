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

import org.kie.kogito.uow.events.UnitOfWorkAbortEvent;
import org.kie.kogito.uow.events.UnitOfWorkEndEvent;
import org.kie.kogito.uow.events.UnitOfWorkEventListener;
import org.kie.kogito.uow.events.UnitOfWorkStartEvent;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;

public abstract class AbstractTransactionManager implements UnitOfWorkEventListener {

    private final MongoClient mongoClient;
    private final Boolean enabled;

    private ThreadLocal<ClientSession> clientSessionLocal = new ThreadLocal<>();

    public AbstractTransactionManager(MongoClient mongoClient, Boolean enabled) {
        this.mongoClient = mongoClient;
        this.enabled = enabled;
    }

    @Override
    public void onBeforeStartEvent(UnitOfWorkStartEvent event) {
        if (!enabled()) {
            return;
        }

        ClientSession clientSession = mongoClient.startSession();
        this.clientSessionLocal.set(clientSession);
        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        clientSession.startTransaction(txnOptions);
    }

    @Override
    public void onAfterEndEvent(UnitOfWorkEndEvent event) {
        if (!enabled()) {
            return;
        }

        try (ClientSession clientSession = this.getClientSession()) {
            clientSession.commitTransaction();
        } finally {
            clientSessionLocal.remove();
        }
    }

    @Override
    public void onAfterAbortEvent(UnitOfWorkAbortEvent event) {
        if (!enabled()) {
            return;
        }

        try (ClientSession clientSession = this.getClientSession()) {
            clientSession.abortTransaction();
        } finally {
            clientSessionLocal.remove();
        }
    }

    public ClientSession getClientSession() {
        return clientSessionLocal.get();
    }

    public boolean enabled() {
        return enabled;
    }
}
