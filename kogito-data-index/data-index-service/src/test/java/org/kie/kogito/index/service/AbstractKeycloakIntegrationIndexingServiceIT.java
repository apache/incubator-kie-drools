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

package org.kie.kogito.index.service;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractKeycloakIntegrationIndexingServiceIT {

    private static final String KEYCLOAK_SERVER_URL = System.getProperty("quarkus.oidc.auth-server-url", "http://localhost:8281/auth/realms/kogito");
    private static final String KEYCLOAK_CLIENT_ID = System.getProperty("quarkus.oidc.client-id", "kogito-service");
    private static final String KEYCLOAK_CLIENT_SECRET = System.getProperty("quarkus.oidc.credentials.secret", "secret");

    @Test
    void testUnauthorizedUserAccess() {
        //alice only have role User, resource is forbidden
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .auth().oauth2(getAccessToken("alice"))
                .when().get("/graphql")
                .then()
                .statusCode(403);
    }

    @Test
    void testNoTokenProvided() {
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().get("/graphql")
                .then()
                .statusCode(401);
    }

    @Test
    void testAuthorizedUserProvided() {
        //jdoe has role:user and confidential, resource served
        given().auth().oauth2(getAccessToken("jdoe"))
                .contentType(ContentType.JSON)
                .body("{ \"query\" : \"{ProcessInstances{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200);
    }

    @Test
    void graphiqlLoginOnKeycloakServedTest() {
        // Returning keycloak login page
        given().when().get("/graphiql/")
                .then()
                .statusCode(200);

        given().when().get("/")
                .then()
                .statusCode(200);

        assertIsLoginPage(given().when().get("/graphiql/"));
        assertIsLoginPage(given().when().get("/"));
        assertIsNotLoginPage(given().when().get("/other"));
        assertIsNotLoginPage(given().when().get("/graphql"));
    }

    private void assertIsLoginPage(Response response) {
        assertThat(response.andReturn().body().asString()).contains("<title>Log in to kogito</title>");
    }

    private void assertIsNotLoginPage(Response response) {
        assertThat(response.andReturn().body().asString()).doesNotContain("<title>Log in to kogito</title>");
    }

    private String getAccessToken(String userName) {
        return given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", userName)
                .param("client_id", KEYCLOAK_CLIENT_ID)
                .param("client_secret", KEYCLOAK_CLIENT_SECRET)
                .when()
                .post(KEYCLOAK_SERVER_URL + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }
}