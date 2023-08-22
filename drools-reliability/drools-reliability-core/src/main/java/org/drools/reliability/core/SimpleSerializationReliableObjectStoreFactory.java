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

package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class SimpleSerializationReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage) {
        return new SimpleSerializationReliableObjectStore(storage);
    }

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption) {
        switch (persistedSessionOption.getPersistenceObjectsStrategy()){
            case OBJECT_REFERENCES: return new SimpleSerializationReliableRefObjectStore(storage);
            default: return new SimpleSerializationReliableObjectStore(storage);
        }
    }

    @Override
    public int servicePriority() {
        return servicePriorityValue;
    }
}
