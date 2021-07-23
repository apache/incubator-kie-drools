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

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.event.KogitoEventStreams;

import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class QuarkusCloudEventReceiver extends AbstractQuarkusCloudEventReceiver {
    /**
     * Listens to a message published in the {@link KogitoEventStreams#INCOMING} channel
     *
     * @param message the given message in string format
     */
    @Incoming(KogitoEventStreams.INCOMING)
    public CompletionStage<?> onEvent(Message<String> message) {
        return produce(message);
    }
}
