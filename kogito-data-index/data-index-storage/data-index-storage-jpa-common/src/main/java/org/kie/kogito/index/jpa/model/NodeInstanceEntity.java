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

import java.time.ZonedDateTime;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "nodes")
@Table(name = "nodes")
public class NodeInstanceEntity extends AbstractEntity {

    @Id
    private String id;
    private String name;
    private String nodeId;
    private String type;
    private ZonedDateTime enter;
    private ZonedDateTime exit;
    private String definitionId;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "processInstanceId", foreignKey = @ForeignKey(name = "fk_nodes_process"))
    private ProcessInstanceEntity processInstance;

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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public ZonedDateTime getEnter() {
        return enter;
    }

    public void setEnter(ZonedDateTime enter) {
        this.enter = enter;
    }

    public ZonedDateTime getExit() {
        return exit;
    }

    public void setExit(ZonedDateTime exit) {
        this.exit = exit;
    }

    public ProcessInstanceEntity getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstanceEntity processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeInstanceEntity that = (NodeInstanceEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
