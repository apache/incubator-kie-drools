/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.trusty.storage.postgresql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.postgresql.PostgresStorage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseTransactionalStorageTest {

    private static final String KEY = "KEY";
    private static final String VALUE = "VALUE";

    private static class StringStorage extends BaseTransactionalStorage<String> {

        public StringStorage(final PostgresStorage<String> delegate) {
            super(delegate);
        }
    }

    private static PostgresStorage<String> delegate;

    private static BaseTransactionalStorage<String> storage;

    @BeforeAll
    @SuppressWarnings("unchecked")
    public static void setup() {
        delegate = mock(PostgresStorage.class);
        storage = new StringStorage(delegate);
    }

    @Test
    public void testObjectCreatedListener() {
        storage.objectCreatedListener();
        verify(delegate).objectCreatedListener();
    }

    @Test
    public void testObjectUpdatedListener() {
        storage.objectUpdatedListener();
        verify(delegate).objectUpdatedListener();
    }

    @Test
    public void testObjectRemovedListener() {
        storage.objectRemovedListener();
        verify(delegate).objectRemovedListener();
    }

    @Test
    public void testQuery() {
        storage.query();
        verify(delegate).query();
    }

    @Test
    public void testGet() {
        storage.get(KEY);
        verify(delegate).get(KEY);
    }

    @Test
    public void testPut() {
        storage.put(KEY, VALUE);
        verify(delegate).put(KEY, VALUE);
    }

    @Test
    public void testRemove() {
        storage.remove(KEY);
        verify(delegate).remove(KEY);
    }

    @Test
    public void testContainsKey() {
        storage.containsKey(KEY);
        verify(delegate).containsKey(KEY);
    }

    @Test
    public void testEntries() {
        storage.entries();
        verify(delegate).entries();
    }

    @Test
    public void testClear() {
        storage.clear();
        verify(delegate).clear();
    }

    @Test
    public void testGetRootType() {
        storage.getRootType();
        verify(delegate).getRootType();
    }
}
