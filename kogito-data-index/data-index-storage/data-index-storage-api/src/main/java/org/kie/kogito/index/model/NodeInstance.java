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
package org.kie.kogito.index.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeInstance {

    private String id;
    private String nodeId;

    @JsonProperty("nodeName")
    private String name;

    @JsonProperty("nodeType")
    private String type;

    @JsonProperty("triggerTime")
    private ZonedDateTime enter;
    @JsonProperty("leaveTime")
    private ZonedDateTime exit;

    private ZonedDateTime slaDueDate;

    private Boolean retrigger;

    private String errorMessage;

    private CancelType cancelType;

    public Boolean isRetrigger() {
        return retrigger;
    }

    public void setRetrigger(Boolean retrigger) {
        this.retrigger = retrigger;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonProperty("nodeDefinitionId")
    private String definitionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(ZonedDateTime slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public CancelType getCancelType() {
        return cancelType;
    }

    public void setCancelType(CancelType cancelType) {
        this.cancelType = cancelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeInstance)) {
            return false;
        }

        NodeInstance that = (NodeInstance) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "NodeInstance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nodeId='" + nodeId + '\'' +
                ", type='" + type + '\'' +
                ", enter=" + enter +
                ", exit=" + exit +
                ", slaDueDate=" + slaDueDate +
                ", definitionId='" + definitionId + '\'' +
                '}';
    }
}
