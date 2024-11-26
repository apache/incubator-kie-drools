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
package org.kie.kogito.jobs.service.messaging.kafka.stream;

import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.service.stream.AbstractJobStreamsTest;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaJobStreamsTest extends AbstractJobStreamsTest<KafkaJobStreams> {

    @Override
    protected KafkaJobStreams createJobStreams() {
        return new KafkaJobStreams(objectMapper, Optional.of(true), emitter, URL);
    }

    @Override
    protected void assertExpectedMetadata(Message<String> message) {
        OutgoingKafkaRecordMetadata<?> metadata = message.getMetadata(OutgoingKafkaRecordMetadata.class).orElse(null);
        assertThat(metadata).isNotNull();
        assertThat(metadata.getKey()).isEqualTo(JOB_ID);
    }
}
