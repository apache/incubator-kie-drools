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
package org.drools.persistence.api;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class TransactionManagerHelper {

    private static final String APP_UPDETEABLE_RESOURCE = "app-updateable-resource";
    private static final String CMD_UPDETEABLE_RESOURCE = "cmd-updateable-resource";

    public static void registerTransactionSyncInContainer(TransactionManager txm, OrderedTransactionSynchronization synchronization) {
        TransactionSynchronizationContainer container = (TransactionSynchronizationContainer)txm.getResource(TransactionSynchronizationContainer.RESOURCE_KEY);
        if (container == null) {
            container = new TransactionSynchronizationContainer();
            txm.registerTransactionSynchronization( container );
            txm.putResource(TransactionSynchronizationContainer.RESOURCE_KEY, container);
        }
        container.addTransactionSynchronization(synchronization);
    }

    @SuppressWarnings("unchecked")
    public static void addToUpdatableSet(TransactionManager txm, Transformable transformable) {
        if (transformable == null) {
            return;
        }
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            toBeUpdated = new LinkedHashSet<>();
            txm.putResource(APP_UPDETEABLE_RESOURCE, toBeUpdated);
        }
        toBeUpdated.add(transformable);
    }

    @SuppressWarnings("unchecked")
    public static void removeFromUpdatableSet(TransactionManager txm, Transformable transformable) {
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            return;
        }
        toBeUpdated.remove(transformable);
    }

    @SuppressWarnings("unchecked")
    public static Set<Transformable> getUpdateableSet(TransactionManager txm) {
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            return Collections.emptySet();
        }

        return new LinkedHashSet<>(toBeUpdated);
    }
}
