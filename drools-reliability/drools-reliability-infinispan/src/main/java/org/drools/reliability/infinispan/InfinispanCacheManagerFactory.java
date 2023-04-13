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

package org.drools.reliability.infinispan;

import org.drools.reliability.core.CacheManager;
import org.drools.reliability.core.CacheManagerFactory;

import static org.drools.util.Config.getConfig;

public class InfinispanCacheManagerFactory implements CacheManagerFactory {

    private final CacheManager cacheManager;

    public InfinispanCacheManagerFactory() {
        if ("REMOTE".equalsIgnoreCase(getConfig(RELIABILITY_CACHE_MODE))) {
            cacheManager = RemoteCacheManager.INSTANCE;
        } else {
            cacheManager = EmbeddedCacheManager.INSTANCE;
        }

        cacheManager.initCacheManager();
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
