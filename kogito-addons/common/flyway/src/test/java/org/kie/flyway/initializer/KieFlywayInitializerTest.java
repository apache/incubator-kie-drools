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

package org.kie.flyway.initializer;

import java.util.*;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.flyway.KieFlywayException;
import org.kie.flyway.test.AbstractKieFlywayTest;
import org.kie.flyway.test.dataSources.H2TestDataSource;
import org.kie.flyway.test.dataSources.PostgreSQLTestDataSource;
import org.kie.flyway.test.dataSources.TestDataSource;
import org.kie.flyway.test.utils.TestClassLoader;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.kie.flyway.test.models.TestModels.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KieFlywayInitializerTest extends AbstractKieFlywayTest {

    @Container
    private static final KogitoPostgreSqlContainer PG_CONTAINER = new KogitoPostgreSqlContainer();

    private static PostgreSQLTestDataSource PG_DATA_SOURCE;
    private static H2TestDataSource H2_DATA_SOURCE;

    private TestClassLoader classLoader;

    @BeforeAll
    public static void start() {
        PG_CONTAINER.start();
        PG_DATA_SOURCE = new PostgreSQLTestDataSource(PG_CONTAINER);
        H2_DATA_SOURCE = new H2TestDataSource();
    }

    public static Stream<Arguments> getDataSources() {
        return Stream.of(Arguments.of(PG_DATA_SOURCE),
                Arguments.of(H2_DATA_SOURCE));
    }

    @BeforeEach
    public void init() {
        classLoader = new TestClassLoader(this.getClass().getClassLoader());
    }

    @ParameterizedTest
    @MethodSource("getDataSources")
    public void testTestKieFlywayInitializerBuilderValidations(TestDataSource dataSource) {
        Assertions.assertThatThrownBy(() -> KieFlywayInitializer.builder()
                .build()).isInstanceOf(KieFlywayException.class)
                .hasMessage("Cannot create KieFlywayInitializer migration, dataSource is null.");

        classLoader.addKieFlywayModule("initializers/kie-flyway.no.locations.properties");

        Assertions.assertThatThrownBy(() -> KieFlywayInitializer.builder()
                .withDatasource(dataSource.getDataSource())
                .withClassLoader(classLoader).build().migrate())
                .isInstanceOf(KieFlywayException.class)
                .hasMessageContaining("Cannot run Flyway migration for module `no-locations`, cannot find SQL Script locations for db");
    }

    @ParameterizedTest
    @MethodSource("getDataSources")
    public void testKieFlywayInitializerValidations(TestDataSource dataSource) {
        classLoader.addKieFlywayModule("initializers/kie-flyway.duplicated1.properties");
        classLoader.addKieFlywayModule("initializers/kie-flyway.duplicated1.properties");
        classLoader.addKieFlywayModule("initializers/kie-flyway.duplicated2.properties");
        classLoader.addKieFlywayModule("initializers/kie-flyway.duplicated2.properties");

        Assertions.assertThatThrownBy(() -> {
            KieFlywayInitializer.builder()
                    .withDatasource(dataSource.getDataSource())
                    .withClassLoader(classLoader)
                    .build()
                    .migrate();
        }).isInstanceOf(KieFlywayException.class)
                .hasMessage("Cannot run Kie Flyway migration: Duplicated Modules found test-duplicated-1, test-duplicated-2");

    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("getDataSources")
    public void testFlywayMigrationsWithExclusions(TestDataSource dataSource) {

        classLoader.addKieFlywayModule("initializers/kie-flyway.customers.properties");
        classLoader.addKieFlywayModule("initializers/kie-flyway.guitars.properties");

        KieFlywayInitializer.builder()
                .withDatasource(dataSource.getDataSource())
                .withClassLoader(classLoader)
                .withModuleExclusions(List.of("guitars"))
                .build()
                .migrate();

        validateKieFlywayIndex("customers", EXPECTED_CUSTOMERS_MIGRATIONS.stream().limit(3).toList(), dataSource);
        validateCustomersData(EXPECTED_CUSTOMERS.stream().limit(2).toList(), dataSource);

        // Guitars module has been excluded, so it shouldn't be installed. Verifying that tables don't exist
        verifyTableDoesntExist("guitars", dataSource);
        verifyTableDoesntExist("kie_flyway_history_guitars", dataSource);
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("getDataSources")
    public void testFlywayMigrationsUpgrade(TestDataSource dataSource) {

        classLoader.addKieFlywayModule("initializers/kie-flyway.customers2.properties");
        classLoader.addKieFlywayModule("initializers/kie-flyway.guitars.properties");

        KieFlywayInitializer.builder()
                .withDatasource(dataSource.getDataSource())
                .withClassLoader(classLoader)
                .withModuleExclusions(List.of("test-3"))
                .build()
                .migrate();

        validateKieFlywayIndex("customers", EXPECTED_CUSTOMERS_MIGRATIONS, dataSource);
        validateCustomersData(EXPECTED_CUSTOMERS, dataSource);

        validateKieFlywayIndex("guitars", EXPECTED_GUITARS_MIGRATIONS, dataSource);
        validateGuitarsData(dataSource);
    }

    @Test
    public void testFlywayMigrationWithFallbackDBType() {
        classLoader.addKieFlywayModule("initializers/kie-flyway.guitars_default.properties");

        H2TestDataSource dataSource = new H2TestDataSource();

        KieFlywayInitializer.builder()
                .withDatasource(dataSource.getDataSource())
                .withClassLoader(classLoader)
                .build()
                .migrate();

        validateKieFlywayIndex("guitars", EXPECTED_GUITARS_MIGRATIONS, dataSource);
        validateGuitarsData(dataSource);

        dataSource.shutDown();
    }

    @AfterAll
    public static void shutdown() {
        H2_DATA_SOURCE.shutDown();
    }

}
