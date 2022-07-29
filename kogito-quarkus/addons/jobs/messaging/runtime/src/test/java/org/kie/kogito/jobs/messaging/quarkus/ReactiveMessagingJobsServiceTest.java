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

package org.kie.kogito.jobs.messaging.quarkus;

import java.net.URI;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveMessagingJobsServiceTest extends AbstractReactiveMessagingJobsServiceTest<ReactiveMessagingJobsService> {

    @Override
    protected ReactiveMessagingJobsService createJobsService(URI serviceUrl, ObjectMapper objectMapper, Emitter<String> eventsEmitter) {
        return new ReactiveMessagingJobsService(serviceUrl, objectMapper, eventsEmitter);
    }

    @Override
    protected void verifyEmitterWasInvoked(int times, String... expectedPayloads) {
        super.verifyEmitterWasInvoked(times, expectedPayloads);
        for (int i = 0; i < times; i++) {
            Message<String> message = messageCaptor.getAllValues().get(i);
            assertThat(message.getMetadata()).isEqualTo(Metadata.empty());
        }
    }
}
