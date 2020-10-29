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
 *
 */

package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.services.event.CloudEventEmitter;

/**
 * the quarkus implementation just delegates to a real emitter,
 * since smallrye reactive messaging handles different transports
 *
 */
@ApplicationScoped
public class QuarkusCloudEventEmitter implements CloudEventEmitter {
    @Inject
    @Channel(KogitoEventStreams.OUTGOING)
    Emitter<String> emitter;

    public CompletionStage<Void> emit(String e) {
        return emitter.send(e);
    }
}
