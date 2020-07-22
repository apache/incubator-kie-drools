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

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.factory.StorageQualifier;
import org.kie.kogito.persistence.mongodb.MongoServerTestResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.persistence.mongodb.Constants.MONGODB_STORAGE;

@QuarkusTest
@QuarkusTestResource(MongoServerTestResource.class)
class MongoStorageManagerIT {

    @Inject
    @StorageQualifier(MONGODB_STORAGE)
    MongoStorageManager mongoStorageManager;

    Storage storage;

    @AfterEach
    void tearDown() {
        ((MongoStorage) storage).mongoCollection.drop();
    }

    @Test
    void testGetCache() {
        String storageName = "testCache";
        storage = mongoStorageManager.getCache(storageName);

        assertTrue(storage instanceof MongoStorage);
        assertEquals(storageName, ((MongoStorage) storage).mongoCollection.getNamespace().getCollectionName());
    }

    @Test
    void testGetCacheWithClass() {
        String storageName = "testCacheWithClass";
        storage = mongoStorageManager.getCache(storageName, String.class);

        assertTrue(storage instanceof MongoStorage);
        assertEquals(storageName, ((MongoStorage) storage).mongoCollection.getNamespace().getCollectionName());
    }

    @Test
    void getCacheWithDataFormat() {
        String storageName = "testCacheWithDataFormat";
        storage = mongoStorageManager.getCacheWithDataFormat(storageName, String.class, "type");

        assertTrue(storage instanceof MongoStorage);
        assertEquals(storageName, ((MongoStorage) storage).mongoCollection.getNamespace().getCollectionName());
    }
}