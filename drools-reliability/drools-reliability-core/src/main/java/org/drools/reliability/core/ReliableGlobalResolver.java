/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.core.rule.accessor.GlobalResolver;

import java.util.HashMap;
import java.util.Map;

public class ReliableGlobalResolver implements GlobalResolver {
    private final Map<String, Object> cache;

    private final Map<String, Object> toBeRefreshed = new HashMap<>();

    public ReliableGlobalResolver(Map<String, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object resolveGlobal(String identifier) {
        // Use an in-memory global reference. Avoid getting a stale object from cache
        if (toBeRefreshed.containsKey(identifier)) {
            return toBeRefreshed.get(identifier);
        }
        Object global = cache.get(identifier);
        toBeRefreshed.put(identifier, global);
        return global;
    }

    @Override
    public void setGlobal(String identifier, Object value) {
        cache.put(identifier, value);
    }

    @Override
    public void removeGlobal(String identifier) {
        cache.remove(identifier);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public void updateCache() {
        if (!toBeRefreshed.isEmpty()) {
            toBeRefreshed.forEach(cache::put);
            toBeRefreshed.clear();
        }
    }
}
