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

package org.kie.kogito.integrationtests.springboot;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.kie.kogito.event.ChannelType;
import org.kie.kogito.event.Topic;
import org.kie.kogito.testcontainers.springboot.KafkaSpringBootTestResource;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers =  { KafkaSpringBootTestResource.class , InfinispanSpringBootTestResource.Conditional.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TopicInformationResourceTest extends BaseRestTest{

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicInformationResourceTest.class);

    @Test
    void verifyTopicsInformation() {
        final List<Topic> topics = Arrays.asList(given().get("/messaging/topics").as(Topic[].class));
        LOGGER.info("Topics registered in the service are {}", topics);
        assertThat(topics).isNotEmpty();
        assertThat(topics.stream().anyMatch(t -> t.getType() == ChannelType.INCOMING && t.getName().equals("pingpong"))).isTrue();
    }
}
