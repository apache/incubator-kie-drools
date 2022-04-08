/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.test.resources;

import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

public class JobServiceKafkaResource extends AbstractJobServiceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceKafkaResource.class);

    private final KogitoKafkaContainer kafka = new KogitoKafkaContainer();

    @Override
    public void start() {
        LOGGER.info("Start JobServiceKafka test resource");
        properties.clear();
        LOGGER.info("Start Kafka");
        Network network = Network.newNetwork();
        kafka.withNetwork(network);
        kafka.withNetworkAliases("kafka");
        kafka.waitingFor(Wait.forListeningPort());
        kafka.start();
        // external access url
        String kafkaURL = kafka.getBootstrapServers();
        LOGGER.info("kafkaURL: {}", kafka.getBootstrapServers());
        // internal access url
        String kafkaInternalUrl = "kafka:29092";
        LOGGER.info("kafkaInternalURL: {}", kafkaInternalUrl);
        properties.put("kafka.bootstrap.servers", kafkaURL);
        properties.put("spring.kafka.bootstrap-servers", kafkaURL);
        properties.put("quarkus.profile", "events-support");

        jobService.withNetwork(network);
        jobService.setKafkaURL(kafkaInternalUrl);
        jobService.setQuarkusProfile("events-support");
        jobService.start();
        LOGGER.info("JobServiceKafka test resource started");
    }

    @Override
    public void stop() {
        LOGGER.info("Stop JobServiceKafka test resource");
        jobService.stop();
        kafka.stop();
        LOGGER.info("JobServiceKafka test resource stopped");
    }
}
