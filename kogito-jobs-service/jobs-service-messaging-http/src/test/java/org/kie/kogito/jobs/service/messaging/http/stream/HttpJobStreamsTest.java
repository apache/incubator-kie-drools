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
package org.kie.kogito.jobs.service.messaging.http.stream;

import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.stream.AbstractJobStreamsTest;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

import jakarta.ws.rs.core.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.messaging.http.stream.HttpJobStreams.PARTITION_KEY_EXTENSION;

class HttpJobStreamsTest extends AbstractJobStreamsTest<HttpJobStreams> {

    @Override
    protected HttpJobStreams createJobStreams() {
        return new HttpJobStreams(objectMapper, Optional.of(true), emitter, AbstractJobStreamsTest.URL);
    }

    @Override
    protected void assertExpectedMetadata(Message<String> message) {
        OutgoingHttpMetadata metadata = message.getMetadata(OutgoingHttpMetadata.class).orElse(null);
        assertThat(metadata).isNotNull();
        assertThat(metadata.getHeaders()).hasSize(1);
        assertThat(metadata.getHeaders().get(HttpHeaders.CONTENT_TYPE)).containsExactlyInAnyOrder(JsonFormat.CONTENT_TYPE);
    }

    @Override
    protected void assertExpectedEvent(JobDataEvent event) {
        super.assertExpectedEvent(event);
        assertThat(event.getExtension(PARTITION_KEY_EXTENSION))
                .isNotNull()
                .isEqualTo(JOB_ID);
    }
}
