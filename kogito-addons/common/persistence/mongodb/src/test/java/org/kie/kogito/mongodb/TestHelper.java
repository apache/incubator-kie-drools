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
package org.kie.kogito.mongodb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.mongodb.transaction.AbstractTransactionManager;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestHelper {

    @Container
    final static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    public final static String DB_NAME = "testdb";
    public final static String PROCESS_NAME = "test";
    private static MongoClient mongoClient;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    public static void close() {
        mongoClient.close();
        mongoDBContainer.stop();
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static AbstractTransactionManager getDisabledMongoDBTransactionManager() {
        return new AbstractTransactionManager(mongoClient, false) {
        };
    }

}
