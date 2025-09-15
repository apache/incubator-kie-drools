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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import jakarta.ws.rs.core.HttpHeaders;

import static org.kie.kogito.addons.quarkus.token.exchange.OpenApiCustomCredentialProvider.LOG_PREFIX_COMPLETED_TOKEN_EXCHANGE;
import static org.kie.kogito.addons.quarkus.token.exchange.OpenApiCustomCredentialProvider.LOG_PREFIX_FAILED_TOKEN_EXCHANGE;
import static org.kie.kogito.addons.quarkus.token.exchange.OpenApiCustomCredentialProvider.LOG_PREFIX_STARTING_TOKEN_EXCHANGE;
import static org.kie.kogito.addons.quarkus.token.exchange.cache.TokenEvictionHandler.LOG_PREFIX_FAILED_TO_REFRESH_TOKEN;
import static org.kie.kogito.addons.quarkus.token.exchange.cache.TokenEvictionHandler.LOG_PREFIX_REFRESH_COMPLETED;
import static org.kie.kogito.addons.quarkus.token.exchange.cache.TokenEvictionHandler.LOG_PREFIX_TOKEN_REFRESH;
import static org.kie.kogito.addons.quarkus.token.exchange.persistence.TokenDataStoreImpl.LOG_PREFIX_USED_REPOSITORY;
import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.quarkus.workflows.TokenExchangeExternalServicesMock.BASE_AND_PROPAGATED_AUTHORIZATION_TOKEN;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceNotExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;

@QuarkusTestResource(TokenExchangeExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
@QuarkusIntegrationTest
class TokenExchangeIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenExchangeIT.class);

    @Test
    void tokenExchange() throws IOException {
        LOGGER.info("Testing token exchange caching behavior - expecting 3 external service calls but only 2 token exchanges");

        // Get the Quarkus log file path (configured in application.properties)
        Path logFile = getQuarkusLogFile();

        // Clear the log file to start fresh
        if (Files.exists(logFile)) {
            Files.write(logFile, new byte[0]); // Clear the file
        }

        // Start a new process instance
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, BASE_AND_PROPAGATED_AUTHORIZATION_TOKEN);

        JsonPath jsonPath = newProcessInstance("/token_exchange", processInput, headers);
        String processInstanceId = jsonPath.getString("id");
        Assertions.assertThat(processInstanceId).isNotBlank();

        // Wait for the process to complete - it should take approximately 11+ seconds
        // due to the 1s delay + 10s delay in the workflow
        long startTime = System.currentTimeMillis();
        ProcessAwaitUtils.waitForProcessCompletion("token_exchange", processInstanceId, Duration.ofSeconds(25));
        long endTime = System.currentTimeMillis();

        LOGGER.info("Process completed in {} seconds", (endTime - startTime) / 1000.0);

        // Verify the process completed successfully (404 means it completed and was cleaned up)
        assertProcessInstanceNotExists("/token_exchange/{id}", processInstanceId);

        // Verify caching behavior by checking WireMock requests
        validateCachingBehavior();
        validateOAuth2LogsFromFile(logFile);
    }

    private void validateCachingBehavior() {
        List<LoggedRequest> externalServiceRequests = TokenExchangeExternalServicesMock.getInstance().findAll(
                WireMock.postRequestedFor(WireMock.urlEqualTo("/token-exchange-external-service/withExchange")));

        // Should have exactly 3 external service requests (all 3 calls to executeQueryWithExchange)
        Assertions.assertThat(externalServiceRequests)
                .as("Should have exactly 3 external service requests - one for each executeQueryWithExchange call")
                .hasSize(3);

        // Verify that all external service requests used the correct exchanged token
        for (LoggedRequest request : externalServiceRequests) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            Assertions.assertThat(authHeader)
                    .as("All external service requests should use the exchanged token")
                    .isEqualTo("Bearer KEYCLOAK_EXCHANGED_ACCESS_TOKEN");
        }
    }

    /**
     * Get the path to the Quarkus log file
     */
    private Path getQuarkusLogFile() {
        // The log file path is configured in application.properties as quarkus.log.file.path
        // For integration tests, Quarkus uses target/quarkus.log
        String logPath = System.getProperty("quarkus.log.file.path", "target/quarkus.log");
        return Paths.get(logPath);
    }

    /**
     * Validate OAuth2 token exchange and caching behavior from log file
     */
    private void validateOAuth2LogsFromFile(Path logFile) throws IOException {
        List<String> logLines = Files.readAllLines(logFile);
        Assertions.assertThat(logLines).hasSizeGreaterThan(0);

        LOGGER.info("Analyzing {} log lines for OAuth2 token exchange patterns", logLines.size());

        List<String> usedInMemoryRepository = logLines.stream().filter(line -> line.contains(LOG_PREFIX_USED_REPOSITORY + ": InMemoryTokenCacheRepository")).toList();
        Assertions.assertThat(usedInMemoryRepository).hasSize(1);

        LOGGER.info("InMemory repository was used as expected");

        List<String> startTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_STARTING_TOKEN_EXCHANGE)).toList();
        List<String> completedTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_COMPLETED_TOKEN_EXCHANGE)).toList();
        List<String> failedTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_FAILED_TOKEN_EXCHANGE)).toList();
        List<String> refreshTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_TOKEN_REFRESH)).toList();
        List<String> completedRefreshTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_REFRESH_COMPLETED)).toList();
        List<String> failedRefreshTokenExchangeLogLines = logLines.stream().filter(line -> line.contains(LOG_PREFIX_FAILED_TO_REFRESH_TOKEN)).toList();

        Assertions.assertThat(startTokenExchangeLogLines).hasSize(1);
        Assertions.assertThat(completedTokenExchangeLogLines).hasSize(1);
        Assertions.assertThat(failedTokenExchangeLogLines).hasSize(0);

        Assertions.assertThat(refreshTokenExchangeLogLines).hasSizeBetween(2, 4);
        Assertions.assertThat(completedRefreshTokenExchangeLogLines).hasSizeBetween(2, 4);
        Assertions.assertThat(failedRefreshTokenExchangeLogLines).hasSize(0);

        // Log what we found for debugging
        LOGGER.info("Token exchange analysis results:");
        LOGGER.info("  - Starting token exchange: {} times", startTokenExchangeLogLines.size());
        LOGGER.info("  - Completed token exchange: {} times", completedTokenExchangeLogLines.size());
        LOGGER.info("  - Token refresh: {} times", refreshTokenExchangeLogLines.size());
    }

    protected static String buildProcessInput(String query) {
        return "{\"workflowdata\": {\"query\": \"" + query + "\"} }";
    }

}
