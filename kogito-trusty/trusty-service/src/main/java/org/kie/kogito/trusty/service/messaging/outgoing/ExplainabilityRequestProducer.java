/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service.messaging.outgoing;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.reactivex.BackpressureStrategy;
import io.reactivex.subjects.PublishSubject;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.cloudevents.CloudEventUtils;
import org.kie.kogito.explainability.api.ExplainabilityRequestDto;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExplainabilityRequestProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplainabilityRequestProducer.class);

    private static final URI URI_PRODUCER = URI.create("trustyService/ExplainabilityRequestProducer");

    private final PublishSubject<String> eventSubject = PublishSubject.create();

    public void sendEvent(ExplainabilityRequestDto request) {
        LOGGER.info("Sending explainability request with id {}", request.getExecutionId());
        Optional<String> optPayload = CloudEventUtils
                .build(request.getExecutionId(), URI_PRODUCER, request, ExplainabilityRequestDto.class)
                .flatMap(CloudEventUtils::encode);
        if (optPayload.isPresent()) {
            eventSubject.onNext(optPayload.get());
        } else {
            LOGGER.warn("Ignoring empty CloudEvent");
        }
    }

    @Outgoing("trusty-explainability-request")
    public Publisher<String> getEventPublisher() {
        return eventSubject.toFlowable(BackpressureStrategy.BUFFER);
    }
}