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

import org.kie.kogito.addons.quarkus.token.exchange.persistence.model.TokenCacheRecord;

/**
 * Interface for token cache repository operations.
 * Provides contract for token cache persistence operations.
 */
public interface TokenCacheRepository {

    /**
     * Saves a token cache record. If a record with the same key exists, it will be updated.
     * Otherwise, a new record will be inserted.
     *
     * @param record the token cache record to save
     * @return the saved record
     */
    TokenCacheRecord save(TokenCacheRecord record);

    /**
     * Finds a token cache record by process instance ID and auth name.
     *
     * @param processInstanceId the process instance ID
     * @param authName the authentication name
     * @return an Optional containing the record if found, empty otherwise
     */
    Optional<TokenCacheRecord> findByKey(String processInstanceId, String authName);

    /**
     * Finds a token cache record by cache key.
     *
     * @param cacheKey the cache key
     * @return an Optional containing the record if found, empty otherwise
     */
    Optional<TokenCacheRecord> findByCacheKey(String cacheKey);

    /**
     * Deletes a token cache record by process instance ID and auth name.
     *
     * @param processInstanceId the process instance ID
     * @param authName the authentication name
     */
    void deleteByKey(String processInstanceId, String authName);

    /**
     * Deletes a token cache record by cache key.
     *
     * @param cacheKey the cache key
     */
    void deleteByCacheKey(String cacheKey);
}
