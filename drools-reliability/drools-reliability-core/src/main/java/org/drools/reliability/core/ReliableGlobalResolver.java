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
import org.drools.base.rule.accessor.GlobalResolver;

import java.util.HashMap;
import java.util.Map;

public class ReliableGlobalResolver implements GlobalResolver {
    protected final Storage<String, Object> storage;

    protected final Map<String, Object> toBeRefreshed = new HashMap<>();

    public ReliableGlobalResolver(Storage<String, Object> storage) {
        this.storage = storage;
    }

    @Override
    public Object resolveGlobal(String identifier) {
        // Use an in-memory global reference. Avoid getting a stale object from storage
        if (toBeRefreshed.containsKey(identifier)) {
            return toBeRefreshed.get(identifier);
        }
        Object global = storage.get(identifier);
        toBeRefreshed.put(identifier, global);
        return global;
    }

    @Override
    public void setGlobal(String identifier, Object value) {
        storage.put(identifier, value);
    }

    @Override
    public void removeGlobal(String identifier) {
        storage.remove(identifier);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    public void updateStorage() {
        if (!toBeRefreshed.isEmpty()) {
            toBeRefreshed.forEach(storage::put);
            toBeRefreshed.clear();
        }
    }
}
