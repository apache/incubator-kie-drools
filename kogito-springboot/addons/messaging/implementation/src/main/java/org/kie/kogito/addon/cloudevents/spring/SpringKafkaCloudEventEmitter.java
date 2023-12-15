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
package org.kie.kogito.addon.cloudevents.spring;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.KogitoEventStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring implementation delegating to kafka template
 */
@Component
public class SpringKafkaCloudEventEmitter<M> implements EventEmitter {

    @Autowired
    org.springframework.kafka.core.KafkaTemplate<String, M> emitter;
    @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + ":" + KogitoEventStreams.OUTGOING + "}")
    String defaultTopicName;
    @Autowired
    Environment env;
    @Autowired
    EventMarshaller<M> marshaller;
    @Autowired
    CloudEventMarshaller<M> ceMarshaller;
    @Autowired
    ConfigBean configBean;
    @Autowired
    ObjectMapper mapper;

    @Override
    public CompletionStage<Void> emit(DataEvent<?> event) {
        try {
            return emitter
                    .send(
                            env.getProperty("kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + "." + event.getType(),
                                    defaultTopicName),
                            configBean.useCloudEvents() ? ceMarshaller.marshall(event.asCloudEvent(ceMarshaller.cloudEventDataFactory())) : marshaller.marshall(event.getData()))
                    .thenApply(r -> null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
