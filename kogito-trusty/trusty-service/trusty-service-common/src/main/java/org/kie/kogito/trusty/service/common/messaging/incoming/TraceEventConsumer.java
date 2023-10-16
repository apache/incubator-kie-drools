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
package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.tracing.event.trace.TraceEvent;
import org.kie.kogito.tracing.event.trace.TraceEventType;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.messaging.BaseEventConsumer;
import org.kie.kogito.trusty.storage.api.StorageExceptionsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.cloudevents.CloudEvent;

@ApplicationScoped
public class TraceEventConsumer extends BaseEventConsumer<TraceEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(TraceEventConsumer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectWriter WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    private static final TypeReference<TraceEvent> CLOUD_EVENT_TYPE = new TypeReference<>() {
    };

    protected TraceEventConsumer() {
        //CDI proxy
    }

    @Inject
    public TraceEventConsumer(TrustyService service,
            ObjectMapper mapper,
            StorageExceptionsProvider storageExceptionsProvider,
            ManagedExecutor executor) {
        super(service,
                mapper,
                storageExceptionsProvider,
                executor);
    }

    @Override
    @Incoming("kogito-tracing-decision")
    public CompletionStage<Void> handleMessage(Message<String> message) {
        return CompletableFuture.runAsync(() -> super.handleMessage(message), executor);
    }

    @Override
    protected void internalHandleCloudEvent(CloudEvent cloudEvent, TraceEvent payload) {
        TraceEventType traceEventType = payload.getHeader().getType();

        if (traceEventType == TraceEventType.DMN) {
            String sourceUrl = cloudEvent.getSource().toString();
            String serviceUrl = payload.getHeader().getResourceId().getServiceUrl();

            logEvent(payload);

            service.processDecision(cloudEvent.getId(),
                    TraceEventConverter.toDecision(payload, sourceUrl, serviceUrl));
        } else {
            LOG.error("Unsupported TraceEvent type {}", traceEventType);
        }
    }

    @Override
    protected TypeReference<TraceEvent> getEventType() {
        return CLOUD_EVENT_TYPE;
    }

    private void logEvent(TraceEvent event) {
        if (LOG.isTraceEnabled()) {
            try {
                LOG.trace(String.format("Incoming event:-\n%s", WRITER.writeValueAsString(event)));
            } catch (JsonProcessingException jpe) {
                //Swallow
            }
        }
    }
}
