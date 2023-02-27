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

package org.kie.kogito.taskassigning.resources;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.index.testcontainers.AbstractDataIndexContainer;
import org.kie.kogito.index.testcontainers.DataIndexInfinispanContainer;
import org.kie.kogito.taskassigning.testcontainers.TaskAssigningProcessesContainer;
import org.kie.kogito.taskassigning.testcontainers.TaskAssigningServiceContainer;
import org.kie.kogito.test.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoInfinispanContainer;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;

public class TaskAssigningServiceResource implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningServiceResource.class);

    private static final String USER_DEF_RESOURCE = "users.properties";

    private final KogitoKafkaContainer kafka = new KogitoKafkaContainer();
    private final KogitoInfinispanContainer infinispan = new KogitoInfinispanContainer();
    private final DataIndexInfinispanContainer dataIndex = new DataIndexInfinispanContainer();
    private final TaskAssigningProcessesContainer taskAssigningProcesses = new TaskAssigningProcessesContainer();
    private final TaskAssigningServiceContainer taskAssigningService = new TaskAssigningServiceContainer();

    private final Map<String, String> properties = new HashMap<>();

    @Override
    public String getResourceName() {
        return taskAssigningService.getResourceName();
    }

    @Override
    public void start() {
        LOGGER.debug("Start Infinispan test resource");
        properties.clear();
        Network network = Network.newNetwork();
        infinispan.withNetwork(network);
        infinispan.withNetworkAliases("infinispan");
        infinispan.start();
        // external access url
        String infinispanURL = "localhost:" + infinispan.getMappedPort();
        properties.put("quarkus.infinispan-client.hosts", infinispanURL);

        LOGGER.debug("Start Kafka test resource");
        kafka.withNetwork(network);
        kafka.withNetworkAliases("kafka");
        kafka.start();
        // external access url
        String kafkaURL = kafka.getBootstrapServers();
        // internal access url
        String kafkaInternalUrl = "kafka:29092";
        properties.put("kafka.bootstrap.servers", kafkaURL);

        LOGGER.debug("Start DataIndex test resource");
        dataIndex.addProtoFileFolder();
        dataIndex.withNetwork(network);
        dataIndex.withNetworkAliases("dataindex");
        dataIndex.setInfinispanURL("infinispan:11222");
        dataIndex.setKafkaURL(kafkaInternalUrl);
        dataIndex.addEnv("QUARKUS_PROFILE", "kafka-events-support");
        dataIndex.start();

        LOGGER.debug("Start TaskAssigningProcesses test resource");
        taskAssigningProcesses.withNetwork(network);
        taskAssigningProcesses.withNetworkAliases("processes");
        String kogitoServiceURL = "http://processes:" + TaskAssigningProcessesContainer.PORT;
        taskAssigningProcesses.setKogitoServiceURL(kogitoServiceURL);
        taskAssigningProcesses.setKafkaURL("kafka:29092");
        taskAssigningProcesses.start();

        LOGGER.debug("Start TaskAssigningService test resource");
        taskAssigningService.withNetwork(network);
        taskAssigningService.withNetworkAliases("taskassigning");
        taskAssigningService.setKafkaURL(kafkaInternalUrl);
        String dataIndexURL = "http://dataindex:" + AbstractDataIndexContainer.PORT + "/graphql";
        taskAssigningService.setDataIndexUrl(dataIndexURL);
        taskAssigningService.setUsersFromResource(USER_DEF_RESOURCE);
        taskAssigningService.start();
    }

    @Override
    public void stop() {
        taskAssigningService.stop();
        taskAssigningProcesses.stop();
        dataIndex.stop();
        infinispan.stop();
        kafka.stop();
    }

    @Override
    public int getMappedPort() {
        return taskAssigningService.getMappedPort();
    }

    public int getDataIndexMappedPort() {
        return dataIndex.getMappedPort();
    }

    public int getKogitoServiceMappedPort() {
        return taskAssigningProcesses.getMappedPort();
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
