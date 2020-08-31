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

package org.kie.kogito.trusty.service.messaging.incoming;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.core.type.TypeReference;
import io.cloudevents.v1.AttributesImpl;
import io.cloudevents.v1.CloudEventImpl;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.messaging.BaseEventConsumer;

@ApplicationScoped
public class TraceEventConsumer extends BaseEventConsumer<TraceEvent> {

    private static final TypeReference<CloudEventImpl<TraceEvent>> CLOUD_EVENT_TYPE_REF = new TypeReference<>() {
    };

    private TraceEventConsumer() {
        //CDI proxy
    }

    @Inject
    public TraceEventConsumer(TrustyService service) {
        super(service);
    }

    @Override
    @Incoming("kogito-tracing-decision")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        return super.handleMessage(message);
    }

    @Override
    protected TypeReference<CloudEventImpl<TraceEvent>> getCloudEventType() {
        return CLOUD_EVENT_TYPE_REF;
    }

    @Override
    protected void handleCloudEvent(CloudEventImpl<TraceEvent> cloudEvent) {
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
            service.processDecision(attributes.getId(), attributes.getSource().toString(), TraceEventConverter.toDecision(traceEvent, attributes.getSource().toString()));
        } else {
            LOG.error("Unsupported TraceEvent type {}", traceEventType);
        }
    }
}
