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
package $Package$;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;

import org.kie.kogito.addon.quarkus.messaging.common.KogitoMessaging;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.quarkus.common.reactive.messaging.MessageDecoratorProvider;
import org.kie.kogito.addon.quarkus.messaging.common.AbstractQuarkusCloudEventEmitter;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.EventUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
@Named("Emitter-$ChannelName$")
public class $ClassName$ extends AbstractQuarkusCloudEventEmitter<$Type$> {

    private static final Logger logger = LoggerFactory.getLogger($ClassName$.class);

    @Inject
    @Channel("$ChannelName$")
    Emitter<$Type$> emitter;

    @Inject
    Event<EmitEventType> event;

    @Inject
    MessageDecoratorProvider messageDecorator;

    class EmitEventType {
        DataEvent<?> data;

        public EmitEventType(DataEvent<?> data) {
            this.data = data;
        }
    }

    public void observe(@Observes(during = TransactionPhase.AFTER_SUCCESS) EmitEventType emitEventType) {
        logger.debug("publishing event {}", emitEventType.data);
        try {
            Message<$Type$> message = messageDecorator.decorate(getMessage(emitEventType.data));
            emitter.send(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    @Transactional
    public void emit(DataEvent<?> dataEvent) {
        event.fire(new EmitEventType(dataEvent));
    }

    protected EventMarshaller<$Type$> getEventMarshaller() {
        return eventDataMarshaller;
    }

    protected CloudEventMarshaller<$Type$> getCloudEventMarshaller() {
        return ceMarshaller;
    }

}
