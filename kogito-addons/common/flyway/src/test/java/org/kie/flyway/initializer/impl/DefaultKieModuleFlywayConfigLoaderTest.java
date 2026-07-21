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

package org.kie.flyway.initializer.impl;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.flyway.KieFlywayException;
import org.kie.flyway.model.KieFlywayModuleConfig;
import org.kie.flyway.test.utils.TestClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultKieModuleFlywayConfigLoaderTest {

    private static final String H2_LOCATIONS = "classpath:kie-flyway/db/test/h2";
    private static final String PGSQL_LOCATIONS = "classpath:kie-flyway/db/test/postgresql";
    private static final String DEFAULT_LOCATIONS = "classpath:kie-flyway/db/test/ansi";

    private TestClassLoader testClassLoader;
    private DefaultKieModuleFlywayConfigLoader flywayConfigLoader;

    @BeforeEach
    public void init() {
        this.testClassLoader = new TestClassLoader(this.getClass().getClassLoader());
        this.flywayConfigLoader = new DefaultKieModuleFlywayConfigLoader(testClassLoader);
    }

    @Test
    public void testDefaultLoading() {

        Collection<KieFlywayModuleConfig> configs = flywayConfigLoader.loadModuleConfigs();

        assertThat(configs)
                .hasSize(1);

        assertThat(configs.iterator().next())
                .isNotNull()
                .hasFieldOrPropertyWithValue("module", "test")
                .returns(H2_LOCATIONS, kieFlywayModuleConfig -> kieFlywayModuleConfig.getDBScriptLocations("h2")[0])
                .returns(PGSQL_LOCATIONS, kieFlywayModuleConfig -> kieFlywayModuleConfig.getDBScriptLocations("postgresql")[0])
                .returns(DEFAULT_LOCATIONS, kieFlywayModuleConfig -> kieFlywayModuleConfig.getDBScriptLocations("db2")[0]);
    }

    @Test
    public void testEmptyConfigFile() {
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.empty.properties");

        assertThatThrownBy(() -> flywayConfigLoader.loadModuleConfigs())
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Could not load ModuleFlywayConfig")
                .cause()
                .isInstanceOf(KieFlywayException.class)
                .hasMessageStartingWith("Could not load module name from");
    }

    @Test
    public void testWrongLocationsFormat() {
        testClassLoader.addKieFlywayModule("initializers/kie-flyway.wrong.format.properties");

        assertThatThrownBy(() -> flywayConfigLoader.loadModuleConfigs())
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Could not load ModuleFlywayConfig")
                .cause()
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Cannot load module `test-wrong-format` config, file has wrong format");
    }

    @Test
    public void testWrongResourceFile() {
        testClassLoader.addKieFlywayModule("wrong content");

        assertThatThrownBy(() -> flywayConfigLoader.loadModuleConfigs())
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Could not load ModuleFlywayConfig");
    }
}
