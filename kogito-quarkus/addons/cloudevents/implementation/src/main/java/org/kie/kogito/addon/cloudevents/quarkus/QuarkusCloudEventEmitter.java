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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.ChannelRegistar;
import io.smallrye.reactive.messaging.ChannelRegistry;
import io.smallrye.reactive.messaging.extension.EmitterConfiguration;
import io.smallrye.reactive.messaging.extension.MediatorManager;

@ApplicationScoped
@Named(KogitoEventStreams.DEFAULT_OUTGOING_BEAN_NAME)
public class QuarkusCloudEventEmitter extends AbstractQuarkusCloudEventEmitter implements ChannelRegistar {

    private static final String PROPERTY = "mp.messaging.outgoing." + KogitoEventStreams.OUTGOING + ".connector";

    private static final Logger logger = LoggerFactory.getLogger(QuarkusCloudEventEmitter.class);

    @Inject
    private ChannelRegistry channelRegistry;

    @Inject
    private MediatorManager mediatorManager;

    private Emitter<String> emitter;

    @Override
    public void initialize() {
        if (ConfigProvider.getConfig().getOptionalValue(PROPERTY, String.class).isPresent()) {
            logger.info("Registering emitter {}", KogitoEventStreams.OUTGOING);
            mediatorManager.addEmitter(new EmitterConfiguration(KogitoEventStreams.OUTGOING, false, null, null));
        }
    }

    @Override
    protected void emit(Message<String> message) {
        if (emitter == null) {
            emitter = (Emitter<String>) channelRegistry.getEmitter(KogitoEventStreams.OUTGOING);
        }
        if (emitter != null) {
            emitter.send(message);
        } else {
            logger.warn("Cannot find emitter {}", KogitoEventStreams.OUTGOING);
        }
    }
}
