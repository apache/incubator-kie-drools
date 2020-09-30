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

package org.kie.kogito.integrationtests.springboot.infinispan;

import org.springframework.beans.factory.annotation.Autowired;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.kie.kogito.integrationtests.springboot.BaseRestTest;
import org.kie.kogito.integrationtests.springboot.KogitoSpringbootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.class)
class InfinispanTest extends BaseRestTest {

    @Autowired
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
