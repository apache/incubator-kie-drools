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

import io.quarkus.arc.DefaultBean;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * In-memory repository for token cache operations.
 * Follows the same pattern as other JDBC repositories in the codebase.
 */
@ApplicationScoped
@DefaultBean
public class InMemoryTokenCacheRepository implements TokenCacheRepository {

    @Override
    public TokenCacheRecord save(TokenCacheRecord record) {
        return record;
    }

    @Override
    public Optional<TokenCacheRecord> findByKey(String processInstanceId, String authName) {
        return Optional.empty();
    }

    @Override
    public Optional<TokenCacheRecord> findByCacheKey(String cacheKey) {
        return Optional.empty();
    }

    @Override
    public void deleteByKey(String processInstanceId, String authName) {

    }

    @Override
    public void deleteByCacheKey(String cacheKey) {

    }

}
