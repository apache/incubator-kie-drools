/**
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
package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class SimpleSerializationReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage) {
        return new SimpleSerializationReliableObjectStore(storage);
    }

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption) {
        switch (persistedSessionOption.getPersistenceObjectsStrategy()){
            case SIMPLE: return new SimpleSerializationReliableObjectStore(storage);
            case OBJECT_REFERENCES: return new SimpleSerializationReliableRefObjectStore(storage);
            default: throw new UnsupportedOperationException();
        }
    }

    @Override
    public int servicePriority() {
        return 0;
    }

    @Override
    public String serviceTag() {
        return "core";
    }
}
