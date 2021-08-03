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

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.ChannelRegistar;
import io.smallrye.reactive.messaging.ChannelRegistry;
import io.smallrye.reactive.messaging.extension.EmitterConfiguration;
import io.smallrye.reactive.messaging.extension.MediatorManager;

@ApplicationScoped
public class QuarkusMultiCloudEventEmitter extends AbstractQuarkusCloudEventEmitter implements ChannelRegistar {

    private static Logger logger = LoggerFactory.getLogger(QuarkusMultiCloudEventEmitter.class);

    @Inject
    private ChannelRegistry channelRegistry;

    @Inject
    private MediatorManager mediatorManager;

    @Inject
    private ChannelResolver channelResolver;

    private EmitterConfiguration emitterConf(String channel) {
        return new EmitterConfiguration(channel, false, null, null);
    }

    @Override
    public <T> CompletionStage<Void> emit(T e, String type, Optional<Function<T, Object>> processDecorator) {
        Message<String> message = processMessage(e, processDecorator);
        Emitter<String> emitter = (Emitter<String>) channelRegistry.getEmitter(type);
        if (emitter != null) {
            emitter.send(message);
        } else {
            logger.warn("Cannot found channel {}. Please add it to application.properties", type);
        }
        return message.getAck().get();
    }

    @Override
    public void initialize() {
        channelResolver.getOutputChannels().stream().map(this::emitterConf).forEach(mediatorManager::addEmitter);
    }
}
