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

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.kie.kogito.addons.quarkus.token.exchange.utils.CacheUtils;
import org.kie.kogito.addons.quarkus.token.exchange.utils.OidcClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;

/**
 * Handles token eviction events from the Caffeine cache and performs background token refresh operations.
 * This class is responsible for managing token lifecycle events including expiration and refresh.
 */
public class TokenEvictionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEvictionHandler.class);
    public static final String LOG_PREFIX_TOKEN_REFRESH = "Attempting background token refresh";
    public static final String LOG_PREFIX_REFRESH_COMPLETED = "Background refresh completed";
    public static final String LOG_PREFIX_FAILED_TO_REFRESH_TOKEN = "Failed to refresh token";

    public static final int REFRESH_THREAD_AMOUNT = 10;
    private final TokenCRUD tokenCRUD;

    private final ExecutorService tokenRefreshExecutor = Executors.newFixedThreadPool(REFRESH_THREAD_AMOUNT);

    public TokenEvictionHandler(TokenCRUD tokenCRUD) {
        this.tokenCRUD = tokenCRUD;
    }

    /**
     * Creates a removal listener that can be used with Caffeine cache.
     * 
     * @return A removal listener that handles token eviction events
     */
    public RemovalListener<String, CachedTokens> createRemovalListener() {
        return (key, value, cause) -> {
            if (value == null)
                return;

            LOGGER.info("Token cache eviction for cache key '{}' - Cause: {}", key, cause);
            onTokenExpired(key, value, cause);
        };
    }

    /**
     * Callback method called when tokens are evicted from the cache.
     * 
     * @param cacheKey The cache key of the evicted tokens
     * @param tokens The evicted token data
     * @param cause The reason for eviction (Caffeine's RemovalCause)
     */
    private void onTokenExpired(String cacheKey, CachedTokens tokens, RemovalCause cause) {
        LOGGER.warn("OAuth2 tokens for cache key '{}' have expired/been evicted: {}", cacheKey, cause);

        // Handle proactive token refresh when cache entry expires (which happens before actual token expiration)
        if (cause == RemovalCause.EXPIRED) {
            if (tokens.refreshToken() == null) {
                LOGGER.warn("OAuth2 tokens for cache key '{}' has no refresh token, the access token cannot be refreshed.", cacheKey);
                return;
            }

            if (!tokens.isExpiredNow()) {
                LOGGER.info("Triggering proactive token refresh for cache key '{}' (token still valid but refresh window reached)", cacheKey);
                refreshWithCachedToken(cacheKey, tokens.refreshToken());
            }
        } else if (cause == RemovalCause.EXPLICIT) {
            LOGGER.info("Deleting OAuth2 tokens with cache key '{}' from persistence system", cacheKey);
            tokenCRUD.deleteToken(cacheKey);
        }
    }

    /**
     * Refreshes tokens using a cached refresh token and updates the cache.
     * 
     * @param cacheKey The cache key for the tokens being refreshed
     * @param refreshToken The refresh token to use for getting new tokens
     */
    private void refreshWithCachedToken(String cacheKey, String refreshToken) {
        tokenRefreshExecutor.submit(() -> {
            try {
                LOGGER.info("{} - cache key '{}'", LOG_PREFIX_TOKEN_REFRESH, cacheKey);

                String authName = CacheUtils.extractAuthNameFromCacheKey(cacheKey);
                OidcClient client = OidcClientUtils.getExchangeTokenClient(authName);

                LOGGER.debug("Refreshing token for cache key '{}' using cached refresh token", cacheKey);

                Tokens refreshedTokens = client.getTokens(Collections.singletonMap("refresh_token", refreshToken))
                        .await().indefinitely();

                tokenCRUD.storeToken(cacheKey, refreshedTokens);

                LOGGER.info("{} - cache key '{}'", LOG_PREFIX_REFRESH_COMPLETED, cacheKey);
            } catch (Exception e) {
                LOGGER.error("{} - cache key '{}': {}", LOG_PREFIX_FAILED_TO_REFRESH_TOKEN, cacheKey, e);
            }
        });
    }
}
