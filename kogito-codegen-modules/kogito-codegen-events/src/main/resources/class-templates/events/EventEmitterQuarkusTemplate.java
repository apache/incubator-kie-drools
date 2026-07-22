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
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.quarkus.messaging.common.KogitoMessaging;
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
    MessageDecoratorProvider messageDecorator;

    @Override
    public void emit(DataEvent<?> dataEvent) {
        logger.debug("publishing event {}", dataEvent);
        try {
            Message<$Type$> message = messageDecorator.decorate(getMessage(dataEvent));
            emitter.send(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected EventMarshaller<$Type$> getEventMarshaller() {
        return eventDataMarshaller;
    }

    protected CloudEventMarshaller<$Type$> getCloudEventMarshaller() {
        return ceMarshaller;
    }

}
