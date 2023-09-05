/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.oracle.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "definitions")
@Table(name = "definitions")
@IdClass(ProcessDefinitionEntityId.class)
public class ProcessDefinitionEntity extends AbstractEntity {

    @Id
    private String id;

    @Id
    private String version;
    private String name;
    private String type;

    private String endpoint;

    private byte[] source;

    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_roles", joinColumns = { @JoinColumn(name = "process_id"),
            @JoinColumn(name = "process_version") }, foreignKey = @ForeignKey(name = "fk_definitions_roles_definitions"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(name = "role", nullable = false)
    private Set<String> roles;

    @ElementCollection
    @JoinColumn(name = "id")
    @CollectionTable(name = "definitions_addons", joinColumns = { @JoinColumn(name = "process_id"),
            @JoinColumn(name = "process_version") }, foreignKey = @ForeignKey(name = "fk_definitions_addons_definitions"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Column(name = "addon", nullable = false)
    private Set<String> addons;

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
                ", endpoint='" + endpoint + '\'' +
                ", roles=" + roles +
                ", addons=" + addons +
                '}';
    }
}
