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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.user.service.Group;
import org.kie.kogito.taskassigning.user.service.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserImplTest {

    private static final String USER_ID = "USER_ID";
    private static final String GROUP_ID = "GROUP_ID";
    private static final String ATTRIBUTE_NAME = "ATTRIBUTE_NAME";
    private static final String ATTRIBUTE_VALUE = "ATTRIBUTE_VALUE";

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    void getId() {
        assertThat(user.getId()).isEqualTo(USER_ID);
    }

    @Test
    void getAttributes() {
        Map<String, Object> expectedAttributes = new HashMap<>();
        expectedAttributes.put(ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
        assertThat(user.getAttributes()).isEqualTo(expectedAttributes);
    }

    @Test
    void getGroups() {
        Set<Group> expectedGroups = Collections.singleton(new GroupImpl(GROUP_ID));
        assertThat(user.getGroups()).isEqualTo(expectedGroups);
    }

    @Test
    void equals() {
        assertThat(user).isEqualTo(createUser());
    }

    @Test
    void hashCodeTest() {
        assertThat(user).hasSameHashCodeAs(createUser());
    }

    private static User createUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTRIBUTE_NAME, ATTRIBUTE_VALUE);
        return new UserImpl(USER_ID, Collections.singleton(new GroupImpl(GROUP_ID)), attributes);
    }
}
