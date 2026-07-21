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
package org.kie.kogito.event.process;

import java.util.Map;

public class NodeDefinition {
    private String id;
    private String name;
    private String type;
    private String uniqueId;
    private Map<String, ?> metadata;

    public NodeDefinition() {
    }

    public NodeDefinition(String id, String name, String type, String uniqueId, Map<String, ?> metadata) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uniqueId = uniqueId;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Map<String, ?> getMetadata() {
        return metadata;
    }

    public static NodeDefinitionEventBodyBuilder builder() {
        return new NodeDefinitionEventBodyBuilder();
    }

    public static class NodeDefinitionEventBodyBuilder {
        private String id;
        private String name;
        private String type;
        private String uniqueId;
        private Map<String, ?> metadata;

        public NodeDefinitionEventBodyBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public NodeDefinitionEventBodyBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public NodeDefinitionEventBodyBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public NodeDefinitionEventBodyBuilder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public NodeDefinitionEventBodyBuilder setMetadata(Map<String, ?> metadata) {
            this.metadata = metadata;
            return this;
        }

        public NodeDefinition build() {
            return new NodeDefinition(id, name, type, uniqueId, metadata);
        }
    }
}
