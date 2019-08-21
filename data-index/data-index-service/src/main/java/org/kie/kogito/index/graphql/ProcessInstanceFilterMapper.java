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

import org.kie.kogito.index.query.ProcessInstanceFilter;

public class ProcessInstanceFilterMapper implements Function<Map<String, Object>, ProcessInstanceFilter> {

    @Override
    public ProcessInstanceFilter apply(Map<String, Object> params) {
        ProcessInstanceFilter filter = new ProcessInstanceFilter();
        if (params != null) {
            params.computeIfPresent("state", (key, value) -> {
                filter.setState((List<Integer>) value);
                return value;
            });

            params.computeIfPresent("processId", (key, value) -> {
                filter.setProcessId((List<String>) value);
                return value;
            });

            params.computeIfPresent("id", (key, value) -> {
                filter.setId((List<String>) value);
                return value;
            });

            params.computeIfPresent("limit", (key, value) -> {
                filter.setLimit((Integer) value);
                return value;
            });

            params.computeIfPresent("offset", (key, value) -> {
                filter.setLimit((Integer) value);
                return value;
            });
        }
        return filter;
    }
}
