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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class ReliableNamedEntryPoint extends NamedEntryPoint {

    public ReliableNamedEntryPoint(EntryPointId entryPoint, EntryPointNode entryPointNode, ReteEvaluator reteEvaluator) {
        super(entryPoint, entryPointNode, reteEvaluator);
    }

    @Override
    protected ObjectStore createObjectStore(EntryPointId entryPoint, RuleBaseConfiguration conf, ReteEvaluator reteEvaluator) {
        boolean storesOnlyStrategy = reteEvaluator.getSessionConfiguration().getPersistedSessionOption().getPersistenceStrategy() == PersistedSessionOption.PersistenceStrategy.STORES_ONLY;
        return storesOnlyStrategy ?
                SimpleReliableObjectStoreFactory.get().createSimpleReliableObjectStore(StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(reteEvaluator, "ep" + getEntryPointId()),
                        reteEvaluator.getSessionConfiguration().getPersistedSessionOption()) :
                new FullReliableObjectStore(StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(reteEvaluator, "ep" + getEntryPointId()));
    }

    public void safepoint() {
        ((SimpleReliableObjectStore) getObjectStore()).safepoint();
    }
}
