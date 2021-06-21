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
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

@Startup
@ApplicationScoped
public class QuarkusCloudEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusCloudEventPublisher.class);

    protected BroadcastProcessor<String> processor = BroadcastProcessor.create();

    /**
     * Broadcasts the received/produced messages to subscribers
     *
     * @see <a href="https://smallrye.io/smallrye-mutiny/guides/hot-streams">How to create a hot stream?</a>
     * @return A {@link Multi} message to subscribers
     */
    @Produces
    @ApplicationScoped
    @Named(KogitoEventStreams.PUBLISHER)
    public Multi<String> producerFactory() {
        return processor;
    }

    /**
     * Listens to a message published in the {@link KogitoEventStreams#INCOMING} channel
     *
     * @param message the given message in JSON format
     * @return a {@link CompletionStage} after ack-ing the message
     */
    @Incoming(KogitoEventStreams.INCOMING)
    public CompletionStage<Void> onEvent(Message<String> message) {
        LOGGER.debug("Received message from channel {}: {}", KogitoEventStreams.INCOMING, message);
        produce(message.getPayload());
        return message
                .ack()
                .exceptionally(e -> {
                    LOGGER.error("Failed to ack message", e);
                    return null;
                });
    }

    /**
     * Produces a message in the internal application bus
     *
     * @param message the given CE message in JSON format
     */
    public void produce(final String message) {
        LOGGER.debug("Producing message to internal bus: {}", message);
        processor.onNext(message);
    }
}
