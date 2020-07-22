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

package org.kie.kogito.persistence.mongodb.client;

import javax.inject.Inject;

import com.mongodb.client.MongoCollection;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(MongoServerTestResource.class)
class MongoClientManagerIT {

    @Inject
    MongoClientManager mongoClientManager;

    @Test
    void testGetCollection() {
        MongoCollection<Document> mongoCollection = mongoClientManager.getCollection("test");
        assertEquals(mongoClientManager.database, mongoCollection.getNamespace().getDatabaseName());
        assertEquals("test", mongoCollection.getNamespace().getCollectionName());
    }

    @Test
    void testGetCollection_withDocumentClass() {
        MongoCollection<TestClass> mongoCollection = mongoClientManager.getCollection("test", TestClass.class);
        assertEquals(mongoClientManager.database, mongoCollection.getNamespace().getDatabaseName());
        assertEquals("test", mongoCollection.getNamespace().getCollectionName());
        assertTrue(mongoCollection.getDocumentClass().isAssignableFrom(TestClass.class));
    }

    @Test
    void testGetReactiveCollection() {
        com.mongodb.reactivestreams.client.MongoCollection<Document> mongoCollection = mongoClientManager.getReactiveCollection("test");
        assertEquals(mongoClientManager.database, mongoCollection.getNamespace().getDatabaseName());
        assertEquals("test", mongoCollection.getNamespace().getCollectionName());
    }

    @Test
    void testGetReactiveCollection_withDocumentClass() {
        com.mongodb.reactivestreams.client.MongoCollection<TestClass> mongoCollection = mongoClientManager.getReactiveCollection("test", TestClass.class);
        assertEquals(mongoClientManager.database, mongoCollection.getNamespace().getDatabaseName());
        assertEquals("test", mongoCollection.getNamespace().getCollectionName());
        assertTrue(mongoCollection.getDocumentClass().isAssignableFrom(TestClass.class));
    }

    private static class TestClass {

    }
}