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

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.services.event.impl.DefaultEventMarshaller;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring implementation delegating to kafka template
 */
@Component
public class SpringKafkaCloudEventEmitter implements EventEmitter {

    @Autowired
    org.springframework.kafka.core.KafkaTemplate<String, String> emitter;
    @Value(value = "${spring.kafka.bootstrap-servers}")
    String kafkaBootstrapAddress;
    @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + ":" + KogitoEventStreams.OUTGOING + "}")
    String defaultTopicName;
    @Autowired
    Environment env;
    @Autowired
    ObjectProvider<EventMarshaller> marshallerInstance;
    private EventMarshaller marshaller;
    @Autowired
    ConfigBean configBean;
    @Autowired
    ObjectMapper mapper;

    @PostConstruct
    void init() {
        marshaller = marshallerInstance.getIfAvailable(() -> new DefaultEventMarshaller(mapper));
    }

    @Override
    public <T> CompletionStage<Void> emit(T e, String type, Optional<Function<T, Object>> processDecorator) {
        return emitter
                .send(
                        env.getProperty("kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + "." + type,
                                defaultTopicName),
                        marshaller.marshall(configBean.useCloudEvents() ? processDecorator.map(d -> d
                                .apply(e)).orElse(e) : e))
                .completable()
                .thenApply(r -> null); // discard return to comply with the signature
    }

}
