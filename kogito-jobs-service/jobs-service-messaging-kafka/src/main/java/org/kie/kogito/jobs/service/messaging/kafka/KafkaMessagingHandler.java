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
package org.kie.kogito.jobs.service.messaging.kafka;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.kie.kogito.jobs.service.messaging.MessagingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.kafka.KafkaConnector;

@ApplicationScoped
public class KafkaMessagingHandler implements MessagingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessagingHandler.class);

    @Inject
    @Connector(value = "smallrye-kafka")
    KafkaConnector kafkaConnector;

    @Override
    public void pause() {
        kafkaConnector.getConsumerChannels().forEach(c -> {
            LOGGER.debug("pausing kafka channel: {}", c);
            kafkaConnector.getConsumer(c).pause();
        });
    }

    @Override
    public void resume() {
        kafkaConnector.getConsumerChannels().forEach(c -> {
            LOGGER.debug("resuming kafka channel: {}", c);
            kafkaConnector.getConsumer(c).resume();
        });
    }
}
