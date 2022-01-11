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
package org.kie.kogito.tracing.decision;

import org.kie.kogito.tracing.EventEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpringBootTraceEventEmitter implements EventEmitter {

    private final KafkaTemplate<String, String> template;
    private final String kafkaTopicName;

    @Autowired
    public SpringBootTraceEventEmitter(final KafkaTemplate<String, String> template,
            final @Value(value = "${kogito.addon.tracing.decision.kafka.topic.name:kogito-tracing-decision}") String kafkaTopicName) {
        this.template = template;
        this.kafkaTopicName = kafkaTopicName;
    }

    @Override
    public void emit(final String payload) {
        template.send(kafkaTopicName, payload);
    }
}
