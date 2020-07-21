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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;
import org.kie.kogito.trusty.service.ITrustyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TraceEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TraceEventConsumer.class);

    private final ITrustyService service;

    @Inject
    public TraceEventConsumer(ITrustyService service) {
        this.service = service;
    }

    @Incoming("kogito-tracing")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        decodeCloudEvent(message.getPayload()).ifPresent(this::handleCloudEvent);
        return message.ack();
    }

    private Optional<CloudEventImpl<TraceEvent>> decodeCloudEvent(String payload) {
        try {
            return Optional.of(CloudEventUtils.decode(payload));
        } catch (IllegalStateException e) {
            LOG.error(String.format("Can't decode message to CloudEvent: %s", payload), e);
            return Optional.empty();
        }
    }

    private void handleCloudEvent(CloudEventImpl<TraceEvent> cloudEvent) {
        AttributesImpl attributes = cloudEvent.getAttributes();
        Optional<TraceEvent> optData = cloudEvent.getData();

        if (!optData.isPresent()) {
            LOG.error("Received CloudEvent with id {} from {} with empty data", attributes.getId(), attributes.getSource());
            return;
        }

        LOG.debug("Received CloudEvent with id {} from {}", attributes.getId(), attributes.getSource());

        TraceEvent traceEvent = optData.get();
        TraceEventType traceEventType = traceEvent.getHeader().getType();

        if (traceEventType == TraceEventType.DMN) {
            service.storeDecision(attributes.getId(), TraceEventConverter.toDecision(traceEvent));
        } else {
            LOG.error("Unsupported TraceEvent type {}", traceEventType);
        }
    }
}
