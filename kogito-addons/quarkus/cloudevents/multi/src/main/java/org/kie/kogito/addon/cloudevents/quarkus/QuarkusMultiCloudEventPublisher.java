/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.smallrye.reactive.messaging.ChannelRegistar;
import io.smallrye.reactive.messaging.DefaultMediatorConfiguration;
import io.smallrye.reactive.messaging.MediatorConfiguration;
import io.smallrye.reactive.messaging.Shape;
import io.smallrye.reactive.messaging.extension.MediatorManager;

@ApplicationScoped
@RegisterForReflection
public class QuarkusMultiCloudEventPublisher implements ChannelRegistar {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusMultiCloudEventPublisher.class);

    @Inject
    private MediatorManager mediatorManager;

    @Inject
    private BeanManager beanManager;

    @Inject
    private ChannelResolver channelResolver;

    private BroadcastProcessor<String> processor;

    @PostConstruct
    private void init() {
        processor = BroadcastProcessor.create();
    }

    private Collection<MediatorConfiguration> mediatorConf(Collection<String> channels) throws NoSuchMethodException {
        return Collections.singletonList(new DefaultMediatorConfiguration(
                QuarkusMultiCloudEventPublisher.class.getMethod("produce", Message.class),
                beanManager.resolve(beanManager.getBeans(QuarkusMultiCloudEventPublisher.class))) {

            @Override
            public List<String> getIncoming() {
                return new ArrayList<>(channels);
            }

            @Override
            public Shape shape() {
                return Shape.SUBSCRIBER;
            }

            @Override
            public Consumption consumption() {
                return Consumption.MESSAGE;
            }
        });
    }

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
     * Produces a message in the internal application bus
     *
     * @param message the given CE message in JSON format
     */
    public CompletionStage<Void> produce(Message<String> message) {
        LOGGER.debug("Received message from channel {}: {}", KogitoEventStreams.INCOMING, message);
        processor.onNext(message.getPayload());
        return message.ack()
                .exceptionally(e -> {
                    LOGGER.error("Failed to ack message", e);
                    return null;
                });
    }

    @Override
    public void initialize() {
        try {
            Collection<String> inputChannels = channelResolver.getInputChannels();
            if (!inputChannels.isEmpty()) {
                mediatorManager.addAnalyzed(mediatorConf(inputChannels));
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

}
