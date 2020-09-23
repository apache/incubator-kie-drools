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
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing our use cases on a controlled environment instead of using generated code.
 */
@QuarkusTest
class CloudEventListenerResourceTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void verifyHttpRequestUnsupportedMediaType() {
        given().when()
                .header("ce-type", "myevent")
                .header("ce-source", "/from/unit/test")
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-kogitoReferenceId", "12345")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .post("/")
                .then()
                .statusCode(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
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
                .body(Matchers.equalTo("{ \"message\": \"Hola Mundo!\" }"))
                .header("ce-kogitoreferenceid", "12345");
    }

    @Test
    void verifyHttpRequestWithJSONPayloadExpectsPOJO() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";

        Message msg = given()
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
                .body(Matchers.equalTo("{ \"message\": \"Hola Mundo!\" }"))
                .header("ce-source", source)
                .extract().as(Message.class);
        assertThat(msg.getMessage()).isEqualTo("Hola Mundo!");
    }

    @Test
    void verifyHttpRequestWithCEPayloadExpectsPOJO() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";

        final Message msg = given().when()
                .body("{\"kogitoReferenceId\":\"12345!\", \"data\":{\"message\":\"Hi World!\"},\"id\":\"x10\",\"source\":\"/from/unit/test\",\"specversion\":\"1.0\",\"type\":\"myevent\",\"datacontenttype\":\"application/json\"}")
                .contentType(MediaType.valueOf(JsonFormat.CONTENT_TYPE).withCharset(StandardCharsets.UTF_8.name()).toString())
                .post("/")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .header("ce-source", source)
                .header("ce-kogitoReferenceId", "12345!")
                .extract().body().as(Message.class);


    }

    @Test
    void verifyHttpRequestWithCEPayloadExpectsString() throws URISyntaxException, IOException {
        final String source = "/from/unit/test";
        final Message msg = given().when()
                .body("{\"data\":{\"message\":\"Hi World!\"},\"id\":\"x10\",\"source\":\"/from/unit/test\",\"specversion\":\"1.0\",\"type\":\"myevent\",\"datacontenttype\":\"application/json\"}")
                .contentType(JsonFormat.CONTENT_TYPE)
                .post("")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(Matchers.isA(String.class))
                .header("ce-source", source)
                .extract().body().as(Message.class);

        assertThat(msg).isNotNull();
        assertThat(msg.getMessage()).isEqualTo("Hi World!");
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