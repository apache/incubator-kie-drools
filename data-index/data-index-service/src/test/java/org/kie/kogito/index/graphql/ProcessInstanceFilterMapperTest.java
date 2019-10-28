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

public class ProcessInstanceFilterMapperTest {

    @Test
    public void testProcessInstanceFilterMapper() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", singletonList(1));
        params.put("processId", singletonList("processId"));
        params.put("id", singletonList("228d5922-5e88-4bfa-8329-7116a5cbe58b"));
        params.put("limit", 1);
        params.put("offset", 10);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(new ProcessInstanceFilterMapper().apply(params))
                .hasFieldOrPropertyWithValue("state", singletonList(1))
                .hasFieldOrPropertyWithValue("processId", singletonList("processId"))
                .hasFieldOrPropertyWithValue("id", singletonList("228d5922-5e88-4bfa-8329-7116a5cbe58b"))
                .hasFieldOrPropertyWithValue("limit", 1)
                .hasFieldOrPropertyWithValue("offset", 10);

        softly.assertAll();
    }

    @Test
    public void testProcessInstanceFilterWithNullValuesMapper() {
        Map<String, Object> params = new HashMap<>();
        params.put("parentProcessInstanceId", singletonList(null));
        params.put("rootProcessInstanceId", singletonList(null));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(new ProcessInstanceFilterMapper().apply(params))
                .hasFieldOrPropertyWithValue("parentProcessInstanceId", singletonList(null))
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", singletonList(null));

        softly.assertAll();
    }
}
