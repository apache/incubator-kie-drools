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
package org.kie.kogito.serverless.workflow.token.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.kie.kogito.addons.quarkus.token.exchange.persistence.TokenCacheRepository;
import org.kie.kogito.addons.quarkus.token.exchange.persistence.model.TokenCacheRecord;
import org.kie.kogito.addons.quarkus.token.exchange.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * JDBC-based repository for token cache operations.
 * Follows the same pattern as other JDBC repositories in the codebase.
 */
@ApplicationScoped
@Alternative
@Priority(200)
public class JdbcTokenCacheRepository implements TokenCacheRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTokenCacheRepository.class);

    // SQL queries following the same pattern as other JDBC repositories
    static final String INSERT =
            "INSERT INTO kogito_oauth2_token_cache (process_instance_id, auth_name, access_token, refresh_token, expiration_time) VALUES (?, ?, ?, ?, ?)";
    static final String UPDATE = "UPDATE kogito_oauth2_token_cache SET access_token = ?, refresh_token = ?, expiration_time = ? WHERE process_instance_id = ? AND auth_name = ?";
    static final String FIND_BY_KEY =
            "SELECT process_instance_id, auth_name, access_token, refresh_token, expiration_time FROM kogito_oauth2_token_cache WHERE process_instance_id = ? AND auth_name = ?";
    static final String DELETE_BY_KEY = "DELETE FROM kogito_oauth2_token_cache WHERE process_instance_id = ? AND auth_name = ?";
    static final String DELETE_EXPIRED = "DELETE FROM kogito_oauth2_token_cache WHERE expiration_time < ?";
    static final String FIND_EXPIRING_SOON =
            "SELECT process_instance_id, auth_name, access_token, refresh_token, expiration_time FROM kogito_oauth2_token_cache WHERE expiration_time < ?";
    static final String FIND_ALL = "SELECT process_instance_id, auth_name, access_token, refresh_token, expiration_time FROM kogito_oauth2_token_cache";

    private final DataSource dataSource;

    public JdbcTokenCacheRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TokenCacheRecord save(TokenCacheRecord record) {
        // Check if record exists first - use direct method since we have the components
        Optional<TokenCacheRecord> existing = findByKey(record.processInstanceId(), record.authName());

        if (existing.isPresent()) {
            return update(record);
        } else {
            return insert(record);
        }
    }

    private TokenCacheRecord insert(TokenCacheRecord record) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT)) {

            statement.setString(1, record.processInstanceId());
            statement.setString(2, record.authName());
            statement.setString(3, record.accessToken());
            statement.setString(4, record.refreshToken());
            statement.setLong(5, record.expirationTime());

            int executed = statement.executeUpdate();
            if (executed > 0) {
                LOGGER.debug("Inserted token cache record for processInstanceId: {}, authName: {}",
                        record.processInstanceId(), record.authName());
                return record;
            } else {
                throw new RuntimeException("Failed to insert token cache record for processInstanceId: " +
                        record.processInstanceId() + ", authName: " + record.authName());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting token cache record for processInstanceId: " +
                    record.processInstanceId() + ", authName: " + record.authName(), e);
        }
    }

    private TokenCacheRecord update(TokenCacheRecord record) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            statement.setString(1, record.accessToken());
            statement.setString(2, record.refreshToken());
            statement.setLong(3, record.expirationTime());
            statement.setString(4, record.processInstanceId());
            statement.setString(5, record.authName());

            int executed = statement.executeUpdate();
            if (executed > 0) {
                LOGGER.debug("Updated token cache record for processInstanceId: {}, authName: {}",
                        record.processInstanceId(), record.authName());
                return record;
            } else {
                throw new RuntimeException("Failed to update token cache record for processInstanceId: " +
                        record.processInstanceId() + ", authName: " + record.authName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating token cache record for processInstanceId: " +
                    record.processInstanceId() + ", authName: " + record.authName(), e);
        }
    }

    @Override
    public Optional<TokenCacheRecord> findByKey(String processInstanceId, String authName) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_KEY)) {

            statement.setString(1, processInstanceId);
            statement.setString(2, authName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapResultSetToRecord(resultSet)) : Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding token cache record by processInstanceId: " +
                    processInstanceId + ", authName: " + authName, e);
        }
    }

    @Override
    public Optional<TokenCacheRecord> findByCacheKey(String cacheKey) {
        // Extract components from cache key and delegate to the main method
        String processInstanceId = CacheUtils.extractProcessInstanceIdFromCacheKey(cacheKey);
        String authName = CacheUtils.extractAuthNameFromCacheKey(cacheKey);
        return findByKey(processInstanceId, authName);
    }

    @Override
    public void deleteByKey(String processInstanceId, String authName) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE_BY_KEY)) {

            statement.setString(1, processInstanceId);
            statement.setString(2, authName);
            int executed = statement.executeUpdate();

            if (executed > 0) {
                LOGGER.debug("Deleted token cache record for processInstanceId: {}, authName: {}",
                        processInstanceId, authName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting token cache record for processInstanceId: " +
                    processInstanceId + ", authName: " + authName, e);
        }
    }

    @Override
    public void deleteByCacheKey(String cacheKey) {
        // Extract components from cache key and delegate to the main method
        String processInstanceId = CacheUtils.extractProcessInstanceIdFromCacheKey(cacheKey);
        String authName = CacheUtils.extractAuthNameFromCacheKey(cacheKey);
        deleteByKey(processInstanceId, authName);
    }

    private TokenCacheRecord mapResultSetToRecord(ResultSet resultSet) throws Exception {
        return new TokenCacheRecord(
                resultSet.getString("process_instance_id"),
                resultSet.getString("auth_name"),
                resultSet.getString("access_token"),
                resultSet.getString("refresh_token"),
                resultSet.getLong("expiration_time"));
    }
}
