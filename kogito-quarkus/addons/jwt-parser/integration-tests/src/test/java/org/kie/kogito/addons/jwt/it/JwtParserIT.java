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
package org.kie.kogito.addons.jwt.it;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.path.json.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.assertProcessInstanceNotExists;
import static org.kie.kogito.test.utils.ProcessInstancesRESTTestUtils.newProcessInstance;

/**
 * Integration tests for JWT Parser functionality
 * Tests the complete workflow with JWT token parsing as requested in Issue #1899
 */
@QuarkusIntegrationTest
class JwtParserIT {

    // Valid JWT token for testing (contains: {"sub":"1234567890","preferred_username":"johndoe","email":"johndoe@example.com","iat":1516239022})
    private static final String VALID_JWT_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiam9obmRvZSIsImVtYWlsIjoiam9obmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    /**
     * End-to-end test that verifies the JWT parser works within a complete SonataFlow workflow.
     * This test demonstrates the feature working as requested in Issue #1899.
     * 
     * The workflow is one-shot (executes without intermediate stops), so results are immediately
     * available in the initial response. After completion, the process instance is removed from storage.
     */
    @Test
    void testJwtParserWorkflowEndToEnd() {
        // Prepare workflow input
        String processInput = "{}";

        // Set up headers with JWT token
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Authorization-acme_financial_auth", VALID_JWT_TOKEN);

        // Start the workflow - for one-shot workflows, results are immediately available
        JsonPath jsonPath = newProcessInstance("/jwt_example", processInput, headers);
        String processInstanceId = jsonPath.getString("id");
        assertThat(processInstanceId).isNotBlank();

        // Verify that the JWT was parsed and user information was extracted
        // The workflow data is available in the initial response for one-shot workflows
        String username = jsonPath.getString("workflowdata.preferred_username");
        assertThat(username).isEqualTo("johndoe"); // The preferred_username from the JWT

        String message = jsonPath.getString("workflowdata.message");
        assertThat(message).isEqualTo("Congrats johndoe! Your loan has been approved!");

        Boolean loanApproved = jsonPath.getBoolean("workflowdata.loanApproved");
        assertThat(loanApproved).isTrue();

        // Verify the process completed and was removed from storage (following TokenExchangeIT pattern)
        assertProcessInstanceNotExists("/jwt_example/{id}", processInstanceId);
    }

    /**
     * Test workflow with Bearer prefix in JWT token
     */
    @Test
    void testJwtParserWithBearerPrefix() {
        String processInput = "{}";
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Authorization-acme_financial_auth", "Bearer " + VALID_JWT_TOKEN);

        JsonPath jsonPath = newProcessInstance("/jwt_example", processInput, headers);
        String processInstanceId = jsonPath.getString("id");
        assertThat(processInstanceId).isNotBlank();

        // Verify the JWT was parsed correctly despite the Bearer prefix
        String username = jsonPath.getString("workflowdata.preferred_username");
        assertThat(username).isEqualTo("johndoe");

        String message = jsonPath.getString("workflowdata.message");
        assertThat(message).contains("johndoe");

        // Verify process was cleaned up
        assertProcessInstanceNotExists("/jwt_example/{id}", processInstanceId);
    }
}
