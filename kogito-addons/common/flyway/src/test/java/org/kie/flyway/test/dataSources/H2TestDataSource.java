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

package org.kie.flyway.test.dataSources;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2TestDataSource implements TestDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(H2TestDataSource.class);

    private final JdbcDataSource dataSource;

    public H2TestDataSource() {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=true");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
    }

    @Override
    public String getDbType() {
        return "h2";
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void shutDown() {
        try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute("SHUTDOWN");
        } catch (SQLException e) {
            LOGGER.warn("Error shutting down database", e);
        }
    }
}
