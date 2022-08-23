/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.user;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.config.UserConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserInfoSupplierTest {

    @Test
    void testNullGetUserInfo() {
        final UserInfoSupplier userInfoSupplier = new UserInfoSupplier(null);
        final UserInfo userInfo = userInfoSupplier.get();

        assertEquals(0, userInfo.getUsers().size());
        assertEquals("[  ]", userInfo.getArrayRepresentation());
    }

    @Test
    void testEmptyGetUserInfo() {
        final UserInfoSupplier userInfoSupplier = new UserInfoSupplier(Collections.emptyMap());
        final UserInfo userInfo = userInfoSupplier.get();

        assertEquals(0, userInfo.getUsers().size());
        assertEquals("[  ]", userInfo.getArrayRepresentation());
    }

    @Test
    void testNotEmptyGetUserInfo() {
        final UserConfig userA = new UserConfig();
        userA.groups = Collections.singletonList("admin");
        final UserConfig userB = new UserConfig();
        userB.groups = Arrays.asList("admin", "user");

        final Map<String, UserConfig> userConfigByUser = new HashMap<>();
        userConfigByUser.put("userA", userA);
        userConfigByUser.put("userB", userB);

        final UserInfoSupplier userInfoSupplier = new UserInfoSupplier(userConfigByUser);
        final UserInfo userInfo = userInfoSupplier.get();

        assertEquals(2, userInfo.getUsers().size());
        assertEquals("[ { id: 'userA', groups: ['admin'] }, { id: 'userB', groups: ['admin', 'user'] } ]", userInfo.getArrayRepresentation());
    }
}
