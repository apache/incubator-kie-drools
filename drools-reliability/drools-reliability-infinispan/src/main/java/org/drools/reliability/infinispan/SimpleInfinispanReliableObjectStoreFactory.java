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
package org.drools.reliability.infinispan;

import org.drools.core.common.Storage;
import org.drools.reliability.core.SimpleReliableObjectStore;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.SimpleSerializationReliableObjectStore;
import org.drools.reliability.core.SimpleSerializationReliableRefObjectStore;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.StoredObject;
import org.drools.reliability.infinispan.proto.SimpleProtoStreamReliableObjectStore;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleInfinispanReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleInfinispanReliableObjectStoreFactory.class);

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption) {
        if (((InfinispanStorageManager)StorageManagerFactory.get().getStorageManager()).isProtoStream()) {
            LOG.debug("Using SimpleProtoStreamReliableObjectStore");
            return new SimpleProtoStreamReliableObjectStore(storage);
        } else {
            if (persistedSessionOption.getPersistenceObjectsStrategy()== PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES){
                LOG.debug("Using SimpleSerializationReliableRefObjectStore");
                return new SimpleSerializationReliableRefObjectStore(storage);
            }else{
                LOG.debug("Using SimpleSerializationReliableObjectStore");
                return new SimpleSerializationReliableObjectStore(storage);
            }
        }
    }

    @Override
    public int servicePriority() {
        return 0;
    }

    @Override
    public String serviceTag() {
        return "infinispan";
    }

}
