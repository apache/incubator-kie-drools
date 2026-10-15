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
package org.kie.flyway.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class KieFlywaySpringbootPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @Configuration
    @EnableConfigurationProperties(KieFlywaySpringbootProperties.class)
    static class TestConfig {
    }

    @Test
    void enabledDefaultsToFalse() {
        contextRunner.run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isEnabled()).isFalse());
    }

    @Test
    void enabledCanBeSetToTrue() {
        contextRunner
                .withPropertyValues("kie.flyway.enabled=true")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isEnabled()).isTrue());
    }

    @Test
    void enabledCanBeSetToFalse() {
        contextRunner
                .withPropertyValues("kie.flyway.enabled=false")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isEnabled()).isFalse());
    }

    @Test
    void outOfOrderDefaultsToFalse() {
        contextRunner.run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isOutOfOrder()).isFalse());
    }

    @Test
    void outOfOrderCanBeSetToTrue() {
        contextRunner
                .withPropertyValues("kie.flyway.out-of-order=true")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isOutOfOrder()).isTrue());
    }

    @Test
    void outOfOrderCanBeSetToFalse() {
        contextRunner
                .withPropertyValues("kie.flyway.out-of-order=false")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).isOutOfOrder()).isFalse());
    }

    @Test
    void modulesDefaultsToEmpty() {
        contextRunner.run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class).getModules()).isEmpty());
    }

    @Test
    void moduleEnabledCanBeSetToFalse() {
        contextRunner
                .withPropertyValues("kie.flyway.modules.data-index.enabled=false")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class)
                        .getModules().get("data-index").isEnabled()).isFalse());
    }

    @Test
    void moduleEnabledDefaultsToTrue() {
        contextRunner
                .withPropertyValues("kie.flyway.modules.data-index.enabled=true")
                .run(ctx -> assertThat(ctx.getBean(KieFlywaySpringbootProperties.class)
                        .getModules().get("data-index").isEnabled()).isTrue());
    }
}
