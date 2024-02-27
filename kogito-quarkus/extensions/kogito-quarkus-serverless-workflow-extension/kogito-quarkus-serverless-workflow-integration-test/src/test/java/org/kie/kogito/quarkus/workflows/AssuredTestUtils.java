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
package org.kie.kogito.quarkus.workflows;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

class AssuredTestUtils {

    private AssuredTestUtils() {
    }

    static String startProcess(String flowName) {
        return startProcess(flowName, Optional.empty());
    }

    static String startProcess(String flowName, Optional<String> businessKey) {
        String id = startProcessNoCheck(flowName, businessKey);
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/" + flowName + "/{id}", id)
                .then()
                .statusCode(200);
        return id;
    }

    static String startProcessNoCheck(String flowName) {
        return startProcessNoCheck(flowName, Optional.empty());
    }

    static String startProcessNoCheck(String flowName, Optional<String> businessKey) {
        RequestSpecification body = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", Collections.emptyMap()));
        businessKey.ifPresent(key -> body.queryParam("businessKey", key));
        return body.post("/" + flowName)
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    static void waitForFinish(String flowName, String id, Duration duration) {
        await("dead").atMost(duration)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/" + flowName + "/{id}", id)
                        .then()
                        .statusCode(404));
    }

    static CloudEvent buildCloudEvent(String id, Optional<String> businessKey, String type, CloudEventMarshaller<byte[]> marshaller) {
        io.cloudevents.core.v1.CloudEventBuilder builder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(type)
                .withTime(OffsetDateTime.now())
                .withData(marshaller.cloudEventDataFactory().apply(Collections.singletonMap(type, "This has been injected by the event")));
        businessKey.ifPresentOrElse(key -> builder.withExtension(CloudEventExtensionConstants.BUSINESS_KEY, key), () -> builder.withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, id));
        return builder.build();
    }

    static CloudEvent buildCloudEvent(String id, String type, CloudEventMarshaller<byte[]> marshaller) {
        return buildCloudEvent(id, Optional.empty(), type, marshaller);
    }

}
