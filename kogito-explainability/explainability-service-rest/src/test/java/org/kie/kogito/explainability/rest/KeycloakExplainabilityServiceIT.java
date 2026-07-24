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
package org.kie.kogito.explainability.rest;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Disabled;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.testcontainers.KogitoKeycloakContainer;
import org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

/*@QuarkusTest
@QuarkusTestResource(KeycloakQuarkusTestResource.Conditional.class)*/
@Disabled("Currently disabled due to failures on test instantiation. For some reason, the KogitoKeycloakContainer" +
        ".getMappedPort() is invoked before" +
        "the container is up and running, hence it throws an IllegalStateException. This needs to be investigated and" +
        " fixed before re-enabling the test.")
class KeycloakExplainabilityServiceIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String VALID_USER = "jdoe";
    private static final String SERVICE_ENDPOINT = "/q/health/live";

    @QuarkusTestProperty(name = KeycloakQuarkusTestResource.KOGITO_KEYCLOAK_PROPERTY)
    String keycloakURL;

    /*
     * @Test
     * 
     * @Disabled("Currently disabled due to failures on test instantiation.")
     */
    void shouldReturnUnauthorized() {
        given().get(SERVICE_ENDPOINT)
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    /*
     * @Test
     * 
     * @Disabled("Currently disabled due to failures on test instantiation.")
     */
    void shouldReturnOkWhenValidUser() {
        given().auth().oauth2(getAccessToken(VALID_USER)).get(SERVICE_ENDPOINT)
                .then().statusCode(HttpStatus.SC_OK);
    }

    private String getAccessToken(String userName) {
        return given().param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", KogitoKeycloakContainer.CLIENT_ID)
                .param("client_secret", KogitoKeycloakContainer.CLIENT_SECRET)
                .when()
                .post(keycloakURL + "/protocol/openid-connect/token")
                .jsonPath().get("access_token");
    }
}
