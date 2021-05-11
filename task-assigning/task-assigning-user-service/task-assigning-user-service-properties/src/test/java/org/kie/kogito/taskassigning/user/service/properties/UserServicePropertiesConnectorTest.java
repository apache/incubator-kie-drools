/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.user.service.properties;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.user.service.Group;
import org.kie.kogito.taskassigning.user.service.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.AFFINITIES_ATTRIBUTE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.NAME_ATTRIBUTE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.SKILLS_ATTRIBUTE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.SURNAME_ATTRIBUTE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER1;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER1_AFFINITIES_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER1_NAME_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER1_SKILLS_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER1_SURNAME_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER2;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER2_SKILLS_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER2_SURNAME_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER2_ZIPCODE_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER3;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER3_NAME_VALUE;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER4;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER5;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.USER6;
import static org.kie.kogito.taskassigning.user.service.properties.DefLinesReaderTest.ZIPCODE_ATTRIBUTE;

class UserServicePropertiesConnectorTest {

    private static final String USER1_GROUP1 = "user1_group1";
    private static final String USER1_GROUP2 = "user1_group2";
    private static final String USER1_GROUP3 = "user1_group3";

    private static final String USER2_GROUP1 = "user2_group1";

    private static final String USER4_GROUP1 = "user4_group1";
    private static final String USER4_GROUP2 = "user4_group2";

    private UserServicePropertiesConnector connector;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    @BeforeEach
    void setUp() throws IOException {
        connector = new UserServicePropertiesConnector(DefLinesReaderTest.readTestConfig());

        Map<String, Object> user1Attributes = new HashMap<>();
        user1Attributes.put(SKILLS_ATTRIBUTE, USER1_SKILLS_VALUE);
        user1Attributes.put(AFFINITIES_ATTRIBUTE, USER1_AFFINITIES_VALUE);
        user1Attributes.put(NAME_ATTRIBUTE, USER1_NAME_VALUE);
        user1Attributes.put(SURNAME_ATTRIBUTE, USER1_SURNAME_VALUE);

        user1 = createUser(USER1, user1Attributes, USER1_GROUP1, USER1_GROUP2, USER1_GROUP3);

        Map<String, Object> user2Attributes = new HashMap<>();
        user2Attributes.put(SKILLS_ATTRIBUTE, USER2_SKILLS_VALUE);
        user2Attributes.put(SURNAME_ATTRIBUTE, USER2_SURNAME_VALUE);
        user2Attributes.put(ZIPCODE_ATTRIBUTE, USER2_ZIPCODE_VALUE);

        user2 = createUser(USER2, user2Attributes, USER2_GROUP1);

        Map<String, Object> user3Attributes = new HashMap<>();
        user3Attributes.put(NAME_ATTRIBUTE, USER3_NAME_VALUE);

        user3 = createUser(USER3, user3Attributes);

        user4 = createUser(USER4, Collections.emptyMap(), USER4_GROUP1, USER4_GROUP2);

        user5 = createUser(USER5, Collections.emptyMap());

        Map<String, Object> user6Attributes = new HashMap<>();
        user6Attributes.put(NAME_ATTRIBUTE, null);

        user6 = createUser(USER6, user6Attributes);
    }

    @Test
    void start() {
        assertThat(connector.findAllUsers()).isEmpty();
        connector.start();
        assertThat(connector.findAllUsers())
                .containsExactlyInAnyOrder(user1, user2, user3, user4, user5, user6);
    }

    @Test
    void findUser() {
        connector.start();
        assertThat(connector.findUser(USER1)).isEqualTo(user1);
    }

    private static User createUser(String id, Map<String, Object> attributes, String... groups) {
        Set<Group> groupsSet = Arrays.stream(groups).map(GroupImpl::new).collect(Collectors.toSet());
        return new UserImpl(id, groupsSet, attributes);
    }
}