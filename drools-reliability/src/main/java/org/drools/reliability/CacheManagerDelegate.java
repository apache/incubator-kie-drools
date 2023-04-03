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
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.runtime.conf.PersistedSessionOption;

import static org.drools.reliability.CacheManager.DELIMITER;
import static org.drools.reliability.CacheManager.SESSION_CACHE_PREFIX;

public abstract class CacheManagerDelegate {

    protected abstract void initCacheManager();

    protected abstract <k, V> BasicCache<k, V> getOrCreateCacheForSession(ReteEvaluator reteEvaluator, String cacheName);

    protected abstract void close();

    protected abstract void removeCache(String cacheName);

    protected abstract void removeCachesBySessionId(String sessionId);

    protected abstract void removeAllSessionCaches();

    protected abstract Set<String> getCacheNames();

    protected abstract void setRemoteCacheManager(RemoteCacheManager remoteCacheManager);

    protected String createCacheId(ReteEvaluator reteEvaluator, String cacheName) {
        return SESSION_CACHE_PREFIX + getSessionIdentifier(reteEvaluator) + DELIMITER + cacheName;
    }

    private long getSessionIdentifier(ReteEvaluator reteEvaluator) {
        PersistedSessionOption persistedSessionOption = reteEvaluator.getSessionConfiguration().getPersistedSessionOption();
        if (persistedSessionOption != null) {
            return persistedSessionOption.isNewSession() ? reteEvaluator.getIdentifier() : persistedSessionOption.getSessionId();
        } else {
            throw new ReliabilityConfigurationException("PersistedSessionOption has to be configured when drools-reliability is used");
        }
    }

    //--- test purpose

    abstract void restart();

    abstract void restartWithCleanUp();

    abstract void setEmbeddedCacheManager(DefaultCacheManager cacheManager);

    abstract ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder();

}
