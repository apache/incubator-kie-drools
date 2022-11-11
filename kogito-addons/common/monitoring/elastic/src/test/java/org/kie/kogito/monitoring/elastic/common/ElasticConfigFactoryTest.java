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
package org.kie.kogito.monitoring.elastic.common;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import io.micrometer.elastic.ElasticConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class ElasticConfigFactoryTest {

    @Test
    public void testGeneratedElasticConfig() {
        ElasticConfigFactory elasticConfigFactory = new ElasticConfigFactory();
        elasticConfigFactory.withProperty(KogitoElasticConfig.HOST_KEY, "http://mylocalhost");
        elasticConfigFactory.withProperty(KogitoElasticConfig.USERNAME_KEY, "pippo");
        elasticConfigFactory.withProperty(KogitoElasticConfig.PASSWORD_KEY, "pluto");
        elasticConfigFactory.withProperty(KogitoElasticConfig.STEP_KEY, "1s");

        ElasticConfig elasticConfig = elasticConfigFactory.getElasticConfig();

        assertThat(elasticConfig.host()).isEqualTo("http://mylocalhost");
        assertThat(elasticConfig.userName()).isEqualTo("pippo");
        assertThat(elasticConfig.password()).isEqualTo("pluto");
        assertThat(elasticConfig.step()).isEqualTo(Duration.ofSeconds(1));
    }
}
