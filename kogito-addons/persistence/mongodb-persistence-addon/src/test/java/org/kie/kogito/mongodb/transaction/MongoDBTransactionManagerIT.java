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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class MongoDBTransactionManagerIT {

    static class TestTransactionManager extends MongoDBTransactionManager {

        public TestTransactionManager(MongoClient mongoClient) {
            super(mongoClient);
        }

        @Override
        public boolean enabled() {
            return true;
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

        MongoDBTransactionManager transactionManager = new TestTransactionManager(mongoClient);

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
                assertEquals(new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1), result1);

                int size1 = (int) mongoCollection1.countDocuments(transactionManager.getClientSession());
                assertEquals(2, size1);
            } finally {
                latch1.countDown();
                assertTrue(latch2.await(10, TimeUnit.SECONDS));
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
                assertEquals(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2), result2);

                int size2 = (int) mongoCollection2.countDocuments(transactionManager.getClientSession());
                assertEquals(2, size2);
            } finally {
                latch2.countDown();
                assertTrue(latch1.await(10, TimeUnit.SECONDS));
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        execution1.get(10, TimeUnit.SECONDS);
        execution2.get(10, TimeUnit.SECONDS);
        assertEquals(3, mongoCollection.countDocuments());
    }

    @Test
    void testDeletionUpdate() throws InterruptedException, ExecutionException, TimeoutException {
        String testName = "test_deletion_update";

        MongoDatabase mongoDatabase = mongoClient.getDatabase(testName);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(testName);

        MongoDBTransactionManager transactionManager = new TestTransactionManager(mongoClient);

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

                    assertEquals(1, values1.size());
                    assertTrue(values1.stream().allMatch(v -> id2.equals(v.get(DOCUMENT_ID).toString())));
                }

                Document value = mongoCollection1.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id2)).first();
                assertEquals(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value2), value);
            } finally {
                latch1.countDown();
                assertTrue(latch2.await(10, TimeUnit.SECONDS));
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
                assertEquals(new Document().append(DOCUMENT_ID, id2).append(TEST_KEY, value1), values2);

                int size2 = (int) mongoCollection2.countDocuments(transactionManager.getClientSession());
                assertEquals(2, size2);
            } finally {
                latch2.countDown();
                assertTrue(latch1.await(10, TimeUnit.SECONDS));
            }
            transactionManager.onAfterEndEvent(new UnitOfWorkEndEvent(null));
            return null;
        });

        execution1.get(10, TimeUnit.SECONDS);
        execution2.get(10, TimeUnit.SECONDS);
        assertEquals(1, mongoCollection.countDocuments());
        assertEquals(value1, Objects.requireNonNull(mongoCollection.find(Filters.eq(DOCUMENT_ID, id2)).first()).getString(TEST_KEY));
    }

    @Test
    void testAbort() {
        String testName = "test_abort";

        MongoDatabase mongoDatabase = mongoClient.getDatabase(testName);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(testName);
        mongoCollection.insertOne(new Document().append(DOCUMENT_ID, "test0"));

        MongoDBTransactionManager transactionManager = new TestTransactionManager(mongoClient);

        String id1 = "test1";
        String value1 = "test1";

        transactionManager.onBeforeStartEvent(new UnitOfWorkStartEvent(null));

        mongoCollection.insertOne(transactionManager.getClientSession(), new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1));

        Document result = mongoCollection.find(transactionManager.getClientSession(), Filters.eq(DOCUMENT_ID, id1)).first();
        assertEquals(new Document().append(DOCUMENT_ID, id1).append(TEST_KEY, value1), result);

        int size1 = (int) mongoCollection.countDocuments(transactionManager.getClientSession());
        assertEquals(2, size1);

        int size2 = (int) mongoCollection.countDocuments();
        assertEquals(1, size2);

        transactionManager.onAfterAbortEvent(new UnitOfWorkAbortEvent(null));

        int size3 = (int) mongoCollection.countDocuments();
        assertEquals(1, size3);
    }
}
