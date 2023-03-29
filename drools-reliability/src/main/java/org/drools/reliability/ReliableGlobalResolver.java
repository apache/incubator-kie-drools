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

import java.util.HashSet;
import java.util.Set;

import org.drools.core.rule.accessor.GlobalResolver;
import org.infinispan.Cache;

public class ReliableGlobalResolver implements GlobalResolver {
    private final Cache<String, Object> cache;

    private final Set<String> toBeRefreshed = new HashSet<>();

    public ReliableGlobalResolver(Cache<String, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object resolveGlobal(String identifier) {
        toBeRefreshed.add(identifier);
        return cache.get(identifier);
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
            toBeRefreshed.forEach( id -> cache.put(id, cache.get(id)));
            toBeRefreshed.clear();
        }
    }
}
