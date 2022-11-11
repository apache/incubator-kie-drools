/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.addons.knative.eventing;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator;
import org.kie.kogito.addon.cloudevents.message.MessagePayloadDecoratorProvider;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class KnativeEventingMessagePayloadDecoratorTest {

    @Test
    public void verifyMergeFromValidCeOverrides() throws JsonProcessingException, URISyntaxException {
        final ObjectMapper mapper = CloudEventUtils.Mapper.mapper();

        KnativeEventingMessagePayloadDecorator decorator = new KnativeEventingMessagePayloadDecorator();
        KnativeEventingMessagePayloadDecorator decoratorSpy = Mockito.spy(decorator);

        Mockito.when(decoratorSpy.readEnvCeOverrides()).thenReturn("{ \"extensions\": { \"" + CloudEventExtensionConstants.ADDONS + "\": \"knative-eventing\"} }");
        final CloudEvent ce = new CloudEventBuilder()
                .withType("unitTest")
                .withSource(new URI("http://localhost"))
                .withDataContentType("application/json")
                .withId(UUID.randomUUID().toString())
                .withSubject("verifyMergeFromValidCeOverrides")
                .withTime(OffsetDateTime.now())
                .withExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, "12345")
                .withData("{\"mykey\": \"myvalue\"}".getBytes(StandardCharsets.UTF_8))
                .build();
        final String ceMarshalled = decoratorSpy.decorate(mapper.writeValueAsString(ce));
        assertThat(ceMarshalled).isNotNull();
        final CloudEvent ceOverride = mapper.readValue(ceMarshalled, CloudEvent.class);
        assertThat(ceOverride).isNotNull();
        assertThat(ceOverride.getExtension(CloudEventExtensionConstants.ADDONS)).isEqualTo("knative-eventing");
        assertThat(ceOverride.getExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID)).isEqualTo("12345");
        assertThat(ceOverride.getExtensionNames()).hasSize(2);
    }

    @Test
    public void verifyMergeFromExistingExtension() throws JsonProcessingException, URISyntaxException {
        final ObjectMapper mapper = CloudEventUtils.Mapper.mapper();

        KnativeEventingMessagePayloadDecorator decorator = new KnativeEventingMessagePayloadDecorator();
        KnativeEventingMessagePayloadDecorator decoratorSpy = Mockito.spy(decorator);

        Mockito.when(decoratorSpy.readEnvCeOverrides()).thenReturn("{ \"extensions\": { \"" + CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID + "\": \"54321\"} }");
        final CloudEvent ce = new CloudEventBuilder()
                .withType("unitTest")
                .withSource(new URI("http://localhost"))
                .withDataContentType("application/json")
                .withId(UUID.randomUUID().toString())
                .withSubject("verifyMergeFromExistingExtension")
                .withTime(OffsetDateTime.now())
                .withExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, "12345")
                .withData("{\"mykey\": \"myvalue\"}".getBytes(StandardCharsets.UTF_8))
                .build();
        final String ceMarshalled = decoratorSpy.decorate(mapper.writeValueAsString(ce));
        assertThat(ceMarshalled).isNotNull();
        final CloudEvent ceOverride = mapper.readValue(ceMarshalled, CloudEvent.class);
        assertThat(ceOverride).isNotNull();
        assertThat(ceOverride.getExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID)).isEqualTo("54321");
        assertThat(ceOverride.getExtensionNames()).hasSize(1);
    }

    @Test
    public void verifyKnativeEventingDecoratorLoader() {
        final MessagePayloadDecoratorProvider provider = MessagePayloadDecoratorProvider.getInstance();
        boolean found = false;
        for (MessagePayloadDecorator decorator : provider.getPayloadDecorators()) {
            if (decorator instanceof KnativeEventingMessagePayloadDecorator) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void verifyEmptyCeOverrides() throws URISyntaxException, JsonProcessingException {
        final ObjectMapper mapper = CloudEventUtils.Mapper.mapper();

        KnativeEventingMessagePayloadDecorator decorator = new KnativeEventingMessagePayloadDecorator();
        KnativeEventingMessagePayloadDecorator decoratorSpy = Mockito.spy(decorator);

        Mockito.when(decoratorSpy.readEnvCeOverrides()).thenReturn("");
        final CloudEvent ce = new CloudEventBuilder()
                .withType("unitTest")
                .withSource(new URI("http://localhost"))
                .withDataContentType("application/json")
                .withId(UUID.randomUUID().toString())
                .withSubject("verifyMergeFromExistingExtension")
                .withTime(OffsetDateTime.now())
                .withExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, "12345")
                .withData("{\"mykey\": \"myvalue\"}".getBytes(StandardCharsets.UTF_8))
                .build();
        final String ceMarshalled = decoratorSpy.decorate(mapper.writeValueAsString(ce));
        assertThat(ceMarshalled).isNotNull();
        final CloudEvent ceOverride = mapper.readValue(ceMarshalled, CloudEvent.class);
        assertThat(ceOverride).isNotNull();
        assertThat(ceOverride.getExtensionNames()).hasSize(1);
    }

    @Test
    public void verifyInvalidCeOverrides() throws URISyntaxException, JsonProcessingException {
        final ObjectMapper mapper = CloudEventUtils.Mapper.mapper();

        KnativeEventingMessagePayloadDecorator decorator = new KnativeEventingMessagePayloadDecorator();
        KnativeEventingMessagePayloadDecorator decoratorSpy = Mockito.spy(decorator);

        Mockito.when(decoratorSpy.readEnvCeOverrides()).thenReturn("{ \"" + CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID + "\": \"54321\"}");
        final CloudEvent ce = new CloudEventBuilder()
                .withType("unitTest")
                .withSource(new URI("http://localhost"))
                .withDataContentType("application/json")
                .withId(UUID.randomUUID().toString())
                .withSubject("verifyMergeFromExistingExtension")
                .withTime(OffsetDateTime.now())
                .withExtension(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID, "12345")
                .withData("{\"mykey\": \"myvalue\"}".getBytes(StandardCharsets.UTF_8))
                .build();
        final String ceMarshalled = decoratorSpy.decorate(mapper.writeValueAsString(ce));
        assertThat(ceMarshalled).isNotNull();
        final CloudEvent ceOverride = mapper.readValue(ceMarshalled, CloudEvent.class);
        assertThat(ceOverride).isNotNull();
        assertThat(ceOverride.getExtensionNames()).hasSize(1);
    }
}
