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
package org.kie.kogito.index.postgresql.model;

import java.util.Map;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity(name = "definitions_nodes")
@Table(name = "definitions_nodes")
@IdClass(NodeEntityId.class)
public class NodeEntity extends AbstractEntity {

    @Id
    private String id;
    private String name;
    private String uniqueId;
    private String type;

    @ElementCollection
    @JoinColumn(name = "node_id")
    @CollectionTable(name = "definitions_nodes_metadata", joinColumns = { @JoinColumn(name = "node_id", referencedColumnName = "id"),
            @JoinColumn(name = "process_id", referencedColumnName = "process_id"), @JoinColumn(name = "process_version", referencedColumnName = "process_version") },
            foreignKey = @ForeignKey(name = "fk_definitions_nodes_metadata_definitions_nodes"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Map<String, String> metadata;

    @Id
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumns(value = { @JoinColumn(name = "process_id"), @JoinColumn(name = "process_version") }, foreignKey = @ForeignKey(name = "fk_definitions_nodes_definitions"))
    private ProcessDefinitionEntity processDefinition;

    @Override
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

    public ProcessDefinitionEntity getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinitionEntity processDefinition) {
        this.processDefinition = processDefinition;
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
                ", uniqueId='" + uniqueId + '\'' +
                ", type='" + type + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
