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

import java.util.Objects;

import javax.inject.Inject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;
import org.kie.kogito.persistence.mongodb.mock.MockMongoEntityMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.persistence.mongodb.mock.MockMongoEntityMapper.TEST_ATTRIBUTE;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;

@QuarkusTest
@QuarkusTestResource(MongoServerTestResource.class)
class MongoStorageIT {

    @Inject
    MongoClientManager mongoClientManager;

    MongoStorage<String, Document> storage;

    MongoCollection<Document> collection;

    @BeforeEach
    void setup() {
        collection = mongoClientManager.getCollection("test", Document.class);
        storage = new MongoStorage(collection, mongoClientManager.getReactiveCollection("test", Document.class),
                                   String.class.getName(), new MockMongoEntityMapper());
    }

    @AfterEach
    void tearDown() {
        collection.drop();
    }

    @Test
    void testContainsKey() {
        String testId = "testContains";
        collection.insertOne(new Document(MONGO_ID, testId));
        assertTrue(storage.containsKey(testId));
    }

    @Test
    void testGet() {
        String testId = "testGet";
        String testValue = "testValue";
        collection.insertOne(new Document(MONGO_ID, testId).append(TEST_ATTRIBUTE, testValue));
        assertEquals(testValue, storage.get(testId));
    }

    @Test
    void testPut() {
        String testId = "testPut";
        String testValue = "testValue";
        storage.put(testId, testValue);
        FindIterable<Document> findIterable = collection.find(new Document(MONGO_ID, testId));
        Document document = findIterable.first();
        assertTrue(Objects.nonNull(document));
        assertEquals(testValue, document.get(TEST_ATTRIBUTE));
    }

    @Test
    void testClear() {
        String testId = "testClear";
        String testValue = "testValue";
        collection.insertOne(new Document(MONGO_ID, testId).append(TEST_ATTRIBUTE, testValue));
        storage.clear();
        FindIterable<Document> findIterable = collection.find();
        assertFalse(findIterable.iterator().hasNext());
    }

    @Test
    void testRemove() {
        String testId = "testRemove";
        String testValue = "testValue";
        collection.insertOne(new Document(MONGO_ID, testId).append(TEST_ATTRIBUTE, testValue));
        storage.remove(testId);
        FindIterable<Document> findIterable = collection.find(new Document(MONGO_ID, testId));
        assertFalse(findIterable.iterator().hasNext());
    }
}