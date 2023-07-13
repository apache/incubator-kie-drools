/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.h2mvstore;

import org.drools.reliability.core.StorageManager;
import org.drools.reliability.core.StorageManagerFactory;

public class H2MVStoreStorageManagerFactory implements StorageManagerFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    private final StorageManager storageManager;

    public H2MVStoreStorageManagerFactory() {
        storageManager = H2MVStoreStorageManager.INSTANCE;

        // initStorageManager() is called by StorageManagerFactory.Holder.createInstance()
    }

    @Override
    public StorageManager getStorageManager() {
        return storageManager;
    }

    @Override
    public int servicePriority() {
        return servicePriorityValue;
    }
}
