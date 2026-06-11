/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask;

import java.util.List;

/**
 * Filter criteria for querying user tasks.
 * All filters are combined using AND logic.
 * Null filters are ignored (no filtering applied for that criterion).
 */
public record UserTaskFilter(
        String processId,
        String processInstanceId,
        List<String> statuses,
        String taskName) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String processId;
        private String processInstanceId;
        private List<String> statuses;
        private String taskName;

        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
            return this;
        }

        public Builder statuses(List<String> statuses) {
            this.statuses = statuses;
            return this;
        }

        public Builder taskName(String taskName) {
            this.taskName = taskName;
            return this;
        }

        public UserTaskFilter build() {
            return new UserTaskFilter(processId, processInstanceId, statuses, taskName);
        }
    }
}
