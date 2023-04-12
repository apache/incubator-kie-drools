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

import org.drools.reliability.core.CacheManagerFactory;
import org.drools.reliability.core.TestableCacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.core.CacheManagerFactory.*;
import static org.drools.util.Config.getConfig;

/**
 *  This class is a unit test for EmbeddedCacheManager methods with a fake cacheManager instead of Infinispan DefaultCacheManager.
 */
@DisabledIf("isRemote")
@ExtendWith(BeforeAllMethodExtension.class)
class EmbeddedCacheManagerTest {

    static {
        System.setProperty(RELIABILITY_CACHE_ALLOWED_PACKAGES, "org.test.domain");
    }

    @AfterEach
    public void tearDown() {
        ((TestableCacheManager)CacheManagerFactory.get().getCacheManager()).restart(); // make sure that FakeCacheManager is removed
    }

    private static boolean isRemote() {
        return "REMOTE".equalsIgnoreCase(getConfig(RELIABILITY_CACHE_MODE));
    }

    @Test
    void removeAllSessionCaches_shouldLeaveNonSessionCache() {
        ((InfinispanCacheManager) CacheManagerFactory.get().getCacheManager()).setEmbeddedCacheManager(new FakeCacheManager());

        assertThat(CacheManagerFactory.get().getCacheManager().getCacheNames()).containsExactlyInAnyOrder(
                SESSION_CACHE_PREFIX + "0_" + "epDefault", SESSION_CACHE_PREFIX + "1_" + "epDefault", "METADATA_0");

        CacheManagerFactory.get().getCacheManager().removeAllSessionCaches();

        assertThat(CacheManagerFactory.get().getCacheManager().getCacheNames()).containsExactly("METADATA_0");
    }

    @Test
    void removeCachesBySessionId_shouldRemoveSpecifiedCacheOnly() {
        ((InfinispanCacheManager) CacheManagerFactory.get().getCacheManager()).setEmbeddedCacheManager(new FakeCacheManager());

        assertThat(CacheManagerFactory.get().getCacheManager().getCacheNames()).containsExactlyInAnyOrder(
                SESSION_CACHE_PREFIX + "0_" + "epDefault", SESSION_CACHE_PREFIX + "1_" + "epDefault", "METADATA_0");

        CacheManagerFactory.get().getCacheManager().removeCachesBySessionId("1");

        assertThat(CacheManagerFactory.get().getCacheManager().getCacheNames()).containsExactlyInAnyOrder(SESSION_CACHE_PREFIX + "0_" + "epDefault", "METADATA_0");
    }

    public static class FakeCacheManager extends DefaultCacheManager {

        private Map<String, Object> cacheMap = new ConcurrentHashMap<>();

        public FakeCacheManager() {
            cacheMap.put(SESSION_CACHE_PREFIX + "0_" + "epDefault", new Object());
            cacheMap.put(SESSION_CACHE_PREFIX + "1_" + "epDefault", new Object());
            cacheMap.put("METADATA_0", new Object());
        }

        @Override
        public Set<String> getCacheNames() {
            return cacheMap.keySet();
        }

        @Override
        public boolean cacheExists(String cacheName) {
            return cacheMap.containsKey(cacheName);
        }

        @Override
        public void removeCache(String cacheName) {
            cacheMap.remove(cacheName);
        }

        @Override
        public void stop() {
            // do nothing
        }
    }
}
