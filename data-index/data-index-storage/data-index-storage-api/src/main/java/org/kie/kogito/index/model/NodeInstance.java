/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.model;

import java.time.ZonedDateTime;

import javax.json.bind.annotation.JsonbProperty;

public class NodeInstance {

    private String id;
    @JsonbProperty("nodeName")
    private String name;
    private String nodeId;
    @JsonbProperty("nodeType")
    private String type;
    @JsonbProperty("triggerTime")
    private ZonedDateTime enter;
    @JsonbProperty("leaveTime")
    private ZonedDateTime exit;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                '}';
    }
}
