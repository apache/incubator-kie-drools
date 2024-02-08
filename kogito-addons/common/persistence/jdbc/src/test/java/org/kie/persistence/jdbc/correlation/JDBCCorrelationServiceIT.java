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
package org.kie.persistence.jdbc.correlation;

import java.util.Collections;
import java.util.Optional;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.correlation.CompositeCorrelation;
import org.kie.kogito.correlation.CorrelationInstance;
import org.kie.kogito.correlation.SimpleCorrelation;
import org.kie.kogito.persistence.jdbc.correlation.PostgreSQLCorrelationService;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class JDBCCorrelationServiceIT {

    @Container
    private static final KogitoPostgreSqlContainer PG_CONTAINER = new KogitoPostgreSqlContainer();
    private static PGSimpleDataSource dataSource;
    private static PostgreSQLCorrelationService correlationService;

    @BeforeAll
    public static void setUp() {
        dataSource = new PGSimpleDataSource();
        dataSource.setUrl(PG_CONTAINER.getJdbcUrl());
        dataSource.setUser(PG_CONTAINER.getUsername());
        dataSource.setPassword(PG_CONTAINER.getPassword());
        correlationService = new PostgreSQLCorrelationService(dataSource);
        //create table
        //        DDLRunner.init(new GenericRepository(dataSource), true);
        initMigration(PG_CONTAINER, "postgresql");
    }

    public static void initMigration(JdbcDatabaseContainer container, String dbKind) {
        Flyway flyway = Flyway.configure().dataSource(container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword())
                .locations("classpath:db/" + dbKind).load();
        flyway.migrate();
    }

    @Test
    public void testCorrelation() {
        CompositeCorrelation correlation = new CompositeCorrelation(Collections.singleton(new SimpleCorrelation<>("name", "Tiago")));
        String correlatedId = "id";
        CorrelationInstance correlationInstance = correlationService.create(correlation, correlatedId);
        assertThat(correlationInstance.getCorrelatedId()).isEqualTo(correlatedId);
        assertThat(correlationInstance.getCorrelation()).isEqualTo(correlation);

        Optional<CorrelationInstance> foundInstance = correlationService.find(correlation);

        assertThat(foundInstance).isNotEmpty();
        assertThat(foundInstance.get().getCorrelatedId()).isEqualTo(correlatedId);
        assertThat(foundInstance.get().getCorrelation()).isEqualTo(correlation);

        foundInstance = correlationService.findByCorrelatedId(correlatedId);
        assertThat(foundInstance).isNotEmpty();
        assertThat(foundInstance.get().getCorrelatedId()).isEqualTo(correlatedId);
        assertThat(foundInstance.get().getCorrelation()).isEqualTo(correlation);

        correlationService.delete(correlation);

        foundInstance = correlationService.find(correlation);
        assertThat(foundInstance).isEmpty();

        foundInstance = correlationService.findByCorrelatedId(correlatedId);
        assertThat(foundInstance).isEmpty();
    }
}
