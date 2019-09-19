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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.index.query.UserTaskInstanceFilter;

public class UserTaskInstanceFilterMapper implements Function<Map<String, Object>, UserTaskInstanceFilter> {

    @Override
    public UserTaskInstanceFilter apply(Map<String, Object> params) {
        UserTaskInstanceFilter filter = new UserTaskInstanceFilter();
        if (params != null) {
            params.computeIfPresent("state", (key, value) -> {
                filter.setState((List<String>) value);
                return value;
            });

            params.computeIfPresent("processInstanceId", (key, value) -> {
                filter.setProcessInstanceId((List<String>) value);
                return value;
            });

            params.computeIfPresent("id", (key, value) -> {
                filter.setId((List<String>) value);
                return value;
            });

            params.computeIfPresent("actualOwner", (key, value) -> {
                filter.setActualOwner((List<String>) value);
                return value;
            });

            params.computeIfPresent("potentialUsers", (key, value) -> {
                filter.setPotentialUsers((List<String>) value);
                return value;
            });

            params.computeIfPresent("potentialGroups", (key, value) -> {
                filter.setPotentialGroups((List<String>) value);
                return value;
            });

            params.computeIfPresent("limit", (key, value) -> {
                filter.setLimit((Integer) value);
                return value;
            });

            params.computeIfPresent("offset", (key, value) -> {
                filter.setOffset((Integer) value);
                return value;
            });
        }
        return filter;
    }
}
