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
import java.io.UncheckedIOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.event.EventUnmarshaller;
import org.kie.kogito.event.KogitoEventStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.kie.kogito.addon.cloudevents.spring.KogitoMessaging;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring implementation delegating to kafka template
 */
@Component("Emitter-$ChannelName$")
public class $ClassName$ implements EventEmitter {

    @Autowired
    org.springframework.kafka.core.KafkaTemplate<String, $Type$> emitter;

    @Autowired
    ObjectMapper mapper;

    @Override
    public void emit(DataEvent<?> event) {
        emitter.send("$Topic$", toTopicType(event));
    }

    private $Type$ toTopicTypeCloud(DataEvent<?> event) {
        try {
            return ceMarshaller.marshall(event.asCloudEvent(ceMarshaller.cloudEventDataFactory()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private $Type$ toTopicTypeEvent(DataEvent<?> event) {
        return eventDataMarshaller.marshall(event.getData());
    }
}