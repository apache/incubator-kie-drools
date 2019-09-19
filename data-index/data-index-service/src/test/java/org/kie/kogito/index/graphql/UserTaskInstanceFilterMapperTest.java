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

package org.kie.kogito.index.graphql;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singletonList;

public class UserTaskInstanceFilterMapperTest {

    @Test
    public void testUserTaskInstanceFilterMapper() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", singletonList("InProgress"));
        params.put("processInstanceId", singletonList("f78fb147-ec22-4478-a592-3063add9f956"));
        params.put("id", singletonList("228d5922-5e88-4bfa-8329-7116a5cbe58b"));
        params.put("actualOwner", singletonList("kogito"));
        params.put("potentialUsers", singletonList("potentialUser"));
        params.put("potentialGroups", singletonList("potentialGroup"));
        params.put("limit", 1);
        params.put("offset", 10);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(new UserTaskInstanceFilterMapper().apply(params))
                .hasFieldOrPropertyWithValue("state", singletonList("InProgress"))
                .hasFieldOrPropertyWithValue("processInstanceId", singletonList("f78fb147-ec22-4478-a592-3063add9f956"))
                .hasFieldOrPropertyWithValue("id", singletonList("228d5922-5e88-4bfa-8329-7116a5cbe58b"))
                .hasFieldOrPropertyWithValue("actualOwner", singletonList("kogito"))
                .hasFieldOrPropertyWithValue("potentialUsers", singletonList("potentialUser"))
                .hasFieldOrPropertyWithValue("potentialGroups", singletonList("potentialGroup"))
                .hasFieldOrPropertyWithValue("limit", 1)
                .hasFieldOrPropertyWithValue("offset", 10);

        softly.assertAll();
    }
}
