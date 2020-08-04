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

package org.kie.kogito.trusty.service.messaging;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyKafkaTestResource;
import org.kie.kogito.trusty.service.TrustyService;

import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCloudEventJsonString;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectModelEvent;
import static org.kie.kogito.trusty.service.messaging.KafkaUtils.generateProducer;
import static org.kie.kogito.trusty.service.messaging.KafkaUtils.sendToKafkaAndWaitForCompletion;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
@QuarkusTestResource(TrustyKafkaTestResource.class)
public class ModelEventConsumerIT {

    @InjectMock
    TrustyService trustyService;

    KafkaProducer<String, String> producer;

    @BeforeEach
    public void setup() {
        producer = generateProducer();
    }

    @Test
    public void eventLoopIsNotStoppedWithException() throws Exception {
        doThrow(new RuntimeException("Something really bad"))
                .doNothing()
                .when(trustyService)
                .storeModel(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        sendToKafkaAndWaitForCompletion(KafkaUtils.KOGITO_TRACING_MODEL_TOPIC,
                                        buildCloudEventJsonString(buildCorrectModelEvent()),
                                        producer);
        sendToKafkaAndWaitForCompletion(KafkaUtils.KOGITO_TRACING_MODEL_TOPIC,
                                        buildCloudEventJsonString(buildCorrectModelEvent()),
                                        producer);

        verify(trustyService, times(2))
                .storeModel(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
