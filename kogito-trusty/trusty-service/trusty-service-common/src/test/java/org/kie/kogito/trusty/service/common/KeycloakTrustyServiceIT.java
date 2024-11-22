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
package org.kie.kogito.trusty.service.common;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.testcontainers.KogitoKeycloakContainer;
import org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource;
import org.kie.kogito.trusty.service.common.models.MatchedExecutionHeaders;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

import static io.restassured.RestAssured.given;
import static org.kie.kogito.testcontainers.quarkus.KeycloakQuarkusTestResource.KOGITO_OIDC_TENANTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@QuarkusTestResource(value = KeycloakQuarkusTestResource.class, initArgs = { @ResourceArg(name = KOGITO_OIDC_TENANTS, value = "web-app-tenant") })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeycloakTrustyServiceIT {

    private static final String VALID_USER = "jdoe";
    private static final String TRUSTY_ENDPOINT = "/executions";

    @QuarkusTestProperty(name = KeycloakQuarkusTestResource.KOGITO_KEYCLOAK_PROPERTY)
    String keycloakURL;

    @InjectMock
    TrustyService trustyService;

    @Test
    void shouldReturnUnauthorized() {
        given().get(TRUSTY_ENDPOINT)
                .then().statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturnOkWhenValidUser() {
        when(trustyService.getExecutionHeaders(any(OffsetDateTime.class), any(OffsetDateTime.class), anyInt(), anyInt(), anyString())).thenReturn(new MatchedExecutionHeaders(new ArrayList<>(), 0));
        given().auth().oauth2(getAccessToken(VALID_USER)).get(TRUSTY_ENDPOINT)
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
