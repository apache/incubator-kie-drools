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
 */

package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.smallrye.mutiny.Multi;
import org.kie.kogito.event.CloudEventReceiver;
import org.kie.kogito.event.KogitoEventStreams;
import org.reactivestreams.Publisher;

@ApplicationScoped
public class QuarkusCloudEventReceiver implements CloudEventReceiver {

    @Inject
    @Named(KogitoEventStreams.PUBLISHER)
    Publisher<String> eventPublisher;

    @Override
    public void subscribe(Consumer<String> consumer) {
        Multi.createFrom().publisher(eventPublisher).subscribe().with(consumer);
    }

    @Override
    public Publisher<String> getEventPublisher() {
        return eventPublisher;
    }
}
