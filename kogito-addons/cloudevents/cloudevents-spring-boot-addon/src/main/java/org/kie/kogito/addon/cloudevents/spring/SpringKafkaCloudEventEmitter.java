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

package org.kie.kogito.addon.cloudevents.spring;

import java.util.concurrent.CompletionStage;

import org.kie.kogito.event.KogitoEventStreams;
import org.kie.kogito.services.event.CloudEventEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Spring implementation delegating to kafka template
 */
@Component
public class SpringKafkaCloudEventEmitter implements CloudEventEmitter {

    @Autowired
    org.springframework.kafka.core.KafkaTemplate<String, String> emitter;
    @Value(value = "${spring.kafka.bootstrap-servers}")
    String kafkaBootstrapAddress;
    @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.OUTGOING + ":" + KogitoEventStreams.OUTGOING + "}")
    String kafkaTopicName;

    public CompletionStage<Void> emit(String e) {
        return emitter.send(kafkaTopicName, e)
                .completable()
                .thenApply(r -> null); // discard return to comply with the signature
    }

}
