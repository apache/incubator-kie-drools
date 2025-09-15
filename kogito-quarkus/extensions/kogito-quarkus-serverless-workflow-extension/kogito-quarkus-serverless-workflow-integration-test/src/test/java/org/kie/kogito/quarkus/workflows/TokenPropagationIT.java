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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import jakarta.ws.rs.core.HttpHeaders;

import static org.kie.kogito.quarkus.workflows.ExternalServiceMock.SUCCESSFUL_QUERY;
import static org.kie.kogito.quarkus.workflows.TokenPropagationExternalServicesMock.AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.workflows.TokenPropagationExternalServicesMock.SERVICE3_AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.workflows.TokenPropagationExternalServicesMock.SERVICE3_HEADER_TO_PROPAGATE;
import static org.kie.kogito.quarkus.workflows.TokenPropagationExternalServicesMock.SERVICE4_AUTHORIZATION_TOKEN;
import static org.kie.kogito.quarkus.workflows.TokenPropagationExternalServicesMock.SERVICE4_HEADER_TO_PROPAGATE;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;

@QuarkusTestResource(TokenPropagationExternalServicesMock.class)
@QuarkusTestResource(KeycloakServiceMock.class)
@QuarkusIntegrationTest
class TokenPropagationIT {

    @Test
    void tokenPropagations() throws InterruptedException {
        // start a new process instance by sending the post query and collect the process instance id.
        String processInput = buildProcessInput(SUCCESSFUL_QUERY);
        Map<String, String> headers = new HashMap<>();
        // prepare the headers to pass to the token_propagation SW.
        // service token-propagation-external-service1 and token-propagation-external-service2 will receive the AUTHORIZATION_TOKEN 
        headers.put(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TOKEN);
        // service token-propagation-external-service3 will receive the SERVICE3_AUTHORIZATION_TOKEN
        headers.put(SERVICE3_HEADER_TO_PROPAGATE, SERVICE3_AUTHORIZATION_TOKEN);
        // service token-propagation-external-service4 will receive the SERVICE4_AUTHORIZATION_TOKEN
        headers.put(SERVICE4_HEADER_TO_PROPAGATE, SERVICE4_AUTHORIZATION_TOKEN);

        JsonPath jsonPath = newProcessInstance("/token_propagation", processInput, headers);
        String processInstanceId = jsonPath.getString("id");
        Assertions.assertThat(processInstanceId).isNotBlank();
        ProcessAwaitUtils.waitForProcessCompletion("token_propagation", processInstanceId, Duration.ofSeconds(25));
        validateExternalServiceInvocations();
    }

    protected static String buildProcessInput(String query) {
        return "{\"workflowdata\": {\"query\": \"" + query + "\"} }";
    }

    private void validateExternalServiceInvocations() {
        WireMockServer wm = TokenPropagationExternalServicesMock.getInstance();

        List<TokenExpectation> expectations = List.of(
                new TokenExpectation("/token-propagation-external-service1/executeQuery1", "Bearer " + AUTHORIZATION_TOKEN, 2),
                new TokenExpectation("/token-propagation-external-service2/executeQuery2", "Bearer " + AUTHORIZATION_TOKEN, 2),
                new TokenExpectation("/token-propagation-external-service3/executeQuery3", "Bearer " + SERVICE3_AUTHORIZATION_TOKEN, 2),
                new TokenExpectation("/token-propagation-external-service4/executeQuery4", "Bearer " + SERVICE4_AUTHORIZATION_TOKEN, 2),
                new TokenExpectation("/token-propagation-external-service5/executeQuery5", "Bearer " + KeycloakServiceMock.KEYCLOAK_ACCESS_TOKEN, 2));

        expectations.forEach(e -> assertCalledExactlyWithAuth(wm, e.url(), e.token(), e.expectedCalls()));
    }

    private void assertCalledExactlyWithAuth(WireMockServer wm, String url, String expectedAuthHeader, int expectedCalls) {
        var pattern = WireMock.postRequestedFor(WireMock.urlEqualTo(url));
        var requests = wm.findAll(pattern);
        Assertions.assertThat(requests)
                .as("Expected %s to be called exactly %d times", url, expectedCalls)
                .hasSize(expectedCalls);
        for (LoggedRequest req : requests) {
            Assertions.assertThat(req.getHeader(HttpHeaders.AUTHORIZATION))
                    .as("Expected Authorization header should match for %s but got '%s'",
                            url, req.getHeader(HttpHeaders.AUTHORIZATION))
                    .isEqualTo(expectedAuthHeader);
        }
    }
}
