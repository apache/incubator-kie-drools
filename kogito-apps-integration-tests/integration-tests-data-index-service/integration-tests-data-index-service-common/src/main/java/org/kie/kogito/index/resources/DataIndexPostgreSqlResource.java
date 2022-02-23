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

package org.kie.kogito.index.resources;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.index.testcontainers.DataIndexPostgreSqlContainer;
import org.kie.kogito.index.testcontainers.KogitoKafkaContainerWithoutBridge;
import org.kie.kogito.test.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class DataIndexPostgreSqlResource implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexPostgreSqlResource.class);

    KogitoKafkaContainerWithoutBridge kafka = new KogitoKafkaContainerWithoutBridge();
    KogitoPostgreSqlContainer postgresql = new KogitoPostgreSqlContainer();
    DataIndexPostgreSqlContainer dataIndex = new DataIndexPostgreSqlContainer();
    Map<String, String> properties = new HashMap<>();

    @Override
    public String getResourceName() {
        return dataIndex.getResourceName();
    }

    @Override
    public void start() {
        LOGGER.debug("Starting PostgreSQL Quarkus test resource");
        properties.clear();
        Network network = Network.newNetwork();
        postgresql.withInitScript("data_index_create.sql");
        postgresql.withNetwork(network);
        postgresql.withNetworkAliases("postgresql");
        postgresql.waitingFor(Wait.forListeningPort());
        postgresql.start();
        kafka.withNetwork(network);
        kafka.withNetworkAliases("kafka");
        kafka.waitingFor(Wait.forListeningPort());
        kafka.start();
        String kafkaURL = kafka.getBootstrapServers();
        properties.put("kafka.bootstrap.servers", kafkaURL);
        properties.put("spring.kafka.bootstrap-servers", kafkaURL);

        dataIndex.addProtoFileFolder();
        dataIndex.withNetwork(network);
        dataIndex
                .setPostgreSqlURL("jdbc:postgresql://postgresql:5432/" + postgresql.getDatabaseName(), postgresql.getUsername(),
                        postgresql.getPassword());
        dataIndex.setKafkaURL("kafka:29092");
        dataIndex.start();
        LOGGER.debug("PostgreSQL Quarkus test resource started");
    }

    @Override
    public void stop() {
        dataIndex.stop();
        postgresql.stop();
        kafka.stop();
        LOGGER.debug("PostgreSQL Quarkus test resource stopped");
    }

    @Override
    public int getMappedPort() {
        return dataIndex.getMappedPort();
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
