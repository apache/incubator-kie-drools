/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import org.junit.jupiter.api.Test;
import org.kie.api.management.GAV;
import org.kie.internal.decision.DecisionModelResource;
import org.kie.internal.decision.DecisionModelResourcesProvider;
import org.kie.kogito.decision.DecisionModelType;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpringBootModelEventEmitterTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
    private static final String TEST_TOPIC = "test-topic";

    @Test
    public void testEmitEvent() throws JsonProcessingException {
        @SuppressWarnings("unchecked")
        final KafkaTemplate<String, String> mockedKarkaTemplate = mock(KafkaTemplate.class);
        final List<DecisionModelResource> models = Arrays.asList(makeModel(), makeModel());
        final DecisionModelResourcesProvider mockedDecisionModelResourcesProvider = () -> models;

        final SpringBootModelEventEmitter eventEmitter = new SpringBootModelEventEmitter(mockedDecisionModelResourcesProvider, mockedKarkaTemplate, TEST_TOPIC);
        eventEmitter.publishDecisionModels();

        final ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockedKarkaTemplate, times(2)).send(topicCaptor.capture(), payloadCaptor.capture());

        topicCaptor.getAllValues().forEach(v -> assertEquals(TEST_TOPIC, v));

        final String rawCloudEvent1 = payloadCaptor.getAllValues().get(0);
        final String rawCloudEvent2 = payloadCaptor.getAllValues().get(1);
        final CloudEvent cloudEvent1 = OBJECT_MAPPER.readValue(rawCloudEvent1, CloudEvent.class);
        final CloudEvent cloudEvent2 = OBJECT_MAPPER.readValue(rawCloudEvent2, CloudEvent.class);

        assertEquals("id", cloudEvent1.getId());
        assertEquals("id", cloudEvent2.getId());
    }

    private DecisionModelResource makeModel() {
        final DecisionModelResource model = mock(DecisionModelResource.class);
        when(model.getGav()).thenReturn(new GAV("groupId", "artifactId", "version"));
        when(model.getModelName()).thenReturn("name");
        when(model.getNamespace()).thenReturn("namespace");
        when(model.getModelType()).thenReturn(DecisionModelType.DMN);
        when(model.get()).thenReturn("model");
        return model;
    }
}
