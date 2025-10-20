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
package org.kie.kogito.serverless.workflow.executor;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;

import com.fasterxml.jackson.databind.JsonNode;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;

public class KafkaEventEmitter implements EventEmitter {

    private final Producer<byte[], CloudEvent> kafkaProducer;
    private final String topic;

    public KafkaEventEmitter(Producer<byte[], CloudEvent> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void emit(DataEvent<?> dataEvent) {
        kafkaProducer.send(new ProducerRecord<>(topic, dataEvent.asCloudEvent(o -> JsonCloudEventData.wrap((JsonNode) o))));
    }
}
