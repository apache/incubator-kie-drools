/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.trusty.service.TrustyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseEventConsumer<E> {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseEventConsumer.class);

    protected final TrustyService service;

    protected BaseEventConsumer() {
        this(null);
    }

    public BaseEventConsumer(final TrustyService service) {
        this.service = service;
    }

    protected CompletionStage<Void> handleMessage(final Message<String> message) {
        try {
            decodeCloudEvent(message.getPayload()).ifPresent(this::handleCloudEvent);
        } catch (Exception e) {
            LOG.error("Something unexpected happened during the processing of an Event. The event is discarded.", e);
        }
        return message.ack();
    }

    protected Optional<CloudEventImpl<E>> decodeCloudEvent(final String payload) {
        try {
            return Optional.of(CloudEventUtils.decode(payload, getCloudEventType()));
        } catch (IllegalStateException e) {
            LOG.error(String.format("Can't decode message to CloudEvent: %s", payload), e);
            return Optional.empty();
        }
    }

    protected abstract TypeReference<CloudEventImpl<E>> getCloudEventType();

    protected abstract void handleCloudEvent(final CloudEventImpl<E> cloudEvent);
}
