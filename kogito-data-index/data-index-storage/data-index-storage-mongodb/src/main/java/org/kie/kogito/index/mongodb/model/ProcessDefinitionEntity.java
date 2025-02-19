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
package org.kie.kogito.index.mongodb.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bson.codecs.pojo.annotations.BsonId;

public class ProcessDefinitionEntity {

    @BsonId
    private String key;

    private String id;

    private String version;
    private String name;

    private String description;
    private String type;

    private Set<String> annotations;

    private Map<String, Object> metadata;

    private Set<String> roles;

    private Set<String> addons;

    private String endpoint;

    private byte[] source;

    private List<NodeEntity> nodes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAddons(Set<String> addons) {
        this.addons = addons;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getAddons() {
        return addons;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public List<NodeEntity> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeEntity> nodes) {
        this.nodes = nodes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProcessDefinitionEntity that = (ProcessDefinitionEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }

    @Override
    public String toString() {
        return "ProcessDefinitionEntity{" +
                "key='" + key + '\'' +
                ", id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", roles=" + roles +
                ", addons=" + addons +
                ", endpoint='" + endpoint + '\'' +
                ", nodes='" + nodes + '\'' +
                '}';
    }

    public static class NodeEntity {

        private String id;
        private String name;
        private String uniqueId;
        private String type;

        private Map<String, String> metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String nodeId) {
            this.uniqueId = nodeId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NodeEntity that = (NodeEntity) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "NodeEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", nodeId='" + uniqueId + '\'' +
                    ", type='" + type + '\'' +
                    ", metadata=" + metadata +
                    '}';
        }
    }
}
