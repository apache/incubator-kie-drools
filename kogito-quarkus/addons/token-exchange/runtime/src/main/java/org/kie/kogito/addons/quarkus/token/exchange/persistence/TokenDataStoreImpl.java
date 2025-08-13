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
package org.kie.kogito.addons.quarkus.token.exchange.persistence;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.addons.quarkus.token.exchange.cache.CachedTokens;
import org.kie.kogito.addons.quarkus.token.exchange.persistence.model.TokenCacheRecord;
import org.kie.kogito.addons.quarkus.token.exchange.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Database-backed implementation of TokenDataStore using JDBC.
 */
@ApplicationScoped
public class TokenDataStoreImpl implements TokenDataStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenDataStoreImpl.class);
    public static final String LOG_PREFIX_USED_REPOSITORY = "Token DataStore initialized with repository";

    @Inject
    private TokenCacheRepository repository;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("{}: {}", LOG_PREFIX_USED_REPOSITORY,
                repository.getClass().getSimpleName());
    }

    @Override
    public void store(String cacheKey, CachedTokens tokens) {
        try {
            String processInstanceId = CacheUtils.extractProcessInstanceIdFromCacheKey(cacheKey);
            String authName = CacheUtils.extractAuthNameFromCacheKey(cacheKey);

            TokenCacheRecord record = new TokenCacheRecord(
                    processInstanceId, authName,
                    tokens.accessToken(), tokens.refreshToken(), tokens.expirationTime());

            repository.save(record);
            LOGGER.debug("Stored token cache entry for key: {}", cacheKey);

        } catch (Exception e) {
            LOGGER.error("Failed to store token cache entry for key: {}", cacheKey, e);
            throw new RuntimeException("Failed to store tokens", e);
        }
    }

    @Override
    public Optional<CachedTokens> retrieve(String cacheKey) {
        try {
            return repository.findByCacheKey(cacheKey)
                    .filter(record -> !isExpired(record))
                    .map(this::recordToTokens);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve token cache entry for key: {}", cacheKey, e);
            return Optional.empty();
        }
    }

    @Override
    public void remove(String cacheKey) {
        try {
            repository.deleteByCacheKey(cacheKey);
            LOGGER.debug("Removed token cache entry for key: {}", cacheKey);
        } catch (Exception e) {
            LOGGER.error("Failed to remove token cache entry for key: {}", cacheKey, e);
        }
    }

    private CachedTokens recordToTokens(TokenCacheRecord record) {
        return new CachedTokens(
                record.accessToken(),
                record.refreshToken(),
                record.expirationTime());
    }

    private TokenEntry recordToTokenEntry(TokenCacheRecord record) {
        String cacheKey = CacheUtils.buildCacheKey(record.processInstanceId(), record.authName());
        return new TokenEntry(cacheKey, recordToTokens(record));
    }

    private boolean isExpired(TokenCacheRecord record) {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) >= record.expirationTime();
    }
}
