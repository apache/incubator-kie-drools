/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventSerializer;

public class MockKafkaEventEmitterFactory extends KafkaEventEmitterFactory {

    public static MockProducer<byte[], CloudEvent> producer = new MockProducer<>(true, new ByteArraySerializer(), new CloudEventSerializer() {
        @Override
        public byte[] serialize(String topic, CloudEvent data) {
            return super.serialize(topic, new RecordHeaders(), data);
        }
    });

    @Override
    public int ordinal() {
        return 1;
    }

    @Override
    protected Producer<byte[], CloudEvent> createKafkaProducer() {
        return producer;
    }
}
