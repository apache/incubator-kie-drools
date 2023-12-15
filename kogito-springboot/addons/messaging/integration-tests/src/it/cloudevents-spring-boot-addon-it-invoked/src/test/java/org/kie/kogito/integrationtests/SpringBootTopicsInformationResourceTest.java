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
package org.kie.kogito.integrationtests;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.KogitoSpringbootApplication;
import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.Topic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
public class SpringBootTopicsInformationResourceTest {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void test() {
        RestAssured.port = randomServerPort;
        List<Topic> topicList = given()
                .when()
                .get("/messaging/topics")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", Topic.class);

        assertThat(topicList).hasSize(2).containsExactly(
                new Topic("cloudevents-addon-it-requests", ChannelType.INCOMING),
                new Topic("cloudevents-addon-it-responses", ChannelType.OUTGOING));
    }
}
