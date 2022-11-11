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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.kie.kogito.uow.events.UnitOfWorkAbortEvent;
import org.kie.kogito.uow.events.UnitOfWorkEndEvent;
import org.kie.kogito.uow.events.UnitOfWorkStartEvent;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class AbstractTransactionManagerIT {

    static class TestTransactionManager extends AbstractTransactionManager {

        public TestTransactionManager(MongoClient mongoClient) {
            super(mongoClient, true);
        }

    }

    @Container
    private static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    private static MongoClient mongoClient;

    private static final String DOCUMENT_ID = "_id";
    private static final String TEST_KEY = "test";

    private static final int TEST_THREADS = 2;

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @Test
    void testInsertion() throws InterruptedException, ExecutionException, TimeoutException {
        String testName = "test_insertion";

        MongoDatabase mongoDatabase = mongoClient.getDatabase(testName);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(testName);
        mongoCollection.insertOne(new Document().append(DOCUMENT_ID, "test0"));

        AbstractTransactionManager transactionManager = new TestTransactionManager(mongoClient);

        String id1 = "test1";
        String value1 = "test1";
        String id2 = "test2";
        String value2 = "test2";

        ExecutorService service = Executors.newFixedThreadPool(TEST_THREADS);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        Future<?> execution1 = service.submit(() -> {
            try {
                transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

                MongoCollection<Document> mongoCollection1 = mongoDatabase.getCollection(testName);
                mongoCollection1.insertOne(transactionManager.getClientSession(), new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));

                Document result1 = mongoCollection1.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id1)).first();
                assertThat(result1).isEqualTo(new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));

                int size1 = (int) mongoCollection1.countDocuments(transactionManager.getClientSession());
                assertThat(size1).isEqualTo(2);
            } finally {
                latch1.countDown();
                assertThat(latch2.await(10, TimeUnit.SECONDS)).isTrue();
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        Future<?> execution2 = service.submit(() -> {
            try {
                transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

                MongoCollection<Document> mongoCollection2 = mongoDatabase.getCollection(testName);
                mongoCollection2.insertOne(transactionManager.getClientSession(), new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2));

                Document result2 = mongoCollection2.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id2)).first();
                assertThat(result2).isEqualTo(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2));

                int size2 = (int) mongoCollection2.countDocuments(transactionManager.getClientSession());
                assertThat(size2).isEqualTo(2);
            } finally {
                latch2.countDown();
                assertThat(latch1.await(10, TimeUnit.SECONDS)).isTrue();
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        execution1.get(10, TimeUnit.SECONDS);
        execution2.get(10, TimeUnit.SECONDS);
        assertThat(mongoCollection.countDocuments()).isEqualTo(3);
    }

    @Test
    void testDeletionUpdate() throws InterruptedException, ExecutionException, TimeoutException {
        String testName = "test_deletion_update";

        MongoDatabase mongoDatabase = mongoClient.getDatabase(testName);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(testName);

        AbstractTransactionManager transactionManager = new TestTransactionManager(mongoClient);

        String id1 = "test1";
        String value1 = "test1";
        String id2 = "test2";
        String value2 = "test2";

        mongoCollection.insertOne(new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));
        mongoCollection.insertOne(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2));

        ExecutorService service = Executors.newFixedThreadPool(TEST_THREADS);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        Future<?> execution1 = service.submit(() -> {
            try {
                transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

                MongoCollection<Document> mongoCollection1 = mongoDatabase.getCollection(testName);
                mongoCollection1.deleteOne(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id1));

                List<Document> values1 = new ArrayList<>();
                try (MongoCursor<Document> cursor = mongoCollection1.find(transactionManager.getClientSession()).iterator()) {
                    while (cursor.hasNext()) {
                        values1.add(cursor.next());
                    }

                    assertThat(values1).hasSize(1);
                    assertThat(values1.stream().allMatch(v -> id2.equals(v.get(DOCUMENT_ID).toString()))).isTrue();
                }

                Document value = mongoCollection1.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id2)).first();
                assertThat(value).isEqualTo(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2));
            } finally {
                latch1.countDown();
                assertThat(latch2.await(10, TimeUnit.SECONDS)).isTrue();
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        Future<?> execution2 = service.submit(() -> {
            try {
                transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

                MongoCollection<Document> mongoCollection2 = mongoDatabase.getCollection(testName);
                mongoCollection2.replaceOne(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id2), new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value1));
                Document values2 = mongoCollection2.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id2)).first();
                assertThat(values2).isEqualTo(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value1));

                int size2 = (int) mongoCollection2.countDocuments(transactionManager.getClientSession());
                assertThat(size2).isEqualTo(2);
            } finally {
                latch2.countDown();
                assertThat(latch1.await(10, TimeUnit.SECONDS)).isTrue();
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        execution1.get(10, TimeUnit.SECONDS);
        execution2.get(10, TimeUnit.SECONDS);
        assertThat(mongoCollection.countDocuments()).isOne();
        assertThat(Objects.requireNonNull(mongoCollection.find(Filters.eq(DOCUMENT_ID, id2)).first()).getString(TEST_KEY)).isEqualTo(value1);
    }

    @Test
    void testAbort() {
        String testName = "test_abort";

        MongoDatabase mongoDatabase = mongoClient.getDatabase(testName);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(testName);
        mongoCollection.insertOne(new Document().append(DOCUMENT_ID, "test0"));

        AbstractTransactionManager transactionManager = new TestTransactionManager(mongoClient);

        String id1 = "test1";
        String value1 = "test1";

        transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

        mongoCollection.insertOne(transactionManager.getClientSession(), new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));

        Document result = mongoCollection.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id1)).first();
        assertThat(result).isEqualTo(new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));

        int size1 = (int) mongoCollection.countDocuments(transactionManager.getClientSession());
        assertThat(size1).isEqualTo(2);

        int size2 = (int) mongoCollection.countDocuments();
        assertThat(size2).isOne();

        transactionManager.onAfterAbortEvent(new UnitOfWorkAbortEvent(null));

        int size3 = (int) mongoCollection.countDocuments();
        assertThat(size3).isOne();
    }
}
