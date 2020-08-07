/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.events.knative.ce.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing our use cases on a controlled environment instead of using generated code.
 */
@QuarkusTest
class CloudEventListenerResourceTest {

    private final EventFormat formatter = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

    @Test
    void verifyHttpRequestIsNotACloudEvent() {
        final ResponseError error =
                given().when()
                        .body("Whatever non-sense")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .post("/")
                        .then()
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .extract().response().as(ResponseError.class);
        assertThat(error.getCause()).contains("Failed to parse CloudEvent");
    }

    @Test
    void verifyHttpRequestWithTextPayloadExpectsString() {
        final String source = "/from/unit/test";
        given().when()
                .body("Ciao!")
                .header("ce-type", "myevent")
                .header("ce-source", source)
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-kogitoReferenceId", "12345")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .post("/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(Matchers.isA(String.class))
                .body(Matchers.containsString("kogitoreferenceid"))
                .body(Matchers.containsString("text/plain"));
    }

    @Test
    void verifyHttpRequestWithJSONPayloadExpectsString() {
        final String source = "/from/unit/test";
        given().when()
                .body("{ \"message\": \"Hola Mundo!\" }")
                .header("ce-type", "myevent")
                .header("ce-source", source)
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-kogitoReferenceId", "12345")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .post("/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(Matchers.isA(String.class))
                .body(Matchers.containsString("kogitoreferenceid"));
    }

    @Test
    void verifyHttpRequestWithJSONPayloadExpectsPOJO() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";

        final CloudEvent event =
                formatter.deserialize(given()
                                              .when()
                                              .body("{ \"message\": \"Hola Mundo!\" }")
                                              .header("ce-type", "myevent")
                                              .header("ce-source", source)
                                              .header("ce-specversion", "1.0")
                                              .header("ce-id", UUID.randomUUID().toString())
                                              .header("ce-kogitoReferenceId", "12345")
                                              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                              .post("/")
                                              .then()
                                              .statusCode(Response.Status.OK.getStatusCode())
                                              .body(Matchers.isA(String.class))
                                              .extract().body().asByteArray());

        assertThat(event).isNotNull();
        assertThat(event.getSource()).isEqualTo(new URI(source));
        assertThat(event.getData()).isNotEmpty();
        assertThat(new ObjectMapper().readValue(event.getData(), Message.class).getMessage()).isEqualTo("Hola Mundo!");
    }

    @Test
    void verifyHttpRequestWithCEPayloadExpectsPOJO() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";

        final CloudEvent event = formatter.deserialize(given().when()
                                                               .body("{\"kogitoReferenceId\":\"12345!\", \"data\":{\"message\":\"Hi World!\"},\"id\":\"x10\",\"source\":\"/from/unit/test\",\"specversion\":\"1.0\",\"type\":\"myevent\",\"datacontenttype\":\"application/json\"}")
                                                               .contentType(ExtMediaType.CLOUDEVENTS_JSON_TYPE.withCharset(StandardCharsets.UTF_8.name()).toString())
                                                               .post("/")
                                                               .then()
                                                               .statusCode(Response.Status.OK.getStatusCode())
                                                               .body(Matchers.isA(String.class))
                                                               .extract().body().asByteArray());

        assertThat(event).isNotNull();
        assertThat(event.getSource()).isEqualTo(new URI(source));
        assertThat(event.getData()).isNotEmpty();
        ObjectMapper mapper = new ObjectMapper();
        assertThat(mapper.readValue(event.getData(), Message.class).getMessage()).isEqualTo("Hi World!");
        assertThat(event.getExtensionNames()).isNotEmpty();
        assertThat(event.getExtension("kogitoReferenceId")).isEqualTo("12345!");
    }

    @Test
    void verifyHttpRequestWithCEPayloadExpectsString() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";
        final CloudEvent event = formatter.deserialize(given().when()
                                                               .body("{\"data\":{\"message\":\"Hi World!\"},\"id\":\"x10\",\"source\":\"/from/unit/test\",\"specversion\":\"1.0\",\"type\":\"myevent\",\"datacontenttype\":\"application/json\"}")
                                                               .contentType(ExtMediaType.CLOUDEVENTS_JSON)
                                                               .post("")
                                                               .then()
                                                               .statusCode(Response.Status.OK.getStatusCode())
                                                               .body(Matchers.isA(String.class))
                                                               .extract().body().asByteArray());

        assertThat(event).isNotNull();
        assertThat(event.getSource()).isEqualTo(new URI(source));
        assertThat(event.getData()).isNotEmpty();
        assertThat(new ObjectMapper().readValue(event.getData(), Message.class).getMessage()).isEqualTo("Hi World!");
    }

    public static class Message {

        private String message;

        public Message() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}