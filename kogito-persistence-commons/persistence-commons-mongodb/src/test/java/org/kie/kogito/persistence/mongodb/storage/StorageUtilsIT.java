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

package org.kie.kogito.persistence.mongodb.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.mongodb.client.MongoCollection;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.mock.MockMongoEntityMapper;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.storage.MongoStorage.OPERATION_TYPE;

@QuarkusTest
@QuarkusTestResource(MongoServerTestResource.class)
class StorageUtilsIT {

    @Inject
    MongoClientManager mongoClientManager;

    @AfterEach
    void tearDown() {
        MongoCollection<Document> collection = mongoClientManager.getCollection("test");
        collection.drop();
    }

    @Test
    void testWatchCollection_insert() throws Exception {
        com.mongodb.reactivestreams.client.MongoCollection<Document> reactiveMongoCollection = mongoClientManager.getReactiveCollection("test");
        MongoEntityMapper<String, Document> mongoEntityMapper = new MockMongoEntityMapper();

        TestListener testListenerInsert1 = new TestListener(2);
        StorageUtils.watchCollection(reactiveMongoCollection, eq(OPERATION_TYPE, "insert"), (k, v) -> testListenerInsert1.add(v), mongoEntityMapper);

        TestListener testListenerInsert2 = new TestListener(2);
        StorageUtils.watchCollection(reactiveMongoCollection, eq(OPERATION_TYPE, "insert"), (k, v) -> testListenerInsert2.add(v), mongoEntityMapper);

        MongoCollection<Document> mongoCollection = mongoClientManager.getCollection("test");
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey1", "testValue1"));
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey2", "testValue2"));

        testListenerInsert1.await();
        testListenerInsert2.await();
        assertEquals(2, testListenerInsert1.items.size());
        assertTrue(testListenerInsert1.items.keySet().containsAll(asList("testValue1", "testValue2")));
        assertEquals(2, testListenerInsert2.items.size());
        assertTrue(testListenerInsert2.items.keySet().containsAll(asList("testValue1", "testValue2")));
    }

    @Test
    void testWatchCollection_update() throws Exception {
        com.mongodb.reactivestreams.client.MongoCollection<Document> mongoReactiveCollection = mongoClientManager.getReactiveCollection("test");
        MongoEntityMapper<String, Document> mongoEntityMapper = new MockMongoEntityMapper();

        TestListener testListenerUpdate1 = new TestListener(2);
        StorageUtils.watchCollection(mongoReactiveCollection, eq(OPERATION_TYPE, "replace"), (k, v) -> testListenerUpdate1.add(v), mongoEntityMapper);

        TestListener testListenerUpdate2 = new TestListener(2);
        StorageUtils.watchCollection(mongoReactiveCollection, eq(OPERATION_TYPE, "replace"), (k, v) -> testListenerUpdate2.add(v), mongoEntityMapper);

        MongoCollection<Document> mongoCollection = mongoClientManager.getCollection("test");
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey1", "testValue1"));
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey2", "testValue2"));

        mongoCollection.replaceOne(new Document(MONGO_ID, "testKey1"), mongoEntityMapper.mapToEntity("testKey1", "testValue3"));
        mongoCollection.replaceOne(new Document(MONGO_ID, "testKey2"), mongoEntityMapper.mapToEntity("testKey2", "testValue4"));

        testListenerUpdate1.await();
        testListenerUpdate2.await();
        assertEquals(2, testListenerUpdate1.items.size());
        assertTrue(testListenerUpdate1.items.keySet().containsAll(asList("testValue3", "testValue4")));
        assertEquals(2, testListenerUpdate2.items.size());
        assertTrue(testListenerUpdate2.items.keySet().containsAll(asList("testValue3", "testValue4")));
    }

    @Test
    void testWatchCollection_delete() throws Exception {
        com.mongodb.reactivestreams.client.MongoCollection<Document> mongoReactiveCollection = mongoClientManager.getReactiveCollection("test");
        MongoEntityMapper<String, Document> mongoEntityMapper = new MockMongoEntityMapper();

        TestListener testListenerRemove1 = new TestListener(2);
        StorageUtils.watchCollection(mongoReactiveCollection, eq(OPERATION_TYPE, "insert"), (k, v) -> testListenerRemove1.add(k), mongoEntityMapper);

        TestListener testListenerRemove2 = new TestListener(2);
        StorageUtils.watchCollection(mongoReactiveCollection, eq(OPERATION_TYPE, "insert"), (k, v) -> testListenerRemove2.add(k), mongoEntityMapper);

        MongoCollection<Document> mongoCollection = mongoClientManager.getCollection("test");
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey1", "testValue1"));
        mongoCollection.insertOne(mongoEntityMapper.mapToEntity("testKey2", "testValue2"));

        mongoCollection.deleteOne(new Document(MONGO_ID, "testKey1"));
        mongoCollection.deleteOne(new Document(MONGO_ID, "testKey2"));

        testListenerRemove1.await();
        testListenerRemove2.await();
        assertEquals(2, testListenerRemove1.items.size());
        assertTrue(testListenerRemove1.items.keySet().containsAll(asList("testKey1", "testKey2")));
        assertEquals(2, testListenerRemove2.items.size());
        assertTrue(testListenerRemove2.items.keySet().containsAll(asList("testKey1", "testKey2")));
    }

    static class TestListener {

        volatile Map<String, String> items = new ConcurrentHashMap<>();
        CountDownLatch latch;

        TestListener(int count) {
            latch = new CountDownLatch(count);
        }

        void await() throws InterruptedException {
            latch.await(10L, TimeUnit.SECONDS);
        }

        void add(String item) {
            items.put(item, item);
            latch.countDown();
        }
    }
}