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

package org.kie.flyway.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.kie.flyway.initializer.KieFlywayInitializerTest;
import org.kie.flyway.test.dataSources.TestDataSource;
import org.kie.flyway.test.models.Customer;
import org.kie.flyway.test.models.Guitar;
import org.kie.flyway.test.models.KieFlywayMigration;

import static org.kie.flyway.test.models.TestModels.EXPECTED_GUITARS;

public abstract class AbstractKieFlywayTest {
    public static final String MODULE_MIGRATIONS_QUERY_TEMPLATE = "select \"version\", \"description\", \"success\" from \"kie_flyway_history_%s\" where \"version\" = ?";
    public static final String QUERY_CUSTOMERS_DATA = "select id, name, last_name, email from customers order by id";
    public static final String QUERY_GUITARS_DATA = "select id, brand, model, rating from guitars order by id";
    public static final String QUERY_QUERY_TABLE_EXISTS = "select count(*) as count from information_schema.tables where table_name = ?";

    public void validateKieFlywayIndex(String moduleName, Collection<KieFlywayMigration> expectedMigrations, TestDataSource dataSource) {
        expectedMigrations.forEach(kieFlywayMigration -> validateFlywayMigration(moduleName, kieFlywayMigration, dataSource));
    }

    private void validateFlywayMigration(final String moduleName, final KieFlywayMigration migration, final TestDataSource dataSource) {
        try (Connection con = dataSource.getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(KieFlywayInitializerTest.MODULE_MIGRATIONS_QUERY_TEMPLATE.formatted(moduleName));) {
            stmt.setString(1, migration.version());
            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertThat(rs.next())
                        .isTrue();
                Assertions.assertThat(rs.getString("version"))
                        .isEqualTo(migration.version());
                Assertions.assertThat(rs.getString("description"))
                        .isEqualTo(migration.description().formatted(dataSource.getDbType()));
                Assertions.assertThat(rs.getBoolean("success"))
                        .isEqualTo(true);
                Assertions.assertThat(rs.next())
                        .isFalse();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void validateCustomersData(Collection<Customer> expectedCustomers, TestDataSource dataSource) {
        try (Connection con = dataSource.getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(KieFlywayInitializerTest.QUERY_CUSTOMERS_DATA);
                ResultSet rs = stmt.executeQuery()) {

            for (Customer customer : expectedCustomers) {
                Assertions.assertThat(rs.next())
                        .isTrue();
                Assertions.assertThat(rs.getInt("id"))
                        .isEqualTo(customer.id());
                Assertions.assertThat(rs.getString("name"))
                        .isEqualTo(customer.name());
                Assertions.assertThat(rs.getString("last_name"))
                        .isEqualTo(customer.lastName());
                Assertions.assertThat(rs.getString("email"))
                        .isEqualTo(customer.email());
            }
            Assertions.assertThat(rs.next())
                    .isFalse();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void validateGuitarsData(TestDataSource dataSource) {
        try (Connection con = dataSource.getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(KieFlywayInitializerTest.QUERY_GUITARS_DATA);
                ResultSet rs = stmt.executeQuery()) {

            for (Guitar guitar : EXPECTED_GUITARS) {
                Assertions.assertThat(rs.next())
                        .isTrue();
                Assertions.assertThat(rs.getInt("id"))
                        .isEqualTo(guitar.id());
                Assertions.assertThat(rs.getString("brand"))
                        .isEqualTo(guitar.brand());
                Assertions.assertThat(rs.getString("model"))
                        .isEqualTo(guitar.model());
                Assertions.assertThat(rs.getInt("rating"))
                        .isEqualTo(guitar.rating());
            }

            Assertions.assertThat(rs.next())
                    .isFalse();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void verifyTableDoesntExist(String tableName, TestDataSource dataSource) {
        try (Connection con = dataSource.getDataSource().getConnection();
                PreparedStatement stmt = con.prepareStatement(QUERY_QUERY_TABLE_EXISTS);) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertThat(rs.next()).isTrue();
                Assertions.assertThat(rs.getInt("count")).isEqualTo(0);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
