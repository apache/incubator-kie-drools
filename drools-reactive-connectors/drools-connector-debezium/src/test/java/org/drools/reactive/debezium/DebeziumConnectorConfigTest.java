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
package org.drools.reactive.debezium;

import java.util.Properties;

import org.drools.reactive.api.FiringStrategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DebeziumConnectorConfigTest {

    @Test
    void shouldBuildWithDefaults() {
        DebeziumConnectorConfig config = DebeziumConnectorConfig.builder().build();

        assertThat(config.getConnectorName()).isEqualTo("drools-cdc");
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.PER_MESSAGE);
        assertThat(config.getDebeziumProperties()).isEmpty();
    }

    @Test
    void shouldBuildWithCustomProperties() {
        Properties props = new Properties();
        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");

        DebeziumConnectorConfig config = DebeziumConnectorConfig.builder()
                .connectorName("my-cdc")
                .debeziumProperties(props)
                .debeziumProperty("database.hostname", "localhost")
                .firingStrategy(FiringStrategy.MICRO_BATCH)
                .build();

        assertThat(config.getConnectorName()).isEqualTo("my-cdc");
        assertThat(config.getFiringStrategy()).isEqualTo(FiringStrategy.MICRO_BATCH);
        assertThat(config.getDebeziumProperties())
                .containsEntry("connector.class", "io.debezium.connector.mysql.MySqlConnector")
                .containsEntry("database.hostname", "localhost");
    }
}
