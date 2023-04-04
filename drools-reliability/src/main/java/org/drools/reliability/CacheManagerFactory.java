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

import java.util.Set;

import org.drools.core.common.ReteEvaluator;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.manager.DefaultCacheManager;

public enum CacheManagerFactory {

    INSTANCE;

    public static final String SESSION_CACHE_PREFIX = "session_";
    public static final String DELIMITER = "_";

    public static final String CACHE_MANAGER_MODE = "drools.reliability.cache.manager.mode";
    public static final String CACHE_MANAGER_REMOTE_HOST = "drools.reliability.cache.manager.remote.host";
    public static final String CACHE_MANAGER_REMOTE_PORT = "drools.reliability.cache.manager.remote.port";
    public static final String CACHE_MANAGER_REMOTE_USER = "drools.reliability.cache.manager.remote.user";
    public static final String CACHE_MANAGER_REMOTE_PASS = "drools.reliability.cache.manager.remote.pass";

    private final CacheManager cacheManager;

    CacheManagerFactory() {
        String modeValue = System.getProperty(CACHE_MANAGER_MODE, "EMBEDDED");
        if (modeValue.equalsIgnoreCase("REMOTE")) {
            cacheManager = RemoteCacheManager.INSTANCE;
        } else {
            cacheManager = EmbeddedCacheManager.INSTANCE;
        }

        cacheManager.initCacheManager();
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
