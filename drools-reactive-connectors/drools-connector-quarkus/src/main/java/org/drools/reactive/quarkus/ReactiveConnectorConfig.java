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
package org.drools.reactive.quarkus;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * SmallRye Config mapping for Drools reactive connector configuration.
 *
 * <p>Properties are read from {@code application.properties} under the
 * prefix {@code drools.connector}. Each named connector gets its own
 * section, e.g.:
 * <pre>
 * drools.connector.transactions.type=kafka
 * drools.connector.transactions.topics=payment-events
 * drools.connector.transactions.bootstrap-servers=broker:9092
 * drools.connector.transactions.group-id=fraud-rules
 * drools.connector.transactions.firing-strategy=per-message
 * </pre>
 */
@ConfigMapping(prefix = "drools.connector")
public interface ReactiveConnectorConfig {

    Map<String, ConnectorInstanceConfig> connectors();

    interface ConnectorInstanceConfig {

        /** Connector type: kafka, pulsar, or debezium. */
        @WithDefault("kafka")
        String type();

        /** Topics to subscribe to (comma-separated). */
        Optional<String> topics();

        /** Kafka bootstrap servers or Pulsar service URL. */
        @WithDefault("localhost:9092")
        String bootstrapServers();

        /** Consumer group ID (Kafka) or subscription name (Pulsar). */
        @WithDefault("drools-reactive")
        String groupId();

        /** When to fire rules: per-message, micro-batch, or external. */
        @WithDefault("per-message")
        String firingStrategy();

        /** Maximum records per micro-batch. */
        @WithDefault("100")
        int batchSize();

        /** Poll timeout duration. */
        @WithDefault("1s")
        Duration pollTimeout();

        /** Pulsar subscription type: exclusive, shared, failover, key-shared. */
        @WithDefault("exclusive")
        String subscriptionType();

        /** Debezium connector class (e.g. io.debezium.connector.mysql.MySqlConnector). */
        Optional<String> connectorClass();
    }
}
