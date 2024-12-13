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
package org.kie.kogito.testcontainers;

import java.nio.charset.StandardCharsets;

import org.kie.kogito.test.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;

import com.github.dockerjava.api.command.InspectContainerResponse;

import static java.lang.String.format;

/**
 * Kafka Container for Kogito examples.
 */
public class KogitoKafkaContainer extends KogitoGenericContainer<KogitoKafkaContainer> implements TestResource {

    public static final String NAME = "kafka";
    public static final String KAFKA_PROPERTY = "container.image." + NAME;
    public static final int KAFKA_PORT = 9092;
    private static final String STARTER_SCRIPT = "/var/lib/redpanda/redpanda.sh";

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoKafkaContainer.class);

    public KogitoKafkaContainer() {
        super(NAME);
        withExposedPorts(KAFKA_PORT);
        withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"));
        withCommand("-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT);
        withNetworkAliases("kafka");
        waitingFor(Wait.forLogMessage(".*Started Kafka API server.*", 1).withStartupTimeout(Constants.CONTAINER_START_TIMEOUT));
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo, boolean reused) {
        super.containerIsStarting(containerInfo, reused);

        // Start and configure the advertised address
        String command = "#!/bin/bash\n";
        command += "/usr/bin/rpk redpanda start --check=false --node-id 0 --smp 1 ";
        command += "--memory 1G --overprovisioned --reserve-memory 0M ";
        command += "--kafka-addr PLAINTEXT://0.0.0.0:29092,OUTSIDE://0.0.0.0:9092 ";
        command += "--advertise-kafka-addr PLAINTEXT://kafka:29092,OUTSIDE://" + getHost() + ":" + getMappedPort(KAFKA_PORT) + " ";
        command += "--set redpanda.enable_idempotence=true ";
        command += "--set redpanda.enable_transactions=true ";

        copyFileToContainer(Transferable.of(command.getBytes(StandardCharsets.UTF_8), 0777), STARTER_SCRIPT);
    }

    @Override
    public void start() {
        super.start();
        LOGGER.info("Kafka servers: {}", getBootstrapServers());
    }

    public String getBootstrapServers() {
        return format("OUTSIDE://%s:%d", getHost(), getMappedPort(KAFKA_PORT));
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(KAFKA_PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

}
