/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.model;

import java.io.Serializable;

public class TimerDesc implements org.jbpm.services.api.model.TimerDesc, Serializable {

    private static final long serialVersionUID = -5724814793988493958L;

    private long id;
    private long nodeId;
    private String uniqueId;
    private String nodeName;

    public TimerDesc() {
    }

    public TimerDesc(long id, long nodeId, String uniqueId, String nodeName) {
        this.id = id;
        this.nodeId = nodeId;
        this.uniqueId = uniqueId;
        this.nodeName = nodeName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return "TimerDesc{" +
                "id=" + id +
                ", nodeId=" + nodeId +
                ", uniqueId='" + uniqueId + '\'' +
                ", nodeName='" + nodeName + '\'' +
                '}';
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }
}
