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

package org.kie.flyway.integration;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.*;
import org.kie.flyway.KieFlywayException;
import org.kie.flyway.test.AbstractKieFlywayTest;
import org.kie.flyway.test.dataSources.H2TestDataSource;
import org.kie.flyway.test.dataSources.TestDataSource;
import org.kie.flyway.test.utils.TestClassLoader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.flyway.test.models.TestModels.*;
import static org.mockito.Mockito.mock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KieFlywayRunnerTest extends AbstractKieFlywayTest {

    private static final TestDataSource TEST_DATA_SOURCE;

    static {
        TEST_DATA_SOURCE = new H2TestDataSource();
    }

    private TestClassLoader testClassLoader;

    @BeforeEach
    public void init() {
        testClassLoader = new TestClassLoader(this.getClass().getClassLoader());
    }

    @Test
    public void testValidations() {
        assertThatThrownBy(() -> KieFlywayRunner.get(null).runFlyway(null))
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Kie Flyway: Cannot run Kie Flyway migration configuration is null.");

        KieFlywayRunnerConfiguration config = new KieFlywayRunnerConfiguration(true, new ArrayList<>());

        assertThatThrownBy(() -> KieFlywayRunner.get(config).runFlyway(null)).isInstanceOf(KieFlywayException.class)
                .hasMessage("Kie Flyway: Cannot run Kie Flyway migration default datasource is null");

        // Mocking DataSource to make sure we cannot resolve dbType.
        DataSource mockedDS = mock(DataSource.class);

        assertThatThrownBy(() -> KieFlywayRunner.get(config).runFlyway(mockedDS))
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Kie Flyway: Couldn't extract database product name from datasource.");
    }

    @Test
    @Order(0)
    public void testFlywayMigrationsWithDisabledConfig() {
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.customers.properties");
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.guitars.properties");

        KieFlywayRunnerConfiguration config = new KieFlywayRunnerConfiguration(false, new ArrayList<>());

        TestKieFlywayRunner.get(config, testClassLoader)
                .runFlyway(TEST_DATA_SOURCE.getDataSource());

        verifyTableDoesntExist("customers", TEST_DATA_SOURCE);
        verifyTableDoesntExist("kie_flyway_history_customers", TEST_DATA_SOURCE);
        verifyTableDoesntExist("guitars", TEST_DATA_SOURCE);
        verifyTableDoesntExist("kie_flyway_history_guitars", TEST_DATA_SOURCE);
    }

    @Test
    @Order(1)
    public void testFlywayMigrationsWithExclusions() {
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.customers.properties");
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.guitars.properties");

        KieFlywayRunnerConfiguration config = new KieFlywayRunnerConfiguration(true, List.of(new KieFlywayNamedModule("guitars", false)));

        TestKieFlywayRunner.get(config, testClassLoader)
                .runFlyway(TEST_DATA_SOURCE.getDataSource());

        validateKieFlywayIndex("customers", EXPECTED_CUSTOMERS_MIGRATIONS.stream().limit(3).toList(), TEST_DATA_SOURCE);
        validateCustomersData(EXPECTED_CUSTOMERS.stream().limit(2).toList(), TEST_DATA_SOURCE);

        // Guitars module has been excluded, so it shouldn't be installed. Verifying that tables don't exist
        verifyTableDoesntExist("guitars", TEST_DATA_SOURCE);
        verifyTableDoesntExist("kie_flyway_history_guitars", TEST_DATA_SOURCE);
    }

    @Test
    @Order(2)
    public void testFlywayMigrationsUpgrade() {

        testClassLoader.addKieFlywayModule("initializers/kie-flyway.customers2.properties");
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.guitars.properties");

        KieFlywayRunnerConfiguration config = new KieFlywayRunnerConfiguration(true, new ArrayList<>());

        TestKieFlywayRunner.get(config, testClassLoader)
                .runFlyway(TEST_DATA_SOURCE.getDataSource());

        validateKieFlywayIndex("customers", EXPECTED_CUSTOMERS_MIGRATIONS, TEST_DATA_SOURCE);
        validateCustomersData(EXPECTED_CUSTOMERS, TEST_DATA_SOURCE);

        validateKieFlywayIndex("guitars", EXPECTED_GUITARS_MIGRATIONS, TEST_DATA_SOURCE);
        validateGuitarsData(TEST_DATA_SOURCE);
    }

    @AfterAll
    public static void shutdown() {
        TEST_DATA_SOURCE.shutDown();
    }

    public static class TestKieFlywayRunner extends KieFlywayRunner {

        protected TestKieFlywayRunner(KieFlywayRunnerConfiguration configuration, ClassLoader classLoader) {
            super(configuration, classLoader);
        }

        public static KieFlywayRunner get(KieFlywayRunnerConfiguration configuration, ClassLoader classLoader) {
            return new TestKieFlywayRunner(configuration, classLoader);
        }
    }
}
