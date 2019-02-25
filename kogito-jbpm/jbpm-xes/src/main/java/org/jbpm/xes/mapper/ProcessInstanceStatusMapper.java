/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes.mapper;

import java.util.function.Function;

import static org.kie.api.runtime.process.ProcessInstance.*;

public class ProcessInstanceStatusMapper implements Function<Integer, String> {

    @Override
    public String apply(Integer status) {
        if (status == null) {
            return "Unknown";
        }
        switch (status) {
            case STATE_PENDING:
                return "pending";
            case STATE_ACTIVE:
                return "active";
            case STATE_COMPLETED:
                return "completed";
            case STATE_ABORTED:
                return "aborted";
            case STATE_SUSPENDED:
                return "suspended";
            default:
                return "Unknown";
        }
    }
}
