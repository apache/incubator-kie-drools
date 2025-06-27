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
package org.kie.kogito.index.service.graphql;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.protobuf.ProtobufService;

import io.restassured.http.ContentType;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.http.WebsocketVersion;
import io.vertx.core.json.JsonObject;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.COMPLETE;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.CONNECTION_ACK;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.CONNECTION_INIT;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.CONNECTION_KEEP_ALIVE;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.CONNECTION_TERMINATE;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.DATA;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.START;
import static java.lang.String.format;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.CoreMatchers.isA;
import static org.kie.kogito.index.test.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.test.TestUtils.getUserTaskCloudEvent;

public abstract class AbstractWebSocketSubscriptionIT {

    @Inject
    public ProtobufService protobufService;

    @Inject
    public Vertx vertx;

    @Inject
    public DataIndexStorageService cacheService;

    private AtomicInteger counter = new AtomicInteger(0);

    private HttpClient httpClient;

    @BeforeEach
    void setup() {
        httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(TestUtils.getPortFromConfig()));
    }

    @AfterEach
    void tearDown() {
        httpClient.close();
        cacheService.getJobsStorage().clear();
        cacheService.getProcessDefinitionStorage().clear();
        cacheService.getProcessInstanceStorage().clear();
        cacheService.getUserTaskInstanceStorage().clear();
        if (cacheService.getDomainModelCache("travels") != null) {
            cacheService.getDomainModelCache("travels").clear();
        }
        if (cacheService.getDomainModelCache("deals") != null) {
            cacheService.getDomainModelCache("deals").clear();
        }
    }

    @Test
    void testProcessInstanceSubscription() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getProcessProtobufFileContent());

        assertProcessInstanceSubscription(processId, processInstanceId, ProcessInstanceState.ACTIVE, "subscription { ProcessInstanceAdded { id, processId, state } }", "ProcessInstanceAdded");
        assertProcessInstanceSubscription(processId, processInstanceId, ProcessInstanceState.COMPLETED, "subscription { ProcessInstanceUpdated { id, processId, state } }", "ProcessInstanceUpdated");
    }

    @Test
    void testUserTaskInstanceSubscription() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getUserTaskProtobufFileContent());

        assertUserTaskInstanceSubscription(taskId, processId, processInstanceId, "InProgress", "subscription { UserTaskInstanceAdded { id, processInstanceId, processId, state } }",
                "UserTaskInstanceAdded");
        assertUserTaskInstanceSubscription(taskId, processId, processInstanceId, "Completed", "subscription { UserTaskInstanceUpdated { id, processInstanceId, processId, state } }",
                "UserTaskInstanceUpdated");
    }

    @Test
    void testJobSubscription() throws Exception {
        String jobId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        assertJobSubscription(jobId, processId, processInstanceId, "SCHEDULED", "subscription { JobAdded { id, processInstanceId, processId, status } }", "JobAdded");
        assertJobSubscription(jobId, processId, processInstanceId, "EXECUTED", "subscription { JobUpdated { id, processInstanceId, processId, status } }", "JobUpdated");
    }

    @Test
    void testDomainSubscription() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getProcessProtobufFileContent());

        assertDomainSubscription(processId, processInstanceId, ProcessInstanceState.ACTIVE, "subscription { TravelsAdded { id, traveller { firstName }, metadata { processInstances { state } } } }",
                "TravelsAdded");
        assertDomainSubscription(processId, processInstanceId, ProcessInstanceState.COMPLETED,
                "subscription { TravelsUpdated { id, traveller { firstName }, metadata { processInstances { state } } } }", "TravelsUpdated");
    }

    private void assertDomainSubscription(String processId, String processInstanceId, ProcessInstanceState state, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        ProcessInstanceDataEvent<?> event = getProcessCloudEvent(processId, processInstanceId, state, null, null, null, "currentUser");

        indexProcessCloudEvent(event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".metadata.processInstances[0].state").isEqualTo(state.name()));
    }

    private void assertProcessInstanceSubscription(String processId, String processInstanceId, ProcessInstanceState state, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        ProcessInstanceDataEvent<?> event = getProcessCloudEvent(processId, processInstanceId, state, null, null, null, "currentUser");

        indexProcessCloudEvent(event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".processId").isEqualTo(processId),
                a -> a.node("payload.data." + subscriptionName + ".state").isEqualTo(state.name()));
    }

    private void assertUserTaskInstanceSubscription(String taskId, String processId, String processInstanceId, String state, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Deals{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Deals", isA(Collection.class));

        UserTaskInstanceDataEvent<?> event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        indexUserTaskCloudEvent(event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(taskId),
                a -> a.node("payload.data." + subscriptionName + ".processInstanceId").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".processId").isEqualTo(processId),
                a -> a.node("payload.data." + subscriptionName + ".state").isEqualTo(state));
    }

    private void assertJobSubscription(String taskId, String processId, String processInstanceId, String status, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Jobs{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Jobs", isA(Collection.class));

        KogitoJobCloudEvent event = getJobCloudEvent(taskId, processId, processInstanceId, null, null, status);
        indexJobCloudEvent(event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(taskId),
                a -> a.node("payload.data." + subscriptionName + ".processInstanceId").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".processId").isEqualTo(processId),
                a -> a.node("payload.data." + subscriptionName + ".status").isEqualTo(status));
    }

    private CompletableFuture<JsonObject> subscribe(String subscription) throws Exception {
        CompletableFuture<JsonObject> cf = new CompletableFuture<>();
        CompletableFuture<Void> wsFuture = new CompletableFuture<>();
        JsonObject terminate = new JsonObject().put("type", CONNECTION_TERMINATE.getText());
        WebSocketConnectOptions options = new WebSocketConnectOptions().setURI("/graphql").setVersion(WebsocketVersion.V08);
        httpClient.webSocket(options, websocketRes -> {
            if (websocketRes.succeeded()) {
                WebSocket webSocket = websocketRes.result();
                webSocket.handler(message -> {
                    JsonObject json = message.toJsonObject();
                    String type = json.getString("type");
                    if (COMPLETE.getText().equals(type)) {
                        webSocket.write(terminate.toBuffer());
                        cf.complete(null);
                    } else if (DATA.getText().equals(type)) {
                        webSocket.write(terminate.toBuffer());
                        cf.complete(message.toJsonObject());
                    } else if (CONNECTION_ACK.getText().equals(type)) {
                        JsonObject init = new JsonObject()
                                .put("id", String.valueOf(counter.getAndIncrement()))
                                .put("type", START.getText())
                                .put("payload", new JsonObject().put("query", subscription));
                        webSocket.write(init.toBuffer()).onSuccess(v -> wsFuture.complete(null));
                    } else if (CONNECTION_KEEP_ALIVE.getText().equals(type)) {
                        //Ignore
                    } else {
                        webSocket.write(terminate.toBuffer());
                        cf.completeExceptionally(new RuntimeException(format("Unexpected message type: %s\nMessage: %s", type, message.toString())));
                    }
                });

                JsonObject init = new JsonObject().put("type", CONNECTION_INIT.getText());
                webSocket.write(init.toBuffer());
            } else {
                websocketRes.cause().printStackTrace();
                wsFuture.completeExceptionally(websocketRes.cause());
            }
        });
        wsFuture.get(1, TimeUnit.MINUTES);
        return cf;
    }

    protected abstract void indexProcessCloudEvent(ProcessInstanceDataEvent<?> event);

    protected abstract void indexUserTaskCloudEvent(UserTaskInstanceDataEvent<?> event);

    protected abstract void indexJobCloudEvent(KogitoJobCloudEvent event);

    protected abstract String getProcessProtobufFileContent() throws Exception;

    protected abstract String getUserTaskProtobufFileContent() throws Exception;

}
