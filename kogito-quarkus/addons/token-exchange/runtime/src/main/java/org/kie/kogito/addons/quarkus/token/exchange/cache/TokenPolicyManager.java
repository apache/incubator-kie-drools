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

package org.kie.kogito.addons.quarkus.token.exchange.cache;

import java.util.concurrent.TimeUnit;

import org.kie.kogito.addons.quarkus.token.exchange.utils.CacheUtils;
import org.kie.kogito.addons.quarkus.token.exchange.utils.ConfigReaderUtils;
import org.kie.kogito.services.context.ProcessInstanceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Expiry;

public class TokenPolicyManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenPolicyManager.class);

    /**
     * Creates an expiry policy that uses each token's actual expiration time
     */
    public static Expiry<String, CachedTokens> createTokenExpiryPolicy() {
        return new Expiry<>() {
            @Override
            public long expireAfterCreate(String key, CachedTokens value, long currentTime) {
                return calculateTimeToExpiration(key, value);
            }

            @Override
            public long expireAfterUpdate(String key, CachedTokens value, long currentTime, long currentDuration) {
                return calculateTimeToExpiration(key, value);
            }

            @Override
            public long expireAfterRead(String key, CachedTokens value, long currentTime, long currentDuration) {
                return currentDuration; // Don't change expiration on read
            }
        };
    }

    /**
     * Calculate time to expiration based on token's actual expiration time minus proactive refresh buffer.
     * This method sets the process instance context from the cache key for proper logging.
     */
    private static long calculateTimeToExpiration(String cacheKey, CachedTokens tokens) {
        // Extract process instance ID from cache key and set context for logging
        ProcessInstanceContext.setProcessInstanceId(CacheUtils.extractProcessInstanceIdFromCacheKey(cacheKey));

        try {
            String authName = CacheUtils.extractAuthNameFromCacheKey(cacheKey);
            long proactiveRefreshSeconds = Math.max(0, ConfigReaderUtils.getProactiveRefreshSeconds(authName));

            long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            long tokenExpirationSeconds = tokens.expirationTime();

            // Schedule refresh proactiveRefreshSeconds before actual expiration
            long refreshTimeSeconds = tokenExpirationSeconds - proactiveRefreshSeconds;
            long timeToRefreshSeconds = Math.max(0, refreshTimeSeconds - currentTimeSeconds);

            // Convert to nanoseconds for Caffeine
            long timeToRefreshNanos = TimeUnit.SECONDS.toNanos(timeToRefreshSeconds);

            LOGGER.info("Token for key '{}' will be refreshed in {} seconds (expires at {}, refresh buffer {} seconds, currentTimeSeconds: {})",
                    cacheKey, timeToRefreshSeconds, tokenExpirationSeconds, proactiveRefreshSeconds, java.time.Instant.now().getEpochSecond());

            return timeToRefreshNanos;
        } finally {
            // Reset to general context after logging
            ProcessInstanceContext.clear();
        }
    }
}
