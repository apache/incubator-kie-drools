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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;

import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.kie.kogito.addon.quarkus.messaging.common.KogitoMessaging;
import org.kie.kogito.addon.quarkus.messaging.common.AbstractQuarkusCloudEventReceiver;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.EventUnmarshaller;

import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@Startup
@ApplicationScoped
@Named("Receiver-$ChannelName$")
public class $ClassName$ extends AbstractQuarkusCloudEventReceiver<$Type$> {

    @Incoming("$ChannelName$")
    @Blocking
    public CompletionStage<Void> onEvent(Message<$Type$> payload) {
        try {
            produce(payload);
        } catch (Exception ex) {
            return payload.nack(ex);
        }
        return payload.ack();
    }

    protected EventUnmarshaller<$Type$> getEventUnmarshaller() {
        return eventDataUnmarshaller;
    }

    protected CloudEventUnmarshallerFactory<$Type$> getCloudEventUnmarshallerFactory() {
        return ceUnmarshaller;
    }

}
