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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.impl.ByteArrayCloudEventMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.OpenTelemetryTestUtils.filterWorkflowSpans;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.extractTraceIds;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.filterByProcessInstanceId;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.validateFlatSpanHierarchy;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.validateLogMessagesInSpanEvents;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.validateNewTraceAfterRestart;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.RestartSpanValidator.validateOneSpanPerState;

@Testcontainers
@Disabled
public class OpenTelemetryRestartIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTelemetryRestartIT.class);
    private static final String POSTGRES_NETWORK_ALIAS = "postgres";
    private static final String POSTGRES_USER = "testuser";
    private static final String POSTGRES_PASSWORD = "testpass";
    private static final String POSTGRES_DB = "testdb";

    private static OtlpMockCollector otlpCollector;
    private static PostgreSQLContainer<?> postgres;
    private static QuarkusApplicationContainer quarkusApp;
    private static Network network;
    private static ObjectMapper objectMapper;
    private static CloudEventMarshaller<byte[]> cloudEventMarshaller;

    @BeforeAll
    static void setup() {
        LOGGER.info("=== Setting up OpenTelemetry Restart Integration Test ===");

        // Check Docker availability before proceeding
        boolean dockerAvailable = isDockerAvailable();
        assumeTrue(dockerAvailable, "Docker is not available or has an incompatible API version. Skipping test.");

        network = Network.newNetwork();
        LOGGER.info("Created Docker network: {}", network.getId());

        postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withNetwork(network)
                .withNetworkAliases(POSTGRES_NETWORK_ALIAS)
                .withDatabaseName(POSTGRES_DB)
                .withUsername(POSTGRES_USER)
                .withPassword(POSTGRES_PASSWORD);
        postgres.start();
        LOGGER.info("Started PostgreSQL container at {}:{}", POSTGRES_NETWORK_ALIAS, PostgreSQLContainer.POSTGRESQL_PORT);

        otlpCollector = new OtlpMockCollector();
        otlpCollector.start();
        LOGGER.info("Started OTLP Mock Collector at {}", otlpCollector.getTracesEndpoint());

        objectMapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
        cloudEventMarshaller = new ByteArrayCloudEventMarshaller(objectMapper);

        startQuarkusApp();
        LOGGER.info("=== Setup Complete ===");
    }

    @AfterAll
    static void teardown() {
        LOGGER.info("=== Tearing down test environment ===");
        if (quarkusApp != null && quarkusApp.isRunning()) {
            quarkusApp.stop();
            LOGGER.info("Stopped Quarkus application");
        }
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
            LOGGER.info("Stopped PostgreSQL container");
        }
        if (otlpCollector != null && otlpCollector.isRunning()) {
            otlpCollector.stop();
            LOGGER.info("Stopped OTLP Mock Collector");
        }
        if (network != null) {
            network.close();
            LOGGER.info("Closed Docker network");
        }
        LOGGER.info("=== Teardown Complete ===");
    }

    private static void startQuarkusApp() {
        String otlpEndpoint = String.format("http://host.docker.internal:%d", otlpCollector.getPort());
        String postgresJdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
                POSTGRES_NETWORK_ALIAS, PostgreSQLContainer.POSTGRESQL_PORT, POSTGRES_DB);

        LOGGER.info("Creating Quarkus application container with:");
        LOGGER.info("  OTLP endpoint: {}", otlpEndpoint);
        LOGGER.info("  PostgreSQL JDBC URL: {}", postgresJdbcUrl);

        quarkusApp = new QuarkusApplicationContainer(
                otlpEndpoint,
                postgresJdbcUrl,
                POSTGRES_USER,
                POSTGRES_PASSWORD);

        quarkusApp.withNetwork(network);
        quarkusApp.start();

        String appUrl = quarkusApp.getApplicationUrl();
        LOGGER.info("Quarkus application started at: {}", appUrl);
        RestAssured.baseURI = appUrl;
    }

    private static void restartQuarkusApp() {
        LOGGER.info("=== Restarting Quarkus Application ===");
        quarkusApp.stop();
        LOGGER.info("Stopped Quarkus application");

        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> !quarkusApp.isRunning());

        startQuarkusApp();
        LOGGER.info("=== Application Restart Complete ===");
    }

    @Test
    void testCompleteScenarioWithRestarts() throws Exception {
        LOGGER.info("=== Starting Complete Scenario Test with 4 Scenarios ===");

        scenario1_BasicWorkflowWithEvents();
        scenario2_RestartAfterSubflowStart();
        scenario3_NewWorkflowAfterRestart();
        scenario4_RestartBetweenEvents();

        LOGGER.info("=== All 4 Scenarios Completed Successfully ===");
    }

    private void scenario1_BasicWorkflowWithEvents() throws Exception {
        LOGGER.info("\n=== SCENARIO 1: Basic workflow with events ===");

        otlpCollector.clearRequests();
        LOGGER.info("Cleared OTLP collector spans");

        String processInstanceId = startWorkflow("complexGreet", Map.of("name", "TestUser", "language", "English"));
        LOGGER.info("Started workflow complexGreet with process instance ID: {}", processInstanceId);

        LOGGER.info("Waiting 10 seconds for subflow to reach WaitForLoudEvent state...");
        Thread.sleep(10000);

        LOGGER.info("Sending /loud event");
        sendCloudEvent("loud", processInstanceId);

        LOGGER.info("Sending /quiet event");
        sendCloudEvent("quiet", processInstanceId);

        LOGGER.info("Waiting for workflow completion...");
        waitForWorkflowCompletion("complexGreet", processInstanceId, Duration.ofSeconds(30));
        LOGGER.info("Workflow completed successfully");

        LOGGER.info("Validating spans...");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> allSpans = getWorkflowSpans();
            List<SpanData> workflowSpans = filterByProcessInstanceId(allSpans, processInstanceId);

            LOGGER.info("Found {} workflow spans for process instance {}", workflowSpans.size(), processInstanceId);
            assertThat(workflowSpans)
                    .as("Scenario 1: Should have at least 7 spans (one per state)")
                    .hasSizeGreaterThanOrEqualTo(7);

            validateFlatSpanHierarchy(workflowSpans);
            LOGGER.info("Validated flat hierarchy");

            List<String> expectedStates = List.of(
                    "GreetInEnglish", "ExecuteSubflow", "WaitForQuietEvent", "GreetPerson",
                    "GreetPersonSubflow", "WaitForLoudEvent", "GreetPersonAfterWait");
            validateOneSpanPerState(workflowSpans, expectedStates);
            LOGGER.info("Validated one span per state");

            List<String> expectedLogMessages = List.of(
                    "Subflow greeting",
                    "Loud event received in subflow",
                    "Subflow completed after event",
                    "Quiet event received",
                    "TestUser");
            validateLogMessagesInSpanEvents(workflowSpans, expectedLogMessages);
            LOGGER.info("Scenario 1: Log messages validated in span events");
        });

        LOGGER.info("=== SCENARIO 1: PASSED ===");
    }

    private void scenario2_RestartAfterSubflowStart() throws Exception {
        LOGGER.info("\n=== SCENARIO 2: Restart after subflow start ===");

        otlpCollector.clearRequests();
        LOGGER.info("Cleared OTLP collector spans");

        String processInstanceId = startWorkflow("complexGreet", Map.of("name", "RestartUser", "language", "English"));
        LOGGER.info("Started workflow complexGreet with process instance ID: {}", processInstanceId);

        LOGGER.info("Waiting 10 seconds for subflow to reach WaitForLoudEvent state...");
        Thread.sleep(10000);

        LOGGER.info("Recording pre-restart trace IDs...");
        List<SpanData> preRestartSpans = filterByProcessInstanceId(getWorkflowSpans(), processInstanceId);
        Set<String> preRestartTraceIds = extractTraceIds(preRestartSpans);
        LOGGER.info("Pre-restart trace IDs: {}", preRestartTraceIds);
        String preRestartTraceId = preRestartTraceIds.iterator().next();

        restartQuarkusApp();

        LOGGER.info("Sending /loud event after restart");
        sendCloudEvent("loud", processInstanceId);

        LOGGER.info("Sending /quiet event");
        sendCloudEvent("quiet", processInstanceId);

        LOGGER.info("Waiting for workflow completion...");
        waitForWorkflowCompletion("complexGreet", processInstanceId, Duration.ofSeconds(30));
        LOGGER.info("Workflow completed successfully after restart");

        LOGGER.info("Validating new trace ID after restart...");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> allSpans = getWorkflowSpans();
            List<SpanData> postRestartSpans = allSpans.stream()
                    .filter(span -> !preRestartTraceIds.contains(span.getTraceId()))
                    .filter(span -> span.getAttributes().get(SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID) != null)
                    .filter(span -> span.getAttributes().get(SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID).equals(processInstanceId))
                    .toList();

            LOGGER.info("Found {} post-restart spans", postRestartSpans.size());
            assertThat(postRestartSpans)
                    .as("Scenario 2: Should have spans after restart")
                    .isNotEmpty();

            validateNewTraceAfterRestart(preRestartTraceId, postRestartSpans);
            LOGGER.info("Validated new trace ID after restart");

            List<String> expectedLogMessages = List.of(
                    "Loud event received in subflow",
                    "Subflow completed after event",
                    "Quiet event received",
                    "RestartUser");
            validateLogMessagesInSpanEvents(postRestartSpans, expectedLogMessages);
            LOGGER.info("Scenario 2: Log messages validated in span events");
        });

        LOGGER.info("=== SCENARIO 2: PASSED ===");
    }

    private void scenario3_NewWorkflowAfterRestart() throws Exception {
        LOGGER.info("\n=== SCENARIO 3: New workflow after restart ===");

        otlpCollector.clearRequests();
        LOGGER.info("Cleared OTLP collector spans");

        String processInstanceId = startWorkflow("complexGreet", Map.of("name", "NewWorkflow", "language", "English"));
        LOGGER.info("Started workflow complexGreet with process instance ID: {}", processInstanceId);

        LOGGER.info("Waiting 10 seconds for subflow to reach WaitForLoudEvent state...");
        Thread.sleep(10000);

        LOGGER.info("Sending /loud event with 10 second timeout");
        given()
                .contentType(ContentType.JSON)
                .body(buildCloudEvent("loud", processInstanceId))
                .when()
                .post("/loud")
                .then()
                .statusCode(202);

        LOGGER.info("Sending /quiet event");
        sendCloudEvent("quiet", processInstanceId);

        LOGGER.info("Waiting for workflow completion...");
        waitForWorkflowCompletion("complexGreet", processInstanceId, Duration.ofSeconds(30));
        LOGGER.info("Workflow completed successfully");

        LOGGER.info("Validating spans...");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> allSpans = getWorkflowSpans();
            List<SpanData> workflowSpans = filterByProcessInstanceId(allSpans, processInstanceId);

            LOGGER.info("Found {} workflow spans", workflowSpans.size());
            assertThat(workflowSpans)
                    .as("Scenario 3: Should have at least 7 spans")
                    .hasSizeGreaterThanOrEqualTo(7);

            validateFlatSpanHierarchy(workflowSpans);
            LOGGER.info("Validated flat hierarchy");

            List<String> expectedLogMessages = List.of(
                    "Subflow greeting",
                    "Loud event received in subflow",
                    "Subflow completed after event",
                    "Quiet event received",
                    "NewWorkflow");
            validateLogMessagesInSpanEvents(workflowSpans, expectedLogMessages);
            LOGGER.info("Scenario 3: Log messages validated in span events");
        });

        LOGGER.info("=== SCENARIO 3: PASSED ===");
    }

    private void scenario4_RestartBetweenEvents() throws Exception {
        LOGGER.info("\n=== SCENARIO 4: Restart between events ===");

        otlpCollector.clearRequests();
        LOGGER.info("Cleared OTLP collector spans");

        String processInstanceId = startWorkflow("complexGreet", Map.of("name", "BetweenEvents", "language", "English"));
        LOGGER.info("Started workflow complexGreet with process instance ID: {}", processInstanceId);

        LOGGER.info("Waiting 10 seconds for subflow to reach WaitForLoudEvent state...");
        Thread.sleep(10000);

        LOGGER.info("Sending /loud event");
        sendCloudEvent("loud", processInstanceId);

        LOGGER.info("Waiting for spans to appear...");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> spans = getWorkflowSpans();
            assertThat(spans).isNotEmpty();
        });

        LOGGER.info("Recording pre-restart trace ID...");
        List<SpanData> preRestartSpans = filterByProcessInstanceId(getWorkflowSpans(), processInstanceId);
        Set<String> preRestartTraceIds = extractTraceIds(preRestartSpans);
        LOGGER.info("Pre-restart trace IDs: {}", preRestartTraceIds);
        String preRestartTraceId = preRestartTraceIds.iterator().next();

        restartQuarkusApp();

        LOGGER.info("Sending /quiet event after restart");
        sendCloudEvent("quiet", processInstanceId);

        LOGGER.info("Waiting for workflow completion...");
        waitForWorkflowCompletion("complexGreet", processInstanceId, Duration.ofSeconds(30));
        LOGGER.info("Workflow completed successfully after restart");

        LOGGER.info("Validating new trace ID after restart...");
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            List<SpanData> allSpans = getWorkflowSpans();
            List<SpanData> postRestartSpans = allSpans.stream()
                    .filter(span -> !preRestartTraceIds.contains(span.getTraceId()))
                    .filter(span -> span.getAttributes().get(SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID) != null)
                    .filter(span -> span.getAttributes().get(SonataFlowOtelAttributes.SONATAFLOW_PROCESS_INSTANCE_ID).equals(processInstanceId))
                    .toList();

            LOGGER.info("Found {} post-restart spans", postRestartSpans.size());
            assertThat(postRestartSpans)
                    .as("Scenario 4: Should have spans after restart")
                    .isNotEmpty();

            validateNewTraceAfterRestart(preRestartTraceId, postRestartSpans);
            LOGGER.info("Validated new trace ID after restart");

            List<String> expectedLogMessages = List.of(
                    "Quiet event received",
                    "BetweenEvents");
            validateLogMessagesInSpanEvents(postRestartSpans, expectedLogMessages);
            LOGGER.info("Scenario 4: Log messages validated in span events");
        });

        LOGGER.info("=== SCENARIO 4: PASSED ===");
    }

    private String startWorkflow(String workflowName, Map<String, Object> input) throws IOException {
        LOGGER.debug("Starting workflow: {} with input: {}", workflowName, input);
        String jsonInput = objectMapper.writeValueAsString(input);

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .body(jsonInput)
                .when()
                .post("/" + workflowName)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        LOGGER.debug("Workflow {} started with process instance ID: {}", workflowName, processInstanceId);
        return processInstanceId;
    }

    private void sendCloudEvent(String eventType, String processInstanceId) throws IOException {
        LOGGER.debug("Sending CloudEvent: type={}, processInstanceId={}", eventType, processInstanceId);

        CloudEvent cloudEvent = buildCloudEvent(eventType, processInstanceId);

        byte[] marshalledEvent = cloudEventMarshaller.marshall(cloudEvent);

        given()
                .contentType(ContentType.JSON)
                .body(marshalledEvent)
                .when()
                .post("/" + eventType)
                .then()
                .statusCode(202);

        LOGGER.debug("CloudEvent {} sent successfully", eventType);
    }

    private CloudEvent buildCloudEvent(String eventType, String processInstanceId) {
        Map<String, Object> data = new HashMap<>();
        data.put(eventType, "Event data for " + eventType);

        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(eventType)
                .withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, processInstanceId)
                .withData(cloudEventMarshaller.cloudEventDataFactory().apply(data))
                .build();
    }

    private void waitForWorkflowCompletion(String workflowName, String processInstanceId, Duration timeout) {
        LOGGER.debug("Waiting for workflow {} with ID {} to complete (timeout: {})",
                workflowName, processInstanceId, timeout);

        await()
                .atMost(timeout)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/" + workflowName + "/{id}", processInstanceId)
                        .then()
                        .statusCode(404));

        LOGGER.debug("Workflow {} with ID {} has completed", workflowName, processInstanceId);
    }

    private List<SpanData> getWorkflowSpans() {
        List<SpanData> allSpans = OtlpDataParser.extractSpansFromTraceRequests(
                otlpCollector.getReceivedTracesRequests());
        return filterWorkflowSpans(allSpans);
    }

    private static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            LOGGER.info("Docker is available");
            return true;
        } catch (Exception e) {
            LOGGER.warn("Docker is not available: {}", e.getMessage());
            return false;
        }
    }
}
