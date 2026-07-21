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
package org.kie.kogito.persistence.jdbc.correlation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import javax.sql.DataSource;

import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JDBCCorrelationRepository {

    static final String INSERT = "INSERT INTO correlation_instances (id, encoded_correlation_id, correlated_id, correlation) VALUES (?, ?, ?, ?)";
    static final String DELETE = "DELETE FROM correlation_instances WHERE encoded_correlation_id = ?";
    private static final String FIND_BY_ENCODED_ID = "SELECT correlated_id, correlation FROM correlation_instances WHERE encoded_correlation_id = ?";
    private static final String FIND_BY_CORRELATED_ID = "SELECT encoded_correlation_id, correlation FROM correlation_instances WHERE correlated_id = ?";

    private DataSource dataSource;
    private ObjectMapper objectMapper;

    public JDBCCorrelationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        this.objectMapper = ObjectMapperFactory.get().copy();

        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Correlation.class, SimpleCorrelation.class);
        objectMapper.registerModule(module);
    }

    public CorrelationInstance insert(String encodedCorrelationId, String correlatedId, Correlation correlation) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT)) {
            String correlationJson = objectMapper.writeValueAsString(correlation);
            String id = UUID.randomUUID().toString();
            statement.setString(1, id);
            statement.setString(2, encodedCorrelationId);
            statement.setString(3, correlatedId);
            statement.setString(4, correlationJson);
            int executed = statement.executeUpdate();
            if (executed > 0) {
                return new CorrelationInstance(encodedCorrelationId, correlatedId, correlation);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CorrelationInstance findByEncodedCorrelationId(String encodedCorrelationId) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ENCODED_ID)) {
            statement.setString(1, encodedCorrelationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String correlationJson = resultSet.getString("correlation");
                    CompositeCorrelation correlation = objectMapper.readValue(correlationJson, CompositeCorrelation.class);
                    String correlatedId = resultSet.getString("correlated_id");
                    return new CorrelationInstance(encodedCorrelationId, correlatedId, correlation);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CorrelationInstance findByCorrelatedId(String correlatedId) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_CORRELATED_ID)) {
            statement.setString(1, correlatedId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String correlationJson = resultSet.getString("correlation");
                    CompositeCorrelation correlation = objectMapper.readValue(correlationJson, CompositeCorrelation.class);
                    String encodedCorrelationId = resultSet.getString("encoded_correlation_id");
                    return new CorrelationInstance(encodedCorrelationId, correlatedId, correlation);
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String encodedCorrelationId) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, encodedCorrelationId);
            int executed = statement.executeUpdate();
            if (executed == 0) {
                throw new RuntimeException("Error deleting correlation with encodedCorrelationId " + encodedCorrelationId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
