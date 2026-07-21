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
package org.kie.kogito.integrationtests.springboot;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.kie.kogito.test.springboot.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test to verify that when a transaction rolls back due to persistence failure,
 * no Kafka message is sent. This validates the transactional behavior of the event emitter.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = { KafkaSpringBootTestResource.class, TransactionRollbackMessageTest.ExposedPostgreSqlResource.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionRollbackMessageTest {

    private static final String TX_TEST_END_TOPIC = "tx_test_end";
    private static final String TX_TEST_CONTINUE_TOPIC = "tx_test_continue";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private KafkaTestClient kafkaClient;

    private AtomicInteger messageCount;
    private CountDownLatch messageLatch;

    // Static reference to PostgreSQL container for stopping it during test
    private static KogitoPostgreSqlContainer postgresContainer;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = randomServerPort;
        messageCount = new AtomicInteger(0);
        messageLatch = new CountDownLatch(1);

        // Ensure PostgreSQL is running before each test
        if (postgresContainer != null && !postgresContainer.isRunning()) {
            postgresContainer.start();
        }
    }

    @Test
    @Order(1)
    void testMessageSentWhenTransactionSucceeds() throws Exception {
        kafkaClient.consume(TX_TEST_END_TOPIC, s -> {
            messageCount.incrementAndGet();
            messageLatch.countDown();
        });

        String pId = given().body(String.format("{ \"sleepTime\": 1000 }"))
                .contentType(ContentType.JSON)
                .when()
                .post("/tx_rollback_test")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/tx_rollback_test/" + pId)
                        .then()
                        .statusCode(200));

        String continueMessage = createCloudEventsMessage(pId, "continue");
        kafkaClient.produce(continueMessage, TX_TEST_CONTINUE_TOPIC);

        // Wait for the process to complete and message to be sent
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);

        // In a successful transaction, the message should be sent
        assertThat(messageReceived).as("Message should be sent when transaction succeeds").isTrue();
        assertThat(messageCount.get()).as("Message count should be at least 1").isGreaterThanOrEqualTo(1);
    }

    @Test
    @Order(2)
    void testNoMessageSentWhenTransactionRollsBack() throws Exception {
        kafkaClient.consume(TX_TEST_END_TOPIC, s -> {
            System.out.println("Received end message: " + s);
            messageCount.incrementAndGet();
            messageLatch.countDown();
        });

        String pId = given().body(String.format("{ \"sleepTime\": 5000 }"))
                .contentType(ContentType.JSON)
                .when()
                .post("/tx_rollback_test")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/tx_rollback_test/" + pId)
                        .then()
                        .statusCode(200));

        String continueMessage = createCloudEventsMessage(pId, "continue");
        kafkaClient.produce(continueMessage, TX_TEST_CONTINUE_TOPIC);

        // Wait for the message to be consumed and sleep to start
        Thread.sleep(1000);

        // Stop PostgreSQL during the sleep to simulate database failure
        if (postgresContainer != null) {
            postgresContainer.stop();
        } else {
            System.out.println("WARNING: PostgreSQL container reference is null, cannot simulate failure");
        }

        // Verify that NO message was sent to the tx_test_end topic
        // because the transaction should have rolled back
        boolean messageReceived = messageLatch.await(5, TimeUnit.SECONDS);

        // In a successful rollback scenario, no message should be received
        assertThat(messageReceived).as("No message should be sent when transaction rolls back").isFalse();
        assertThat(messageCount.get()).as("Message count should be 0").isEqualTo(0);
    }

    private String createCloudEventsMessage(String processInstanceId, String messageContent) throws Exception {
        Map<String, Object> cloudEvent = new HashMap<>();
        cloudEvent.put("specversion", "1.0");
        cloudEvent.put("id", UUID.randomUUID().toString());
        cloudEvent.put("source", "test");
        cloudEvent.put("type", "tx_test_continue");
        cloudEvent.put("data", messageContent);
        cloudEvent.put("kogitoprocrefid", processInstanceId);

        return MAPPER.writeValueAsString(cloudEvent);
    }

    /**
     * Custom PostgreSQL test resource that exposes the container instance
     * so tests can stop it to simulate database failure.
     */
    public static class ExposedPostgreSqlResource extends PostgreSqlSpringBootTestResource
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            // Call parent to start the container and configure properties
            super.initialize(applicationContext);
            // Store reference to the container for test access
            postgresContainer = getTestResource();
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            // Call parent to stop the container
            super.onApplicationEvent(event);
            postgresContainer = null;
        }
    }
}
