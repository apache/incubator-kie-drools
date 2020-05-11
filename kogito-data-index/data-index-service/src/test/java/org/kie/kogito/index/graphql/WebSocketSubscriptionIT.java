/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.graphql.ApolloWSMessageType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.InfinispanServerTestResource;
import org.kie.kogito.index.TestUtils;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.infinispan.protostream.ProtobufService;
import org.kie.kogito.index.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.index.model.ProcessInstanceState;

import static io.restassured.RestAssured.given;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.COMPLETE;
import static io.vertx.ext.web.handler.graphql.ApolloWSMessageType.DATA;
import static java.lang.String.format;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.CoreMatchers.isA;
import static org.kie.kogito.index.TestUtils.getDealsProtoBufferFile;
import static org.kie.kogito.index.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getTravelsProtoBufferFile;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class WebSocketSubscriptionIT {

    @Inject
    ReactiveMessagingEventConsumer consumer;

    @Inject
    ProtobufService protobufService;

    @Inject
    MockGraphQLInstrumentation instrumentation;

    @Inject
    Vertx vertx;

    private AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void testProcessInstanceSubscription() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        assertProcessInstanceSubscription(processId, processInstanceId, ProcessInstanceState.ACTIVE, "subscription { ProcessInstanceAdded { id, processId, state } }", "ProcessInstanceAdded");
        assertProcessInstanceSubscription(processId, processInstanceId, ProcessInstanceState.COMPLETED, "subscription { ProcessInstanceUpdated { id, processId, state } }", "ProcessInstanceUpdated");
    }

    @Test
    public void testUserTaskInstanceSubscription() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getDealsProtoBufferFile());

        assertUserTaskInstanceSubscription(taskId, processId, processInstanceId, "InProgress", "subscription { UserTaskInstanceAdded { id, processInstanceId, processId, state } }", "UserTaskInstanceAdded");
        assertUserTaskInstanceSubscription(taskId, processId, processInstanceId, "Completed", "subscription { UserTaskInstanceUpdated { id, processInstanceId, processId, state } }", "UserTaskInstanceUpdated");
    }

    @Test
    public void testJobSubscription() throws Exception {
        String jobId = UUID.randomUUID().toString();
        String processId = "deals";
        String processInstanceId = UUID.randomUUID().toString();

        assertJobSubscription(jobId, processId, processInstanceId, "SCHEDULED", "subscription { JobAdded { id, processInstanceId, processId, status } }", "JobAdded");
        assertJobSubscription(jobId, processId, processInstanceId, "EXECUTED", "subscription { JobUpdated { id, processInstanceId, processId, status } }", "JobUpdated");
    }

    @Test
    public void testDomainSubscription() throws Exception {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        assertDomainSubscription(processId, processInstanceId, ProcessInstanceState.ACTIVE, "subscription { TravelsAdded { id, traveller { firstName }, metadata { processInstances { state } } } }", "TravelsAdded");
        assertDomainSubscription(processId, processInstanceId, ProcessInstanceState.COMPLETED, "subscription { TravelsUpdated { id, traveller { firstName }, metadata { processInstances { state } } } }", "TravelsUpdated");
    }

    private void assertDomainSubscription(String processId, String processInstanceId, ProcessInstanceState state, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, state, null, null, null);
        consumer.onProcessInstanceDomainEvent(() -> event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".metadata.processInstances[0].state").isEqualTo(state.name()),
                a -> a.node("payload.data." + subscriptionName + ".traveller.firstName").isEqualTo("Maciej"));
    }

    private void assertProcessInstanceSubscription(String processId, String processInstanceId, ProcessInstanceState state, String subscription, String subscriptionName) throws Exception {
        CompletableFuture<JsonObject> cf = subscribe(subscription);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, state, null, null, null);
        consumer.onProcessInstanceEvent(() -> event);

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

        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null, null, state);
        consumer.onUserTaskInstanceEvent(() -> event);

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

        KogitoJobCloudEvent event = getJobCloudEvent(taskId, processId, processInstanceId, null, null, status);
        consumer.onJobEvent(() -> event);

        JsonObject json = cf.get(1, TimeUnit.MINUTES);

        assertThatJson(json.toString()).and(
                a -> a.node("type").isEqualTo("data"),
                a -> a.node("payload.data." + subscriptionName + ".id").isEqualTo(taskId),
                a -> a.node("payload.data." + subscriptionName + ".processInstanceId").isEqualTo(processInstanceId),
                a -> a.node("payload.data." + subscriptionName + ".processId").isEqualTo(processId),
                a -> a.node("payload.data." + subscriptionName + ".status").isEqualTo(status));
    }

    private CompletableFuture<JsonObject> subscribe(String subscription) throws Exception {
        HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultPort(TestUtils.getPortFromConfig()));
        CompletableFuture<JsonObject> cf = new CompletableFuture<>();
        CompletableFuture<Void> wsFuture = new CompletableFuture<>();
        instrumentation.setFuture(wsFuture);
        httpClient.webSocket("/graphql", websocketRes -> {
            if (websocketRes.succeeded()) {
                WebSocket webSocket = websocketRes.result();
                webSocket.handler(message -> {
                    JsonObject json = message.toJsonObject();
                    String type = json.getString("type");
                    if (COMPLETE.getText().equals(type)) {
                        cf.complete(null);
                    } else if (DATA.getText().equals(type)) {
                        cf.complete(message.toJsonObject());
                    } else {
                        cf.completeExceptionally(new RuntimeException(format("Unexpected message type: %s\nMessage: %s", type, message.toString())));
                    }
                });

                JsonObject init = new JsonObject()
                        .put("id", String.valueOf(counter.getAndIncrement()))
                        .put("type", ApolloWSMessageType.START.getText())
                        .put("payload", new JsonObject().put("query", subscription));
                webSocket.write(init.toBuffer());
            } else {
                websocketRes.cause().printStackTrace();
                wsFuture.completeExceptionally(websocketRes.cause());
            }
        });
        cf.whenComplete((r, t) -> httpClient.close());
        wsFuture.get(1, TimeUnit.MINUTES);
        return cf;
    }
}
