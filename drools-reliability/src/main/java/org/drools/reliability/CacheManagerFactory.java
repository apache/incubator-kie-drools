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

public enum CacheManagerFactory {

    INSTANCE;

    public static final String SESSION_CACHE_PREFIX = "session_";
    public static final String SHARED_CACHE_PREFIX = "shared_";
    public static final String DELIMITER = "_";

    public static final String RELIABILITY_CACHE = "drools.reliability.cache";
    public static final String RELIABILITY_CACHE_ALLOWED_PACKAGES = RELIABILITY_CACHE + ".allowedpackages";
    public static final String RELIABILITY_CACHE_DIRECTORY = RELIABILITY_CACHE + ".dir";
    public static final String RELIABILITY_CACHE_MODE = RELIABILITY_CACHE + ".mode";
    public static final String RELIABILITY_CACHE_REMOTE_HOST = RELIABILITY_CACHE + ".remote.host";
    public static final String RELIABILITY_CACHE_REMOTE_PORT = RELIABILITY_CACHE + ".remote.port";
    public static final String RELIABILITY_CACHE_REMOTE_USER = RELIABILITY_CACHE + ".remote.user";
    public static final String RELIABILITY_CACHE_REMOTE_PASS = RELIABILITY_CACHE + ".remote.pass";

    private final CacheManager cacheManager;

    CacheManagerFactory() {
        if ("REMOTE".equalsIgnoreCase(System.getProperty(RELIABILITY_CACHE_MODE))) {
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
