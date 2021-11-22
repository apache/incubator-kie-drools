/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.persistence.jdbc;

import java.sql.SQLException;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.testcontainers.KogitoOracleSqlContainer;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import oracle.jdbc.pool.OracleDataSource;

@Testcontainers
public class TestHelper {

    @Container
    final static KogitoPostgreSqlContainer PG_CONTAINER = new KogitoPostgreSqlContainer();

    @Container
    final static KogitoOracleSqlContainer ORACLE_CONTAINER = new KogitoOracleSqlContainer();

    static DataSource PG_DATA_SOURCE;
    static DataSource ORACLE_DATA_SOURCE;

    public static Stream<DataSource> datasources() {
        return Stream.of(ORACLE_DATA_SOURCE, PG_DATA_SOURCE);
    }

    public static SecurityPolicy securityPolicy = SecurityPolicy.of(IdentityProviders.of("john"));

    public static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";
    private static final String ORACLE_TIMEZONE_PROPERTY = "oracle.jdbc.timezoneAsRegion";

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        PG_CONTAINER.start();
        PG_DATA_SOURCE = getPGDataSource(PG_CONTAINER);

        ORACLE_CONTAINER.start();
        ORACLE_DATA_SOURCE = getOracleDataSource(ORACLE_CONTAINER);
        System.setProperty(ORACLE_TIMEZONE_PROPERTY, "false");
    }

    @AfterAll
    public static void close() {
        PG_CONTAINER.stop();
        ORACLE_CONTAINER.stop();
        System.clearProperty(ORACLE_TIMEZONE_PROPERTY);
    }

    public static BpmnProcess createProcess(TestProcessInstancesFactory factory, String fileName) {

        BpmnProcess process = BpmnProcess.from(new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(factory);
        process.configure();
        process.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(p -> p.abort());
        return process;
    }

    private static DataSource getPGDataSource(final PostgreSQLContainer postgreSQLContainer) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(postgreSQLContainer.getJdbcUrl());
        ds.setUser(postgreSQLContainer.getUsername());
        ds.setPassword(postgreSQLContainer.getPassword());
        return ds;
    }

    private static DataSource getOracleDataSource(final OracleContainer oracleContainer) {
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL(oracleContainer.getJdbcUrl());
            ds.setUser(oracleContainer.getUsername());
            ds.setPassword(oracleContainer.getPassword());
            return ds;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create oracle datasource");
        }
    }

    public static BpmnProcess configure(boolean lock) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        process.setProcessInstancesFactory(new TestProcessInstancesFactory(null, lock));
        process.configure();
        return process;
    }

}
