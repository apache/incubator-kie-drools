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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "definitions")
@Table(name = "definitions")
@IdClass(ProcessDefinitionEntityId.class)
public class ProcessDefinitionEntity extends AbstractEntity {

    @Id
    private String id;

    @Id
    private String version;
    private String name;
    private String description;
    private String type;
    private byte[] source;

    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_roles", joinColumns = { @JoinColumn(name = "process_id"),
            @JoinColumn(name = "process_version") }, foreignKey = @ForeignKey(name = "fk_definitions_roles_definitions"))
    @Column(name = "role", nullable = false)
    private Set<String> roles;

    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_addons", joinColumns = { @JoinColumn(name = "process_id"),
            @JoinColumn(name = "process_version") }, foreignKey = @ForeignKey(name = "fk_definitions_addons_definitions"))
    @Column(name = "addon", nullable = false)
    private Set<String> addons;

    private String endpoint;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processDefinition")
    private List<NodeEntity> nodes;
    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_annotations", joinColumns = { @JoinColumn(name = "process_id", referencedColumnName = "id"),
            @JoinColumn(name = "process_version", referencedColumnName = "version") }, foreignKey = @ForeignKey(name = "fk_definitions_annotations"))
    @Column(name = "value")
    private Set<String> annotations;
    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_metadata", joinColumns = {
            @JoinColumn(name = "process_id", referencedColumnName = "id"), @JoinColumn(name = "process_version", referencedColumnName = "version") },
            foreignKey = @ForeignKey(name = "fk_definitions_metadata"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;

    @Override
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
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
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", roles=" + roles +
                ", addons=" + addons +
                ", endpoint='" + endpoint + '\'' +
                ", nodes='" + nodes + '\'' +
                '}';
    }
}
