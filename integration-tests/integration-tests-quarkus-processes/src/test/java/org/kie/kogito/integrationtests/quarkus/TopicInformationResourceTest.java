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

package org.kie.kogito.integrationtests.quarkus;

import java.util.Arrays;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.Topic;
import org.kie.kogito.event.TopicType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class TopicInformationResourceTest {

    @Test
    void verifyTopicsInformation() {
        final List<Topic> topics = Arrays.asList(given().get("/messaging/topics").as(Topic[].class));
        assertThat(topics, notNullValue());
        assertThat(topics, hasItem(new Topic("pong_send_start", TopicType.PRODUCED)));
        assertThat(topics, hasItem(new Topic("pong_receive_end", TopicType.CONSUMED)));
    }

}
