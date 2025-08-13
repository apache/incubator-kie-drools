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

import org.kie.kogito.addons.quarkus.token.exchange.cache.CachedTokens;

/**
 * Abstract interface for token storage operations.
 * Provides a clean separation between caching logic and storage implementation.
 */
public interface TokenDataStore {

    /**
     * Store or update tokens for a given cache key
     */
    void store(String cacheKey, CachedTokens tokens);

    /**
     * Retrieve tokens by cache key
     */
    Optional<CachedTokens> retrieve(String cacheKey);

    /**
     * Remove tokens by cache key
     */
    void remove(String cacheKey);

    /**
     * Token entry with cache key and tokens
     */
    record TokenEntry(String cacheKey, CachedTokens tokens) {
    }
}
