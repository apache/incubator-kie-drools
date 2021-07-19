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
package org.kie.kogito.addon.cloudevents.spring;

import java.util.function.Consumer;

import org.kie.kogito.addon.cloudevents.JsonStringToObjectConsumer;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.event.SubscriptionInfo;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Component
public class SpringBootCloudEventReceiver implements EventReceiver {

    @Autowired
    @Qualifier(KogitoEventStreams.PUBLISHER)
    Publisher<String> eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> void subscribe(Consumer<T> consumer, SubscriptionInfo<T> info) {
        Flux.from(eventPublisher).subscribe(new JsonStringToObjectConsumer<>(objectMapper, consumer, info.getEventType()));
    }
}
