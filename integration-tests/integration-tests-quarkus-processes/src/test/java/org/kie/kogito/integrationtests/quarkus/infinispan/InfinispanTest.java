/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.integrationtests.quarkus.infinispan;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.class)
class InfinispanTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Inject
    RemoteCacheManager cacheManager;

    @Test
    void testPersistence() {
        String processId = "greetings";
        String cacheName = processId + "_store";
        
        String pid = given().contentType(ContentType.JSON)
                .when()
                    .post("/greetings")
                .then()
                    .statusCode(201)
                    .body("id", not(emptyOrNullString()))
                    .body("test", nullValue())
                .extract()
                    .path("id");

        assertThat(cacheManager.getCacheNames()).contains(cacheName);
        assertThat(cacheManager.getCache(cacheName).containsKey(pid)).isTrue();

        given().contentType(ContentType.JSON)
                .when()
                    .delete("/management/processes/{processId}/instances/{processInstanceId}", processId, pid)
                .then()
                .statusCode(200);

        assertThat(cacheManager.getCache(cacheName).containsKey(pid)).isFalse();
    }
}
