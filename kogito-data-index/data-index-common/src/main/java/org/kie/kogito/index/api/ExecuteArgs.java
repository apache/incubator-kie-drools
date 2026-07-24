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
package org.kie.kogito.index.api;

import com.fasterxml.jackson.databind.JsonNode;

public record ExecuteArgs(JsonNode input, String businessKey, String referenceId) {

    public static ExecuteArgs of(JsonNode input) {
        return builder().withInput(input).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private JsonNode input;
        private String businessKey;
        private String referenceId;

        private Builder() {
        }

        public Builder withInput(JsonNode input) {
            this.input = input;
            return this;
        }

        public Builder withBusinessKey(String businessKey) {
            this.businessKey = businessKey;
            return this;
        }

        public Builder withReferenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public ExecuteArgs build() {
            return new ExecuteArgs(input, businessKey, referenceId);
        }
    }
}
