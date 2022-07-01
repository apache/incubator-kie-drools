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
package org.kie.kogito.addon.cloudevents;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.process.ProcessDataEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;

import static org.assertj.core.api.Assertions.assertThat;

class CloudEventConverterTest {

    static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(JsonFormat.getCloudEventJacksonModule())
            .registerModule(new JavaTimeModule());

    @Test
    void verifyBasicCloudEventConversion() throws IOException {
        // given
        final String eventId = UUID.randomUUID().toString();
        final URI src = URI.create("/trigger");
        final String eventType = "My.Cloud.Event.Type";
        final String payload = "{\"message\": \"Oi Mundo!\"}";

        // passing in the given attributes
        final CloudEvent cloudEvent =
                CloudEventBuilder.v1()
                        .withType(eventType)
                        .withId(eventId)
                        .withSource(src)
                        .withData(payload.getBytes())
                        .build();

        final JsonNode ceJson = objectMapper.readTree(cloudEvent.getData().toBytes());
        assertThat(ceJson.get("message").asText()).isNotEmpty().isEqualTo("Oi Mundo!");
    }

    @Test
    void verifyDataEventCloudEventConversion() throws IOException {
        // this is a typical HTTP post message
        final String messageJson = "{\n" +
                "  \"specversion\": \"0.3\",\n" +
                "  \"id\": \"21627e26-31eb-43e7-8343-92a696fd96b1\",\n" +
                "  \"source\": \"/process/instance/12345\",\n" +
                "  \"type\": \"process.persons.travellers\",\n" +
                "  \"time\": \"2019-10-01T12:02:23.812262+02:00\",\n" +
                "  \"data\": {\n" +
                "\t\"firstName\" : \"Jan\",\n" +
                "\t\"lastName\" : \"Kowalski\",\n" +
                "\t\"email\" : \"jan.kowalski@example.com\",\n" +
                "\t\"nationality\" : \"Polish\"\n" +
                "\t}\n" +
                "}";
        final PersonDataEvent dataEventJson = objectMapper.readValue(messageJson, PersonDataEvent.class);
        assertThat(dataEventJson.getData().getEmail()).isEqualTo("jan.kowalski@example.com");
        assertThat(dataEventJson).isNotNull();

        final CloudEvent event = objectMapper.readValue(messageJson, CloudEvent.class);
        assertThat(event).isNotNull();
        final Person person = objectMapper.readValue(event.getData().toBytes(), Person.class);
        assertThat(person).isNotNull();
        assertThat(person.getEmail()).isEqualTo("jan.kowalski@example.com");

        final String convertedEvent = objectMapper.writeValueAsString(event);
        assertThat(convertedEvent).contains("jan.kowalski@example.com");
    }

    @Test
    void verifyDataEventWithProcessDataCloudEventConversion() throws IOException {
        // this is a typical HTTP post message
        final String messageJson = "{\n" +
                "  \"" + CloudEventExtensionConstants.PROCESS_REFERENCE_ID + "\": \"12345\",\n" +
                "  \"specversion\": \"0.3\",\n" +
                "  \"id\": \"21627e26-31eb-43e7-8343-92a696fd96b1\",\n" +
                "  \"source\": \"/process/instance/12345\",\n" +
                "  \"type\": \"process.persons.travellers\",\n" +
                "  \"time\": \"2019-10-01T12:02:23.812262+02:00\",\n" +
                "  \"data\": {\n" +
                "\t\"firstName\" : \"Jan\",\n" +
                "\t\"lastName\" : \"Kowalski\",\n" +
                "\t\"email\" : \"jan.kowalski@example.com\",\n" +
                "\t\"nationality\" : \"Polish\"\n" +
                "\t}\n" +
                "}";

        final CloudEvent event = objectMapper.readValue(messageJson.getBytes(), CloudEvent.class);
        assertThat(event).isNotNull();
        assertThat(event.getExtensionNames()).isNotEmpty();

        final String convertedEvent = new String(objectMapper.writeValueAsBytes(event));
        assertThat(convertedEvent)
                .contains("jan.kowalski@example.com")
                .contains(CloudEventExtensionConstants.PROCESS_REFERENCE_ID)
                .contains("12345");
    }

    public static class PersonDataEvent extends ProcessDataEvent<Person> {

        private String kogitoStartFromNode;

        public String getKogitoStartFromNode() {
            return kogitoStartFromNode;
        }

        public void setKogitoStartFromNode(String kogitoStartFromNode) {
            this.kogitoStartFromNode = kogitoStartFromNode;
        }
    }

    public static class Person {

        private String firstName;
        private String lastName;
        private String email;
        private String nationality;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
