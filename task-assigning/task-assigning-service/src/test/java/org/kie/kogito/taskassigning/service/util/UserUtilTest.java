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

package org.kie.kogito.taskassigning.service.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.model.IdentifiableElement;
import org.kie.kogito.taskassigning.core.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.mockExternalUser;

class UserUtilTest {

    private static final String USER_ID_1 = "USER_ID_1";
    private static final String USER_ID_2 = "USER_ID_2";
    private static final String USER_ID_3 = "USER_ID_3";
    private static final String GROUP_ID_1 = "GROUP_ID_1";
    private static final String GROUP_ID_2 = "GROUP_ID_2";
    private static final String ATTRIBUTE_1_NAME = "ATTRIBUTE_1_NAME";
    private static final String ATTRIBUTE_1_VALUE = "ATTRIBUTE_1_VALUE";
    private static final String ATTRIBUTE_2_NAME = "ATTRIBUTE_2_NAME";
    private static final String ATTRIBUTE_2_VALUE = "ATTRIBUTE_2_VALUE";

    @Test
    void fromExternalUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(ATTRIBUTE_1_NAME, ATTRIBUTE_1_VALUE);
        attributes.put(ATTRIBUTE_2_NAME, ATTRIBUTE_2_VALUE);

        org.kie.kogito.taskassigning.user.service.User externalUser = mockExternalUser(USER_ID_1,
                Arrays.asList(GROUP_ID_1, GROUP_ID_2),
                attributes);
        User user = UserUtil.fromExternalUser(externalUser);
        assertThat(user.getId()).isEqualTo(USER_ID_1);
        assertThat(user.getGroups().stream().map(IdentifiableElement::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder(GROUP_ID_1, GROUP_ID_2);
        assertThat(user.getAttributes()).containsExactlyEntriesOf(attributes);
    }

    @Test
    void filterDuplicates() {
        List<org.kie.kogito.taskassigning.user.service.User> users = Arrays.asList(mockExternalUser(USER_ID_1),
                mockExternalUser(USER_ID_2),
                mockExternalUser(USER_ID_1),
                mockExternalUser(USER_ID_3),
                mockExternalUser(USER_ID_2),
                mockExternalUser(USER_ID_1),
                null);
        List<org.kie.kogito.taskassigning.user.service.User> filteredUsers = UserUtil.filterDuplicates(users).collect(Collectors.toList());
        assertThat(filteredUsers.stream().map(org.kie.kogito.taskassigning.user.service.User::getId).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(Arrays.asList(USER_ID_1, USER_ID_2, USER_ID_3));
    }
}
