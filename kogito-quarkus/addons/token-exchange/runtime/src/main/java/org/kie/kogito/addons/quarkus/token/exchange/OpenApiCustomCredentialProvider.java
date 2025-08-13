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
package org.kie.kogito.addons.quarkus.token.exchange;

import java.util.Collections;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.addons.quarkus.token.exchange.cache.CachedTokens;
import org.kie.kogito.addons.quarkus.token.exchange.cache.TokenCRUD;
import org.kie.kogito.addons.quarkus.token.exchange.cache.TokenEvictionHandler;
import org.kie.kogito.addons.quarkus.token.exchange.cache.TokenPolicyManager;
import org.kie.kogito.addons.quarkus.token.exchange.persistence.TokenDataStoreImpl;
import org.kie.kogito.addons.quarkus.token.exchange.utils.CacheUtils;
import org.kie.kogito.addons.quarkus.token.exchange.utils.ConfigReaderUtils;
import org.kie.kogito.addons.quarkus.token.exchange.utils.OidcClientUtils;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Scheduler;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClientException;
import io.quarkus.oidc.client.Tokens;
import io.quarkus.runtime.configuration.ConfigurationException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthProvider.getHeaderName;

/**
 * Custom credential provider that supports OAuth2 token exchange and caching using Caffeine cache
 * with database persistence and per-token expiration management.
 *
 * Configuration properties:
 * - sonataflow.security.{authName}.token-exchange.enabled: Enable token exchange for the specified auth name
 * - sonataflow.security.{authName}.token-exchange.proactive-refresh-seconds: Number of seconds before token expiration to refresh cache (default: 300)
 */
@ApplicationScoped
@Alternative
@Specializes
@Priority(200)
public class OpenApiCustomCredentialProvider extends ConfigCredentialsProvider implements TokenCRUD {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiCustomCredentialProvider.class);
    public static final String LOG_PREFIX_STARTING_TOKEN_EXCHANGE = "STARTING TOKEN EXCHANGE";
    public static final String LOG_PREFIX_COMPLETED_TOKEN_EXCHANGE = "COMPLETED TOKEN EXCHANGE";
    public static final String LOG_PREFIX_FAILED_TOKEN_EXCHANGE = "FAILED TOKEN EXCHANGE";

    @Inject
    private TokenDataStoreImpl dataStore;
    private LoadingCache<String, CachedTokens> tokenCache;

    @PostConstruct
    public void initCache() {
        this.tokenCache = Caffeine.newBuilder()
                .expireAfter(TokenPolicyManager.createTokenExpiryPolicy())
                .scheduler(Scheduler.systemScheduler())
                .removalListener(new TokenEvictionHandler(this).createRemovalListener()) // Use TokenEvictionHandler's removal listener
                .build(this::loadTokenFromDatabase);

        LOGGER.info("Caffeine token cache initialized with per-token expiration");
    }

    /**
     * Load token from database when cache miss occurs during refresh
     */
    private CachedTokens loadTokenFromDatabase(String cacheKey) {
        Optional<CachedTokens> optionalToken = dataStore.retrieve(cacheKey);
        if (optionalToken.isPresent()) {
            if (optionalToken.get().isExpiredNow()) {
                dataStore.remove(cacheKey);
            } else {
                return optionalToken.get();
            }
        }

        return null;
    }

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        LOGGER.debug("Calling OpenApiCustomCredentialProvider.getOauth2BearerToken for {}", input.getAuthName());
        String authorizationHeaderName = Optional.ofNullable(getHeaderName(input.getOpenApiSpecId(), input.getAuthName())).orElse(HttpHeaders.AUTHORIZATION);
        boolean exchangeToken = ConfigReaderUtils.getTokenExchangeEnabledPropertyValue(input).orElse(false);

        if (exchangeToken) {
            LOGGER.info("Oauth2 token exchange enabled for {}, will generate tokens...", input.getAuthName());

            String cacheKey = CacheUtils.buildCacheKey(input);
            String accessToken = getAccessTokenFromCache(cacheKey);

            if (accessToken == null) {
                accessToken = performTokenExchange(input, cacheKey, authorizationHeaderName);
            }
            return Optional.ofNullable(accessToken);
        }
        return Optional.empty();
    }

    private String getAccessTokenFromCache(String cacheKey) {
        if (tokenCache == null) {
            return null;
        }

        try {
            CachedTokens cachedTokens = tokenCache.get(cacheKey);
            if (cachedTokens != null) {
                LOGGER.debug("Found cached tokens for cache key '{}'", cacheKey);
                if (!cachedTokens.isExpiredNow()) {
                    LOGGER.debug("Using valid cached access token for cache key '{}'", cacheKey);
                    return cachedTokens.accessToken();
                } else {
                    LOGGER.info("Cached token for cache key '{}' is expired, falling back to new token exchange", cacheKey);
                    tokenCache.invalidate(cacheKey);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to retrieve token from cache for key '{}': {}", cacheKey, e.getMessage());
        }
        return null;
    }

    private String performTokenExchange(CredentialsContext input, String cacheKey, String authorizationHeaderName) {
        LOGGER.info("Performing token exchange for '{}'", cacheKey);

        String accessToken = input.getRequestContext().getHeaderString(authorizationHeaderName);
        if (ConversionUtils.isEmpty(accessToken)) {
            throw new ConfigurationException("An access token is required in the header %s (default is %s) but none was provided".formatted(authorizationHeaderName, HttpHeaders.AUTHORIZATION));
        }

        return exchangeToken(accessToken, OidcClientUtils.getExchangeTokenClient(input.getAuthName()), input, cacheKey);
    }

    private String exchangeToken(String token, OidcClient exchangeTokenClient, CredentialsContext input, String cacheKey) {
        OidcClientConfig.Grant.Type exchangeTokenGrantType = ConfigProvider.getConfig()
                .getValue("quarkus.oidc-client.%s.grant.type".formatted(input.getAuthName()), OidcClientConfig.Grant.Type.class);
        try {
            LOGGER.info("{} - Cache key: {}, Thread: {}", LOG_PREFIX_STARTING_TOKEN_EXCHANGE, cacheKey, Thread.currentThread().getName());

            Tokens tokens = exchangeTokenClient.getTokens(Collections.singletonMap(
                    OidcClientUtils.getExchangeTokenProperty(exchangeTokenGrantType), token)).await().indefinitely();

            this.storeToken(cacheKey, tokens);

            LOGGER.info("{} - Cache key: {}, Thread: {}, Token expires at: {}",
                    LOG_PREFIX_COMPLETED_TOKEN_EXCHANGE, cacheKey, Thread.currentThread().getName(),
                    tokens.getAccessTokenExpiresAt());
            return tokens.getAccessToken();

        } catch (OidcClientException e) {
            LOGGER.error("{} - Cache key: {}, Thread: {}, Error: {}", LOG_PREFIX_FAILED_TOKEN_EXCHANGE, cacheKey, Thread.currentThread().getName(), e.getMessage());
            return token;
        }
    }

    /**
     * Store tokens in cache - used by TokenEvictionHandler for refresh operations
     */

    @Override
    public void storeToken(String cacheKey, Tokens tokens) {
        CachedTokens cachedTokens = new CachedTokens(
                tokens.getAccessToken(),
                tokens.getRefreshToken(),
                tokens.getAccessTokenExpiresAt());

        this.tokenCache.put(cacheKey, cachedTokens);

        try {
            dataStore.store(cacheKey, cachedTokens);
            LOGGER.debug("Persisted token for cache key {} using {}", cacheKey, dataStore.getClass().getSimpleName());
        } catch (Exception e) {
            LOGGER.error("Failed to persist token for key {} using {} ", cacheKey, dataStore.getClass().getSimpleName(), e);
        }

        LOGGER.debug("Stored token in cache for key: {}", cacheKey);
    }

    @Override
    public void deleteToken(String cacheKey) {
        try {
            dataStore.remove(cacheKey);
            LOGGER.debug("Removed token for cache key {} using {}", cacheKey, dataStore.getClass().getSimpleName());
        } catch (Exception e) {
            LOGGER.error("Failed to remove token for key {} using {} ", cacheKey, dataStore.getClass().getSimpleName(), e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (tokenCache != null) {
            tokenCache.invalidateAll();
            LOGGER.info("Token cache cleared on shutdown");
        }
    }
}
