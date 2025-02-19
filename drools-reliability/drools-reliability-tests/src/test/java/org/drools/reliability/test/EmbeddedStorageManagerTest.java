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
package org.drools.reliability.test;

import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.TestableStorageManager;
import org.drools.reliability.infinispan.EmbeddedStorageManager;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.core.StorageManagerFactory.SESSION_STORAGE_PREFIX;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MODE;
import static org.drools.reliability.test.util.TestConfigurationUtils.DROOLS_RELIABILITY_MODULE_TEST;
import static org.drools.util.Config.getConfig;

@EnabledIf("isEmbeddedInfinispan")
@ExtendWith(BeforeAllMethodExtension.class)
class EmbeddedStorageManagerTest {

    static {
        System.setProperty(INFINISPAN_STORAGE_ALLOWED_PACKAGES, "org.test.domain");
    }

    @BeforeEach
    public void setUp() {
        // Cache will be [METADATA_0, ___protobuf_metadata, ___script_cache, session_0_epDefault, session_1_epDefault]
        DefaultCacheManager cacheManager = ((EmbeddedStorageManager) StorageManagerFactory.get().getStorageManager()).getEmbeddedCacheManager();
        Configuration cacheConfiguration = ((EmbeddedStorageManager) StorageManagerFactory.get().getStorageManager()).getCacheConfiguration();
        cacheManager.createCache(SESSION_STORAGE_PREFIX + "0_" + "epDefault", cacheConfiguration);
        cacheManager.createCache(SESSION_STORAGE_PREFIX + "1_" + "epDefault", cacheConfiguration);
        cacheManager.createCache("METADATA_0", cacheConfiguration);
    }

    @AfterEach
    public void tearDown() {
        ((TestableStorageManager) StorageManagerFactory.get().getStorageManager()).restart();
    }

    static boolean isEmbeddedInfinispan() {
        return "INFINISPAN".equalsIgnoreCase(getConfig(DROOLS_RELIABILITY_MODULE_TEST))
                && "EMBEDDED".equalsIgnoreCase(getConfig(INFINISPAN_STORAGE_MODE));
    }

    @Test
    void removeAllSessionCaches_shouldLeaveNonSessionCache() {
        StorageManagerFactory.get().getStorageManager().removeAllSessionStorages();

        assertThat(StorageManagerFactory.get().getStorageManager().getStorageNames())
                .containsExactlyInAnyOrder("METADATA_0", "___protobuf_metadata", "___script_cache");
    }

    @Test
    void removeCachesBySessionId_shouldRemoveSpecifiedCacheOnly() {
        StorageManagerFactory.get().getStorageManager().removeStoragesBySessionId("1");

        assertThat(StorageManagerFactory.get().getStorageManager().getStorageNames())
                .containsExactlyInAnyOrder(SESSION_STORAGE_PREFIX + "0_" + "epDefault", "METADATA_0", "___protobuf_metadata", "___script_cache");
    }
}
