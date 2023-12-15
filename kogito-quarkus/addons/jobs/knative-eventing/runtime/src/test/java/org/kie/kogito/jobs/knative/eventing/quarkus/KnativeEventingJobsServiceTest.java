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
package org.kie.kogito.jobs.knative.eventing.quarkus;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.messaging.quarkus.AbstractReactiveMessagingJobsServiceTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata;

import jakarta.ws.rs.core.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.kogito.addon.quarkus.common.reactive.messaging.http.CloudEventHttpOutgoingDecorator.CLOUD_EVENTS_CONTENT_TYPE;

class KnativeEventingJobsServiceTest extends AbstractReactiveMessagingJobsServiceTest<KnativeEventingJobsService> {

    @Override
    protected KnativeEventingJobsService createJobsService(URI serviceUrl, ObjectMapper objectMapper, Emitter<String> eventsEmitter) {
        return new KnativeEventingJobsService(serviceUrl, objectMapper, eventsEmitter);
    }

    @Override
    protected void verifyEmitterWasInvoked(int times, String... expectedPayloads) {
        super.verifyEmitterWasInvoked(times, expectedPayloads);
        for (int i = 0; i < times; i++) {
            Message<String> message = messageCaptor.getAllValues().get(i);
            assertHasExpectedHttpMetadata(message);
        }
    }

    private void assertHasExpectedHttpMetadata(Message<String> message) {
        Optional<OutgoingHttpMetadata> httpMetadata = message.getMetadata(OutgoingHttpMetadata.class);
        if (httpMetadata.isEmpty()) {
            fail("Message doesn't have the expected OutgoingHttpMetadata");
        } else {
            assertThat(httpMetadata.get().getHeaders()).hasSize(1);
            List<String> contentTypeValues = httpMetadata.get().getHeaders().get(HttpHeaders.CONTENT_TYPE);
            assertThat(contentTypeValues).containsExactly(CLOUD_EVENTS_CONTENT_TYPE);
        }
    }
}
