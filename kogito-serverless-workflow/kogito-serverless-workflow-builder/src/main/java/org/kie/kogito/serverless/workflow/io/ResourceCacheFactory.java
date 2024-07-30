/*
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
package org.kie.kogito.serverless.workflow.io;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ResourceCacheFactory {
    private static final AtomicReference<ResourceCache> cache = new AtomicReference<>(new LocalResourceCache());

    public static ResourceCache getCache() {
        return cache.get();
    }

    private static class LocalResourceCache implements ResourceCache {
        private final Map<String, byte[]> map = Collections.synchronizedMap(new WeakHashMap<>());

        @Override
        public byte[] get(String uri, Supplier<byte[]> retrieveCall) {
            return map.computeIfAbsent(uri, u -> retrieveCall.get());
        }

    }

    protected static void setResourceCache(ResourceCache newCache) {
        cache.set(newCache);
    }

    private ResourceCacheFactory() {
    }
}
