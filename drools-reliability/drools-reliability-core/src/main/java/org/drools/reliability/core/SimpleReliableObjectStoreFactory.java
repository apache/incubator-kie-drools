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
import org.kie.api.internal.utils.KieService;
import org.kie.api.runtime.conf.PersistedSessionOption;

public interface SimpleReliableObjectStoreFactory extends KieService {

    SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption);

    class Tag {

        private Tag() {
            // hide constructor
        }

        private static String reliabilityPersistanceLayer = null;
    }

    class Holder {

        private static final SimpleReliableObjectStoreFactory INSTANCE = createInstance();

        private Holder() {
        }

        static SimpleReliableObjectStoreFactory createInstance() {
            SimpleReliableObjectStoreFactory factory = KieService.loadWithTag(SimpleReliableObjectStoreFactory.class, Tag.reliabilityPersistanceLayer);
            if (factory == null) {
                return new SimpleSerializationReliableObjectStoreFactory();
            }
            return factory;
        }
    }

    /**
     * Use this method first to specify reliabilityPersistanceLayer when you have dependencies covering multiple persistence layers (e.g. infinispan and core)
     * Once a factory is instantiated, get() is enough to get the same instance.
     */
    static SimpleReliableObjectStoreFactory get(String reliabilityPersistanceLayer) {
        if (Tag.reliabilityPersistanceLayer != null && !Tag.reliabilityPersistanceLayer.equals(reliabilityPersistanceLayer)) {
            throw new IllegalStateException("You must call the same service with the same reliabilityPersistanceLayer. " +
                                            "Previous reliabilityPersistanceLayer was " + Tag.reliabilityPersistanceLayer +
                                            " and current reliabilityPersistanceLayer is " + reliabilityPersistanceLayer);
        }
        Tag.reliabilityPersistanceLayer = reliabilityPersistanceLayer;
        return SimpleReliableObjectStoreFactory.Holder.INSTANCE;
    }

    static SimpleReliableObjectStoreFactory get() {
        return SimpleReliableObjectStoreFactory.Holder.INSTANCE;
    }
}
