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
package org.kie.kogito.index.jpa.model;

/**
 * Describes the entity field paths used for data isolation filtering.
 * Supports fallback mechanism where a primary field can fall back to a secondary field when null.
 *
 * @param processId The primary entity field path for the process ID (e.g., "rootProcessId")
 * @param processVersion The primary entity field path for the process version (e.g., "rootProcessVersion")
 */
public record DataIsolationKeyDescriptor(
        String processId,
        String processVersion,
        String rootProcessId,
        String rootProcessVersion) {

    // Compact constructor for validation
    public DataIsolationKeyDescriptor {
        if (processId == null || processId.isBlank()) {
            throw new IllegalArgumentException("processId path is required and cannot be blank");
        }
    }

    /**
     * Creates a new builder instance for constructing DataIsolationKeyDescriptor.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for DataIsolationKeyDescriptor with fluent API.
     */
    public static class Builder {
        private String processId;
        private String processVersion;
        private String rootProcessId;
        private String rootProcessVersion;

        private Builder() {
        }

        /**
         * Sets the primary process ID field path.
         *
         * @param processId the primary process ID field path
         * @return this builder instance
         */
        public Builder processId(String processId) {
            this.processId = processId;
            return this;
        }

        /**
         * Sets the primary process version field path.
         *
         * @param processVersion the primary process version field path
         * @return this builder instance
         */
        public Builder processVersion(String processVersion) {
            this.processVersion = processVersion;
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            this.rootProcessId = rootProcessId;
            return this;
        }

        public Builder rootProcessVersion(String rootProcessVersion) {
            this.rootProcessVersion = rootProcessVersion;
            return this;
        }

        /**
         * Builds the DataIsolationKeyDescriptor instance.
         *
         * @return a new DataIsolationKeyDescriptor instance
         * @throws IllegalArgumentException if processId is null or blank
         */
        public DataIsolationKeyDescriptor build() {
            return new DataIsolationKeyDescriptor(
                    processId,
                    processVersion,
                    rootProcessId,
                    rootProcessVersion);
        }
    }
}
