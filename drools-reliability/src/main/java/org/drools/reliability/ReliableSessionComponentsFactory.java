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

package org.drools.reliability;

import java.util.concurrent.locks.Lock;

import org.drools.core.common.ObjectStore;
import org.drools.kiesession.session.SessionComponentsFactory;

public class ReliableSessionComponentsFactory implements SessionComponentsFactory {

    @Override
    public ObjectStore createIdentityObjectStore(String entryPointName) {
        return new ReliableObjectStore(CacheManager.INSTANCE.getOrCreateCache(entryPointName));
    }

    @Override
    public ObjectStore createClassAwareObjectStore(String entryPointName, boolean isEqualityBehaviour, Lock lock) {
        // When using reliability the only store available is the identity one
        return createIdentityObjectStore(entryPointName);
    }
}
